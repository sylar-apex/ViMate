package org.app.vimate.sites.logic

import org.app.kmediaplayer.util.nowMs
import org.app.vimate.sharedlogic.download.option.Kind
import org.app.vimate.sharedlogic.download.vid.Part
import org.app.vimate.sharedlogic.download.vid.VidTask
import org.app.vimate.sharedlogic.sites.MediaOption
import org.app.vimate.sharedlogic.sites.SiteMedia
import org.app.vimate.sites.model.GrabType

private val webExts = setOf("php", "asp", "aspx", "jsp", "html", "htm", "cgi")

/** POC `enqueueDirect`: the task for one browser/grabber URL. [cookie] is the browser's cookie for [mediaUrl]. */
fun buildDirectTask(mediaUrl: String, title: String, page: String, userAgent: String?, grabType: GrabType?, cookie: String?): VidTask {
    val hls = ".m3u8" in mediaUrl
    val urlExt = mediaUrl.substringBefore('#').substringBefore('?').substringAfterLast('/').substringAfterLast('.', "").lowercase().takeIf { it.length in 2..5 && it.all(Char::isLetterOrDigit) }
    val type = grabType ?: GrabType.of(mediaUrl)
    val webPage = type == GrabType.Other && (urlExt == null || urlExt in webExts)
    val ext = when {
        hls -> "mp4"
        webPage -> "html"
        urlExt != null -> urlExt
        type == GrabType.Image -> "jpg"
        type == GrabType.Audio -> "m4a"
        else -> "mp4"
    }
    val kind = when (type) { GrabType.Audio -> Kind.Music; GrabType.Image -> Kind.Picture; GrabType.Program -> Kind.App; GrabType.Video -> Kind.Video; else -> Kind.Other }
    val now = nowMs()
    val name = title.ifBlank { mediaUrl.substringAfter("://").substringBefore('?').trimEnd('/').replace('/', '_').take(60) }.ifBlank { "file_$now" }
    return VidTask(
        id = now + (mediaUrl.hashCode() and 0xffff), title = name, ext = ext, kind = kind,
        pageUrl = page, parts = listOf(Part(mediaUrl, hls = hls, allowHtml = type != GrabType.Video && type != GrabType.Audio, headers = buildMap {
            userAgent?.let { put("User-Agent", it) }
            if (page.startsWith("http") && page != mediaUrl) put("Referer", page)
            cookie?.takeIf { it.isNotEmpty() }?.let { put("Cookie", it) }
        })),
    )
}

/** POC SiteDownloadSheet DOWNLOAD: the task for the chosen [o] of [m]. */
fun buildSiteTask(m: SiteMedia, o: MediaOption, title: String): VidTask = VidTask(
    id = nowMs(), title = title.ifBlank { m.title } + if (o.section != "Music") "_${o.label}" else "",
    ext = o.ext, kind = when (o.ext) { "m4a" -> Kind.Music; "jpg" -> Kind.Picture; else -> Kind.Video },
    thumbnail = m.thumbnail, pageUrl = m.pageUrl, parts = listOf(Part(o.url, o.sizeBytes.takeIf { it > 0 } ?: -1, headers = m.headers)),
)
