package org.app.vimate.sites.ui.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

/** Operations on the live web view (POC `web?.…`). */
interface WebHandle {
    fun canGoBack(): Boolean
    fun canGoForward(): Boolean
    fun goBack()
    fun goForward()
    fun reload()
    fun loadUrl(url: String)
    /** Runs [js]; [result] gets the raw JSON-encoded value. */
    fun evaluate(js: String, result: ((String?) -> Unit)? = null)
    val userAgent: String?
    /** Switches between the desktop UA and the default mobile UA (without "; wv"). */
    fun setDesktop(desktop: Boolean)
}

/** Holds the [WebHandle] once the platform view exists; null where there is no in-app web view. */
@Stable
class BrowserController {
    var handle by mutableStateOf<WebHandle?>(null)
}

/** Page events the browser screen reacts to (POC WebViewClient / WebChromeClient / VMSniff bridge). */
class WebCallbacks(
    val onProgress: (Int) -> Unit = {},
    val onTitle: (String) -> Unit = {},
    val onPageStarted: (String) -> Unit = {},
    val onHistory: (String) -> Unit = {},
    val onPageFinished: (url: String, title: String) -> Unit = { _, _ -> },
    /** A network request that looks like media (already on the main thread). */
    val onMediaRequest: (String) -> Unit = {},
    /** A <video>/<audio>/<source> src reported by the sniff script. */
    val onMediaElement: (String) -> Unit = {},
    /** Instagram post shortcode tapped on the injected button. */
    val onIgPost: (String) -> Unit = {},
    /** True when [url] was opened natively (YouTube watch/playlist); the web view then stays where it was. */
    val openNatively: (String) -> Boolean = { false },
)

/** True where [PlatformWebView] embeds a real browser (Android); elsewhere it opens the URL externally. */
expect val hasInAppWebView: Boolean

/**
 * The in-app browser. Android: android.webkit.WebView with the POC's settings, popups, media sniffing
 * and injected scripts. Other platforms: opens [startUrl] in the system browser and shows a note.
 * [sniff] = false gives a plain viewer (grabber preview of non-media links).
 */
@Composable
expect fun PlatformWebView(
    startUrl: String,
    controller: BrowserController,
    callbacks: WebCallbacks,
    modifier: Modifier,
    sniff: Boolean = true,
    userAgent: String? = null,
)
