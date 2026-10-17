package org.app.vimate.sites.repository

import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.request.head
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import io.ktor.http.Url
import io.ktor.http.takeFrom
import io.ktor.http.URLBuilder
import org.app.vimate.sharedlogic.yt.YouTube
import org.app.vimate.sites.model.GrabCandidate
import org.app.vimate.sites.model.GrabType
import org.app.vimate.sites.model.ProbeResult

/** Reads real sizes / dimensions / thumbnails of grabber entries (POC Grabber.kt headSize, probeMedia, probeHls). */
interface GrabProbeRepository {
    suspend fun headSize(url: String, referer: String, ua: String?): Long
    suspend fun probeMedia(g: GrabCandidate, ua: String?, referer: String): ProbeResult
    /** HLS: duration/resolution/size/thumb of a master, plus the rendition URLs (without query) it lists. */
    suspend fun probeHls(g: GrabCandidate, ua: String?, referer: String): Pair<ProbeResult, List<String>>
    /** Full bytes of [url] with the page's Referer/UA (preview of hotlink-protected images). */
    suspend fun fetch(url: String, referer: String, ua: String?): ByteArray?
}

class GrabProbeRepositoryImpl : GrabProbeRepository {
    private val http get() = YouTube.http

    private suspend fun quick(url: String, referer: String?, ua: String?, extra: Map<String, String> = emptyMap(), headOnly: Boolean = false): HttpResponse {
        val block: io.ktor.client.request.HttpRequestBuilder.() -> Unit = {
            timeout { connectTimeoutMillis = 6_000; socketTimeoutMillis = 8_000; requestTimeoutMillis = 20_000 }
            referer?.let { header("Referer", it) }
            ua?.let { header("User-Agent", it) }
            extra.forEach { (k, v) -> header(k, v) }
        }
        return if (headOnly) http.head(url, block) else http.get(url, block)
    }

    override suspend fun headSize(url: String, referer: String, ua: String?): Long = runCatching {
        quick(url, referer, ua, headOnly = true).headers["Content-Length"]?.toLongOrNull() ?: 0
    }.getOrDefault(0)

    override suspend fun fetch(url: String, referer: String, ua: String?): ByteArray? = runCatching {
        http.get(url) { header("Referer", referer); ua?.let { header("User-Agent", it) } }.readRawBytes()
    }.getOrNull()

    override suspend fun probeMedia(g: GrabCandidate, ua: String?, referer: String): ProbeResult = runCatching {
        when (g.type) {
            GrabType.Image -> {
                val r = quick(g.url, referer, ua, mapOf("Range" to "bytes=0-262143"))
                val bytes = r.readRawBytes()
                var p = ProbeResult()
                MediaProbe.imageBounds(bytes)?.let { (w, h) ->
                    p = p.copy(width = w, height = h, thumb = MediaProbe.imageThumb(bytes, w, h))
                }
                // Larger than the first chunk: fetch the whole file for the thumbnail.
                if (p.thumb == null && p.width > 0) runCatching {
                    fetch(g.url, referer, ua)?.let { all -> p = p.copy(thumb = MediaProbe.imageThumb(all, p.width, p.height)) }
                }
                p.copy(size = r.headers["Content-Range"]?.substringAfter('/')?.toLongOrNull() ?: bytes.size.toLong())
            }
            GrabType.Video, GrabType.Audio -> {
                val m = MediaProbe.media(g.url, buildMap { put("Referer", referer); ua?.let { put("User-Agent", it) } }, g.type == GrabType.Video)
                ProbeResult(m?.width ?: 0, m?.height ?: 0, m?.durationMs ?: 0, -1, m?.frame)
            }
            else -> ProbeResult()
        }
    }.getOrDefault(ProbeResult())

    override suspend fun probeHls(g: GrabCandidate, ua: String?, referer: String): Pair<ProbeResult, List<String>> = runCatching {
        suspend fun get(u: String) = quick(u, referer, ua).bodyAsText()
        fun abs(base: String, u: String) = URLBuilder(Url(base)).takeFrom(u.trim()).buildString()
        fun seconds(text: String) = Regex("""#EXTINF:([\d.]+)""").findAll(text).sumOf { it.groupValues[1].toDouble() }
        val text = get(g.url)
        if ("#EXT-X-STREAM-INF" !in text) return@runCatching ProbeResult(durationMs = (seconds(text) * 1000).toLong()) to emptyList()
        val lines = text.lines()
        val variants = lines.withIndex().filter { it.value.startsWith("#EXT-X-STREAM-INF") }.map { (i, l) ->
            Triple(abs(g.url, lines[i + 1]), Regex("""RESOLUTION=(\d+)x(\d+)""").find(l)?.groupValues, Regex("""[^-]BANDWIDTH=(\d+)""").find(l)?.groupValues?.get(1)?.toLong() ?: 0)
        }
        val renditions = Regex("""URI="([^"]+)"""").findAll(text).map { abs(g.url, it.groupValues[1]) }.toList()
        val best = variants.maxByOrNull { it.third }
        var p = ProbeResult()
        best?.second?.let { p = p.copy(width = it[1].toInt(), height = it[2].toInt()) }
        variants.minByOrNull { it.third }?.let { low ->
            val media = get(low.first)
            val dur = (seconds(media) * 1000).toLong()
            p = p.copy(durationMs = dur)
            if (best != null) p = p.copy(size = best.third * dur / 8000)
            // Thumbnail: third segment of the low rendition (skips short intros).
            media.lines().filter { it.isNotBlank() && !it.startsWith("#") }.let { it.getOrNull(2) ?: it.firstOrNull() }?.let { seg ->
                runCatching { quick(abs(low.first, seg), referer, null).readRawBytes() }.getOrNull()
                    ?.let { MediaProbe.segmentFrame(it) }?.let { p = p.copy(thumb = it) }
            }
        }
        p to (variants.map { it.first } + renditions).map { it.substringBefore('?') }
    }.getOrDefault(ProbeResult() to emptyList())
}
