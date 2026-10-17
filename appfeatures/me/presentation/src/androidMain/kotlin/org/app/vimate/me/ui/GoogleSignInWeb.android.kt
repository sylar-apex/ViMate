package org.app.vimate.me.ui

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.app.vimate.sharedlogic.yt.YouTube

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun GoogleSignInWeb(url: String, modifier: Modifier, onSignedIn: () -> Unit) {
    AndroidView(factory = { c ->
        WebView(c).apply {
            layoutParams = ViewGroup.LayoutParams(-1, -1)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            // Google refuses sign-in inside an embedded WebView ("; wv"); present as mobile Chrome.
            settings.userAgentString = settings.userAgentString.replace("; wv", "").replace(Regex(" Version/[0-9.]+"), "")
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    if ("youtube.com" in url && YouTube.signedIn) {
                        CookieManager.getInstance().flush()
                        onSignedIn()
                    }
                }
            }
            loadUrl(url)
        }
    }, modifier = modifier, onRelease = { it.destroy() })
}
