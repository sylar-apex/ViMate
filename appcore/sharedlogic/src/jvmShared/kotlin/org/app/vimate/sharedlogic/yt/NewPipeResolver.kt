package org.app.vimate.sharedlogic.yt

import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Response
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.downloader.Request as NpRequest

/**
 * Stream resolver backed by NewPipeExtractor, which runs YouTube's player JavaScript (signature and
 * "n" parameter) so the returned googlevideo URLs play and download without being refused.
 */
object NewPipeResolver : StreamResolver {
    /** NewPipe's own requests go out with a desktop browser UA; googlevideo accepts this UA for its URLs. */
    const val UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0"

    private val http: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    private object OkDownloader : Downloader() {
        override fun execute(request: NpRequest): Response {
            val b = Request.Builder().url(request.url()).header("User-Agent", UA)
            request.headers().forEach { (k, vs) -> b.removeHeader(k); vs.forEach { v -> b.addHeader(k, v) } }
            val body = request.dataToSend()
            when (request.httpMethod()) {
                "POST" -> b.post((body ?: ByteArray(0)).toRequestBody())
                "HEAD" -> b.head()
                else -> b.get()
            }
            http.newCall(b.build()).execute().use { r ->
                return Response(r.code, r.message, r.headers.toMultimap(), r.body?.string(), r.request.url.toString())
            }
        }
    }

    @Volatile private var ready = false

    private fun init() {
        if (ready) return
        NewPipe.init(OkDownloader, Localization(YouTube.hl, YouTube.gl), ContentCountry(YouTube.gl))
        ready = true
    }

    override suspend fun resolve(videoId: String): VideoInfo = info(videoId)

    suspend fun info(id: String): VideoInfo = withContext(Dispatchers.IO) {
        init()
        val s = StreamInfo.getInfo(ServiceList.YouTube, "https://www.youtube.com/watch?v=$id")
        fun mime0(fmt: String?, video: Boolean) = when (fmt?.lowercase()) {
            "mpeg_4", "mp4" -> if (video) "video/mp4; codecs=\"avc1\"" else "audio/mp4"
            "m4a" -> "audio/mp4"
            "webm", "webma", "webm_audio" -> if (video) "video/webm" else "audio/webm"
            "v3gpp" -> "video/3gpp"
            else -> if (video) "video/mp4" else "audio/mp4"
        }
        fun mime(fmt: String?, video: Boolean) = when {
            fmt != null && "webm" in fmt.lowercase() -> if (video) "video/webm" else "audio/webm"
            else -> mime0(fmt, video)
        }
        val muxed = s.videoStreams.filter { it.isUrl }.map { v ->
            Stream(v.itag, v.content, mime(v.format?.name, true), v.bitrate, 0, minOf(v.height, v.width.takeIf { it > 0 } ?: v.height), v.resolution.orEmpty(), 0, hasVideo = true, hasAudio = true)
        }
        val videoOnly = s.videoOnlyStreams.filter { it.isUrl }.map { v ->
            val codec = v.codec.orEmpty()
            val m = if (codec.startsWith("avc")) "video/mp4; codecs=\"$codec\"" else mime(v.format?.name, true) + "; codecs=\"$codec\""
            Stream(v.itag, v.content, m, v.bitrate, v.itagItem?.contentLength ?: 0, minOf(v.height, v.width.takeIf { it > 0 } ?: v.height), v.resolution.orEmpty(), 0, hasVideo = true, hasAudio = false)
        }
        val audio = s.audioStreams.filter { it.isUrl }.map { a ->
            Stream(a.itag, a.content, mime(a.format?.name, false), a.averageBitrate * 1000, a.itagItem?.contentLength ?: 0, 0, "", 0, hasVideo = false, hasAudio = true)
        }
        val dur = s.duration
        fun size(st: Stream) = if (st.sizeBytes > 0) st else st.copy(sizeBytes = dur * st.bitrate / 8)
        YouTube.streamUA = UA
        YouTube.lastClient = "NewPipe"
        VideoInfo(
            video = Video(id = id, title = s.name, channel = s.uploaderName.orEmpty(), channelId = s.uploaderUrl.orEmpty().substringAfterLast('/'),
                views = YouTube.formatCount(s.viewCount.coerceAtLeast(0)) + " views", duration = YouTube.formatDuration(dur),
                thumbnail = s.thumbnails.maxByOrNull { it.width }?.url ?: "https://i.ytimg.com/vi/$id/hqdefault.jpg"),
            description = s.description?.content.orEmpty(), lengthSec = dur, likes = YouTube.formatCount(s.likeCount.coerceAtLeast(0)),
            muxed = muxed.map(::size), videoOnly = videoOnly.map(::size), audioOnly = audio.map(::size),
            captions = s.subtitles.map { Caption(it.content, it.languageTag, it.displayLanguageName) },
            hlsUrl = s.hlsUrl?.takeIf { it.isNotEmpty() },
        )
    }
}
