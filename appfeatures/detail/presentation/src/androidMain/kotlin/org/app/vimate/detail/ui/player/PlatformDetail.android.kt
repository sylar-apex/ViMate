package org.app.vimate.detail.ui.player

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_p_next
import org.app.vimate.appres.resources.ic_p_pause
import org.app.vimate.appres.resources.ic_p_play
import org.app.vimate.appres.resources.ic_pp_close
import org.app.vimate.appres.resources.ic_pp_full
import org.app.vimate.appres.resources.ic_pp_zoom
import org.app.vimate.detail.model.WebStream
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.WebResolver
import org.jetbrains.compose.resources.painterResource

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun EmbedPlayer(id: String, thumbnail: String) {
    AndroidView(factory = { c ->
        WebView(c).apply {
            settings.javaScriptEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            webChromeClient = WebChromeClient()
            loadDataWithBaseURL("https://www.youtube-nocookie.com",
                """<html><body style="margin:0;background:#000"><iframe width="100%" height="100%" style="position:fixed;inset:0;border:0"
                src="https://www.youtube-nocookie.com/embed/$id?autoplay=1&playsinline=1" allow="autoplay; encrypted-media; picture-in-picture" allowfullscreen></iframe></body></html>""",
                "text/html", "utf-8", null)
        }
    }, modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9), update = {}, onRelease = { it.destroy() })
}

@Composable
actual fun rememberWebFallback(): (suspend (videoId: String) -> List<WebStream>)? {
    val ctx = LocalContext.current
    return remember(ctx) { { id -> WebResolver.capture(ctx, id).map { WebStream(it.url, it.itag, it.mime, it.userAgent) } } }
}

@Composable
actual fun rememberPopupController(): PopupController {
    val ctx = LocalContext.current
    val icons = PopupIcons(
        close = painterResource(Res.drawable.ic_pp_close), pause = painterResource(Res.drawable.ic_p_pause),
        play = painterResource(Res.drawable.ic_p_play), next = painterResource(Res.drawable.ic_p_next),
        zoom = painterResource(Res.drawable.ic_pp_zoom), full = painterResource(Res.drawable.ic_pp_full),
    )
    return remember(ctx, icons) {
        object : PopupController {
            override val supported = true
            override fun canDraw() = PopupPlayer.canDraw(ctx)
            override fun requestPermission() = PopupPlayer.requestPermission(ctx)
            override fun show(v: Video, videoUrl: String, audioUrl: String?, qualityLabel: String, positionMs: Long) =
                PopupPlayer.show(ctx, icons, v, videoUrl, audioUrl, qualityLabel, positionMs)
        }
    }
}
