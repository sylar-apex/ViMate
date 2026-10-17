package org.app.vimate.sites.ui.web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.sites_close
import org.app.vimate.appres.resources.sites_sign_in
import org.app.vimate.sites.constant.BrowserScripts
import org.app.vimate.sites.logic.MediaSniffer
import org.jetbrains.compose.resources.stringResource

actual val hasInAppWebView: Boolean = true

private class AndroidWebHandle(val view: WebView) : WebHandle {
    override fun canGoBack() = view.canGoBack()
    override fun canGoForward() = view.canGoForward()
    override fun goBack() = view.goBack()
    override fun goForward() = view.goForward()
    override fun reload() = view.reload()
    override fun loadUrl(url: String) = view.loadUrl(url)
    override fun evaluate(js: String, result: ((String?) -> Unit)?) = view.evaluateJavascript(js, result)
    override val userAgent: String? get() = view.settings.userAgentString
    override fun setDesktop(desktop: Boolean) {
        view.settings.userAgentString = if (desktop) BrowserScripts.DESKTOP_UA
        else WebSettings.getDefaultUserAgent(view.context).replace("; wv", "").replace(Regex(" Version/[0-9.]+"), "")
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
actual fun PlatformWebView(startUrl: String, controller: BrowserController, callbacks: WebCallbacks, modifier: Modifier, sniff: Boolean, userAgent: String?) {
    val cb by rememberUpdatedState(callbacks)
    // Popup windows (window.open), e.g. "Sign in with Google" on Pinterest/others.
    var popup by remember { mutableStateOf<WebView?>(null) }

    if (!sniff) {
        AndroidView(factory = { c ->
            WebView(c).apply {
                layoutParams = ViewGroup.LayoutParams(-1, -1)
                settings.javaScriptEnabled = true; settings.domStorageEnabled = true
                userAgent?.let { settings.userAgentString = it }
                webViewClient = WebViewClient()
                controller.handle = AndroidWebHandle(this)
                loadUrl(startUrl)
            }
        }, modifier = modifier, onRelease = { it.destroy() })
        return
    }

    AndroidView(factory = { c ->
        WebView(c).apply {
            controller.handle = AndroidWebHandle(this)
            // Without real layout params the page sees a 0px viewport: 100vh = 0 and Instagram renders blank.
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            // Present as regular mobile Chrome: Instagram/Facebook serve a blank page to WebView ("; wv").
            settings.userAgentString = settings.userAgentString.replace("; wv", "").replace(Regex(" Version/[0-9.]+"), "")
            @Suppress("DEPRECATION")
            settings.databaseEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            CookieManager.getInstance().setAcceptCookie(true)
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            addJavascriptInterface(object {
                @JavascriptInterface fun media(src: String) { post { if (src.startsWith("http")) cb.onMediaElement(src) } }
                @JavascriptInterface fun igPost(code: String) { post { cb.onIgPost(code) } }
            }, BrowserScripts.BRIDGE)
            settings.setSupportMultipleWindows(true)
            webChromeClient = object : WebChromeClient() {
                override fun onCreateWindow(view: WebView, isDialog: Boolean, isUserGesture: Boolean, resultMsg: android.os.Message): Boolean {
                    val child = WebView(view.context).apply {
                        layoutParams = ViewGroup.LayoutParams(-1, -1)
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.userAgentString = view.settings.userAgentString
                        settings.setSupportMultipleWindows(true)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
                        webViewClient = WebViewClient()
                        webChromeClient = object : WebChromeClient() {
                            override fun onCloseWindow(window: WebView) { popup = null; window.destroy() }
                        }
                    }
                    popup = child
                    (resultMsg.obj as WebView.WebViewTransport).webView = child
                    resultMsg.sendToTarget()
                    return true
                }
                override fun onProgressChanged(view: WebView, p: Int) { cb.onProgress(p) }
                override fun onReceivedTitle(view: WebView, t: String?) { cb.onTitle(t.orEmpty()) }
            }
            webViewClient = object : WebViewClient() {
                /** YouTube video/shorts/playlist URLs open natively, like VidMate. Returns true if handled. */
                fun native(view: WebView, u: String): Boolean {
                    if (!cb.openNatively(u)) return false
                    // Keep the browser on the page the user came from.
                    view.post { if (view.canGoBack() && view.url?.let { it == u } == true) view.goBack() }
                    return true
                }
                override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                    val u = request.url.toString()
                    if (native(view, u)) return true
                    return !u.startsWith("http")
                }
                override fun onPageStarted(view: WebView, u: String, favicon: Bitmap?) {
                    cb.onPageStarted(u)
                    if (native(view, u)) view.stopLoading()
                }
                // m.youtube.com is a single-page app: taps change the URL via history.pushState, no page load.
                override fun doUpdateVisitedHistory(view: WebView, u: String, isReload: Boolean) {
                    cb.onHistory(u)
                    native(view, u)
                }
                override fun onPageFinished(view: WebView, u: String) {
                    cb.onPageFinished(u, view.title.orEmpty())
                    view.evaluateJavascript(BrowserScripts.SNIFF, null)
                    if ("instagram.com" in u) view.evaluateJavascript(BrowserScripts.IG_BUTTONS, null)
                }
                override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                    val u = request.url.toString()
                    if (MediaSniffer.isMedia(u)) view.post { cb.onMediaRequest(u) }
                    return null
                }
            }
            loadUrl(startUrl)
        }
    }, modifier = modifier, onRelease = { controller.handle = null; it.destroy() })

    popup?.let { p ->
        Dialog(onDismissRequest = { popup = null; p.destroy() }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Column(Modifier.fillMaxSize().background(ViColors.white)) {
                Row(Modifier.fillMaxWidth().height(50.dp).padding(start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { popup = null; p.destroy() }) { Icon(Icons.Default.Close, stringResource(Res.string.sites_close)) }
                    Text(p.title.orEmpty().ifEmpty { stringResource(Res.string.sites_sign_in) }, fontWeight = FontWeight.Bold, fontSize = 17.sp, maxLines = 1)
                }
                AndroidView(factory = { (p.parent as? ViewGroup)?.removeView(p); p }, modifier = Modifier.fillMaxSize())
            }
        }
    }
}
