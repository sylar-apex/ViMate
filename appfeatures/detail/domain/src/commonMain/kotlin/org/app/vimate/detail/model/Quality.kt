package org.app.vimate.detail.model

/** One playable choice of the detail player (POC ui/InlinePlayer.kt `Quality`). [audioUrl] is merged with a video-only [url]. */
data class Quality(val label: String, val url: String, val audioUrl: String? = null, val hls: Boolean = false, val ua: String? = null)

/** A stream captured from YouTube's own web player (POC yt/WebResolver.Captured). */
data class WebStream(val url: String, val itag: Int, val mime: String, val userAgent: String)
