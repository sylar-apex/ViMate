package org.app.vimate.sites.ui.web

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import javafx.application.Platform
import javafx.concurrent.Worker
import javafx.embed.swing.JFXPanel
import javafx.scene.Scene
import javafx.scene.web.WebView
import org.app.vimate.sites.constant.BrowserScripts
import org.app.vimate.sites.logic.MediaSniffer
import java.awt.BorderLayout
import javax.swing.JPanel
import netscape.javascript.JSObject

actual val hasInAppWebView: Boolean = true

private const val DESKTOP_UA =
    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36"

private class DesktopHandle(private val engine: javafx.scene.web.WebEngine) : WebHandle {
    override fun canGoBack(): Boolean = engine.history.currentIndex > 0
    override fun canGoForward(): Boolean = engine.history.currentIndex < engine.history.entries.size - 1
    override fun goBack() = Platform.runLater { if (canGoBack()) engine.history.go(-1) }
    override fun goForward() = Platform.runLater { if (canGoForward()) engine.history.go(1) }
    override fun reload() = Platform.runLater { engine.reload() }
    override fun loadUrl(url: String) = Platform.runLater { engine.load(url) }
    override fun evaluate(js: String, result: ((String?) -> Unit)?) {
        Platform.runLater {
            val value = runCatching { engine.executeScript(js)?.toString() }.getOrNull()
            result?.let { cb -> javax.swing.SwingUtilities.invokeLater { cb(value) } }
        }
    }
    override val userAgent: String? get() = engine.userAgent
    override fun setDesktop(desktop: Boolean) {
        Platform.runLater { engine.userAgent = if (desktop) DESKTOP_UA else DESKTOP_UA }
    }
}

@Composable
actual fun PlatformWebView(
    startUrl: String,
    controller: BrowserController,
    callbacks: WebCallbacks,
    modifier: Modifier,
    sniff: Boolean,
    userAgent: String?,
) {
    // One JavaFX panel for the lifetime of this screen: changing the Swing tree while Compose lays out
    // ("Reentry into ignoringRedrawRequests") crashes, so only the page inside it changes.
    val fx = remember { JFXPanel() }
    val panel = remember { JPanel(BorderLayout()).apply { add(fx, BorderLayout.CENTER) } }
    DisposableEffect(startUrl, sniff) {
        Platform.runLater {
            val view = WebView()
            val engine = view.engine
            engine.userAgent = userAgent ?: DESKTOP_UA
            engine.isJavaScriptEnabled = true
            controller.handle = DesktopHandle(engine)
            engine.loadWorker.progressProperty().addListener { _, _, p ->
                javax.swing.SwingUtilities.invokeLater { callbacks.onProgress((p.toDouble() * 100).toInt()) }
            }
            engine.loadWorker.stateProperty().addListener { _, _, state ->
                val loc = engine.location.orEmpty()
                javax.swing.SwingUtilities.invokeLater {
                    when (state) {
                        Worker.State.RUNNING -> if (loc.isNotBlank()) callbacks.onPageStarted(loc)
                        Worker.State.SUCCEEDED -> {
                            callbacks.onPageFinished(loc, engine.title.orEmpty())
                            callbacks.onHistory(loc)
                            if (callbacks.openNatively(loc)) engine.load(startUrl)
                        }
                        else -> Unit
                    }
                }
                if (state == Worker.State.SUCCEEDED && sniff) inject(engine, callbacks, loc)
            }
            engine.locationProperty().addListener { _, _, loc ->
                if (loc.isNullOrBlank()) return@addListener
                javax.swing.SwingUtilities.invokeLater {
                    callbacks.onHistory(loc)
                    if (callbacks.openNatively(loc)) Platform.runLater { if (engine.history.currentIndex > 0) engine.history.go(-1) }
                }
            }
            fx.scene = Scene(view)
            engine.load(startUrl)
        }
        onDispose { controller.handle = null }
    }
    SwingPanel(factory = { panel }, modifier = modifier)
}

private fun inject(engine: javafx.scene.web.WebEngine, callbacks: WebCallbacks, loc: String) {
    runCatching {
        val window = engine.executeScript("window") as JSObject
        window.setMember("VMSniff", object {
            fun media(src: String) { javax.swing.SwingUtilities.invokeLater { if (src.startsWith("http")) callbacks.onMediaElement(src) } }
            fun igPost(code: String) { javax.swing.SwingUtilities.invokeLater { callbacks.onIgPost(code) } }
        })
        engine.executeScript(BrowserScripts.SNIFF)
        if ("instagram.com" in loc) engine.executeScript(BrowserScripts.IG_BUTTONS)
        val grabbed = engine.executeScript(BrowserScripts.GRAB)?.toString().orEmpty()
        grabbed.removePrefix("[").removeSuffix("]").split("\",\"").forEach { raw ->
            val u = raw.trim().trim('"')
            if (MediaSniffer.isMedia(u)) javax.swing.SwingUtilities.invokeLater { callbacks.onMediaRequest(u) }
        }
    }
}
