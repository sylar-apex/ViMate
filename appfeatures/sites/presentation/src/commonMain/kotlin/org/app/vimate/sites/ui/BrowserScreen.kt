package org.app.vimate.sites.ui

import org.app.vimate.appres.resources.sites_blocked
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.sites.logic.SiteFilter
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.http.encodeURLParameter
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.app.kmediaplayer.KMediaPlayer
import org.app.kmediaplayer.util.nowMs
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.component.rememberLoad
import org.app.vimate.appcore.component.rememberShareText
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_download
import org.app.vimate.appres.resources.sites_added_to_downloads
import org.app.vimate.appres.resources.sites_close
import org.app.vimate.appres.resources.sites_copied
import org.app.vimate.appres.resources.sites_download
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.YouTube
import org.app.vimate.sites.constant.BrowserScripts
import org.app.vimate.sites.logic.MediaSniffer
import org.app.vimate.sites.logic.NativeLink
import org.app.vimate.sites.logic.hostOf
import org.app.vimate.sites.logic.nativeLink
import org.app.vimate.sites.logic.typedToUrl
import org.app.vimate.sites.model.Sniffed
import org.app.vimate.sites.ui.component.BrowserHeader
import org.app.vimate.sites.ui.component.BrowserHeaderActions
import org.app.vimate.sites.ui.component.FoundSheet
import org.app.vimate.sites.ui.constant.siteTiles
import org.app.vimate.sites.ui.grabber.GrabberDialog
import org.app.vimate.sites.ui.web.BrowserController
import org.app.vimate.sites.ui.web.PlatformWebView
import org.app.vimate.sites.ui.web.WebCallbacks
import org.app.vimate.sites.usecase.AddSiteVisitUseCase
import org.app.vimate.sites.usecase.EnqueueDirectUseCase
import org.app.vimate.sites.usecase.ResolveInstagramPostUseCase
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/** Ignore the second report of the same native link (page start + history update). */
private const val NATIVE_DEBOUNCE_MS = 1500L

/** In-app browser with media sniffing, grabber, Instagram per-post download and floating download button. */
@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
@Composable
fun BrowserScreen(startUrl: String) {
    val nav = LocalAppNavigator.current
    val toast = LocalToast.current
    val share = rememberShareText()
    val clipboard = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val addSite = koinInject<AddSiteVisitUseCase>()
    val enqueueDirect = koinInject<EnqueueDirectUseCase>()
    val resolveIg = koinInject<ResolveInstagramPostUseCase>()
    val controller = remember { BrowserController() }
    val web = controller.handle
    var url by remember { mutableStateOf(startUrl) }
    var edit by remember { mutableStateOf(startUrl) }
    var title by remember { mutableStateOf("") }
    var progress by remember { mutableIntStateOf(0) }
    val found = remember { mutableStateListOf<Sniffed>() }
    var showFound by remember { mutableStateOf(false) }
    var grab by remember { mutableStateOf<List<String>?>(null) }
    var desktop by remember { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf(false) }
    // Per-post download (Instagram): shortcode picked by the injected button → resolved media → sheet.
    var postCode by remember { mutableStateOf<String?>(null) }
    var lastNative by remember { mutableStateOf("" to 0L) }
    // Sites Filter: an adult page is replaced by a notice.
    val settings = koinInject<ObservableSettings>()
    var blocked by remember { mutableStateOf(settings[AppKeys.hideAdultSites] && SiteFilter.isAdult(startUrl)) }

    fun add(u: String, mime: String) {
        val clean = MediaSniffer.clean(u) ?: return
        val key = clean.substringBefore('?')
        if (found.none { it.url.substringBefore('?') == key }) found.add(0, Sniffed(clean, mime, title))
    }

    BackHandler { if (web?.canGoBack() == true) web.goBack() else nav.back() }
    val siteName = remember(url, title) {
        if (YouTube.idFromUrl(url) != null && title.isNotBlank()) return@remember title.removeSuffix(" - YouTube")
        siteTiles.firstOrNull { t -> hostOf(t.url)?.removePrefix("m.")?.let { h -> h in url } == true }?.name
            ?: title.ifEmpty { hostOf(url) ?: url }
    }
    val callbacks = WebCallbacks(
        onProgress = { progress = it },
        onTitle = { title = it },
        onPageStarted = { u ->
            url = u; edit = u; found.clear()
            if (settings[AppKeys.hideAdultSites] && SiteFilter.isAdult(u)) { blocked = true; web?.loadUrl("about:blank") }
            else if (u != "about:blank") blocked = false
        },
        onHistory = { u -> url = u; edit = u },
        onPageFinished = { u, t -> addSite(u, t) },
        onMediaRequest = { u -> add(u, MediaSniffer.mimeOf(u)) },
        onMediaElement = { src -> add(src, "video/mp4") },
        onIgPost = { postCode = it },
        openNatively = { u ->
            val link = nativeLink(u, YouTube::idFromUrl)
            if (link == null) false
            else {
                val now = nowMs()
                if (lastNative.first != u || now - lastNative.second > NATIVE_DEBOUNCE_MS) {
                    lastNative = u to now
                    when (link) {
                        is NativeLink.Playlist -> nav.go(Routes.YtPlaylist(link.listId))
                        is NativeLink.Watch -> nav.go(Routes.Detail.of(Video(link.videoId, "")))
                    }
                }
                true
            }
        },
    )
    // Measured from VidMate: 50dp white header with shadow; home at x=16, bold centred site name, reload at x=268, menu at x=312.
    Column(Modifier.fillMaxSize().background(ViColors.pocLightBackground).statusBarsPadding().background(ViColors.surface)) {
        BrowserHeader(
            url = url, host = hostOf(url) ?: url, siteName = siteName, foundCount = found.size, desktop = desktop,
            canGoBack = web?.canGoBack() == true, canGoForward = web?.canGoForward() == true, menu = menu, onMenu = { menu = it },
            a = BrowserHeaderActions(
                onHome = { nav.home(Tab.HOME) },
                onEdit = { editing = true },
                onGrab = {
                    web?.evaluate(BrowserScripts.GRAB) { raw ->
                        val list = runCatching { Json.parseToJsonElement(Json.parseToJsonElement(raw.orEmpty()).jsonPrimitive.content).jsonArray.map { it.jsonPrimitive.content } }.getOrNull()
                        grab = list.orEmpty() + found.map { it.url }
                    } ?: run { grab = found.map { it.url } }
                },
                onReload = { web?.reload() },
                onBack = { web?.goBack() },
                onForward = { web?.goForward() },
                onToggleDesktop = { desktop = !desktop; web?.setDesktop(desktop); web?.reload() },
                onCopy = { clipboard.setText(AnnotatedString(url)); scope.launch { toast(getString(Res.string.sites_copied)) } },
                onShare = { share(url) },
                onDownloads = { nav.home(Tab.FILES) },
            ),
        )
        if (editing) Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(edit, { edit = it }, Modifier.weight(1f), singleLine = true, shape = RoundedCornerShape(24.dp), textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go), keyboardActions = KeyboardActions(onGo = {
                    web?.loadUrl(typedToUrl(edit.trim()) { it.encodeURLParameter() }.let { if (settings[AppKeys.hideAdultSites]) SiteFilter.safeSearch(it) else it })
                    editing = false
                }))
            IconButton(onClick = { editing = false }) { Icon(Icons.Default.Close, stringResource(Res.string.sites_close)) }
        }
        if (progress in 1..99) LinearProgressIndicator(progress = { progress / 100f }, Modifier.fillMaxWidth().height(2.dp), color = VRed, trackColor = ViColors.transparent, drawStopIndicator = {})
        Box(Modifier.weight(1f)) {
            // The native web view draws above Compose, so a blocked page hides it rather than covering it.
            PlatformWebView(if (blocked) "about:blank" else startUrl, controller, callbacks, if (blocked) Modifier.size(0.dp) else Modifier.fillMaxSize())
            if (blocked) Box(Modifier.fillMaxSize().background(ViColors.surface), contentAlignment = Alignment.Center) {
                Text(stringResource(Res.string.sites_blocked), Modifier.padding(32.dp), color = ViColors.muted, fontSize = 15.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }
            // On YouTube the page's stream URLs are unusable; download via our own resolver for the current video/Short.
            val ytId = if ("youtube.com" in url || "youtu.be" in url) YouTube.idFromUrl(url) else null
            if (ytId != null) Box(Modifier.align(Alignment.BottomEnd).padding(end = 20.dp, bottom = 46.dp).size(54.dp).clip(CircleShape).background(VRed)
                .clickable { nav.download(Video(ytId, title.removeSuffix(" - YouTube"))) }, contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_download, Modifier.size(28.dp), ViColors.white, stringResource(Res.string.sites_download))
            }
            else if (found.isNotEmpty() && "youtube.com" !in url && "instagram.com" !in url) ExtendedFloatingActionButton(
                onClick = { showFound = true },
                containerColor = VRed, contentColor = ViColors.white,
                modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
                icon = { Icon(Icons.Filled.Download, null) }, text = { Text("${found.size}") })
        }
    }
    grab?.let { g -> GrabberDialog(url, title, g, web?.userAgent) { grab = null } }
    postCode?.let { code ->
        val ua = remember { web?.userAgent.orEmpty() }
        val (state, retry) = rememberLoad(code) { resolveIg(code, ua) }
        SiteDownloadSheet(state, retry) { postCode = null }
    }
    if (showFound) FoundSheet(
        found,
        onPlay = { KMediaPlayer.playUrl(it.url) },
        onDownload = { f ->
            enqueueDirect(f.url, f.title.ifEmpty { title }, url, web?.userAgent)
            scope.launch { toast(getString(Res.string.sites_added_to_downloads)) }
            showFound = false
        },
        onDismiss = { showFound = false },
    )
}
