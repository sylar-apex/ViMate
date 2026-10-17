package org.app.vimate.sharedlogic.yt

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume

/**
 * Fallback stream resolver: loads the YouTube watch page in an off-screen WebView so YouTube's own
 * player (with its tokens and signature handling) requests the media, and captures those
 * googlevideo "videoplayback" URLs. Range parameters are stripped so the URL serves the whole stream.
 * Android only; the Detail player calls it after a BAD_HTTP_STATUS playback error.
 */
object WebResolver {
    data class Captured(val url: String, val itag: Int, val mime: String, val userAgent: String)

    @SuppressLint("SetJavaScriptEnabled")
    suspend fun capture(context: Context, videoId: String, timeoutMs: Long = 20_000): List<Captured> = withContext(Dispatchers.Main) {
        val found = LinkedHashMap<Int, Captured>()
        var web: WebView? = null
        val result = withTimeoutOrNull(timeoutMs) {
            suspendCancellableCoroutine { cont ->
                val w = WebView(context.applicationContext)
                web = w
                w.settings.javaScriptEnabled = true
                w.settings.domStorageEnabled = true
                w.settings.mediaPlaybackRequiresUserGesture = false
                CookieManager.getInstance().setAcceptThirdPartyCookies(w, true)
                val ua = w.settings.userAgentString
                w.webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                        val u = request.url
                        if (u.host?.endsWith("googlevideo.com") == true && u.path?.contains("videoplayback") == true) {
                            val itag = u.getQueryParameter("itag")?.toIntOrNull() ?: 0
                            val mime = u.getQueryParameter("mime").orEmpty()
                            val clean = stripRange(u)
                            synchronized(found) {
                                if (itag !in found) found[itag] = Captured(clean, itag, mime, ua)
                                val hasVideo = found.values.any { it.mime.startsWith("video") }
                                val hasAudio = found.values.any { it.mime.startsWith("audio") }
                                if (hasVideo && hasAudio && cont.isActive) cont.resume(found.values.toList())
                            }
                        }
                        return null
                    }
                }
                w.loadUrl("https://m.youtube.com/watch?v=$videoId")
                cont.invokeOnCancellation { }
            }
        }
        web?.apply { stopLoading(); destroy() }
        result ?: synchronized(found) { found.values.toList() }
    }

    private fun stripRange(u: Uri): String {
        val b = u.buildUpon().clearQuery()
        for (name in u.queryParameterNames) if (name !in setOf("range", "rn", "rbuf", "ump", "srfvp", "alr")) b.appendQueryParameter(name, u.getQueryParameter(name))
        return b.build().toString()
    }
}
