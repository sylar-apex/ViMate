package org.app.vimate.shell

import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.foundation.layout.fillMaxSize
import org.koin.compose.koinInject
import org.app.vimate.sites.logic.SiteFilter
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.kmediaplayer.settings.ObservableSettings
import io.ktor.http.encodeURLParameter
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_download
import org.app.vimate.appres.resources.ic_nav_me
import org.app.vimate.appres.resources.ic_search
import org.app.vimate.appres.resources.wide_top_search_hint
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource

/**
 * macOS title bar (the window's own bar is transparent): back / forward and a compact search pill centred,
 * Downloads and Me at the end. 38dp tall, beside the traffic lights.
 */
@Composable
internal fun TitleBar(activeDownloads: Int) {
    val nav = LocalAppNavigator.current
    val settings = koinInject<ObservableSettings>()
    Box(Modifier.fillMaxWidth().height(38.dp).background(ViColors.sideBarTint)) {
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(1.dp).background(ViColors.wideHairline))
    }
    Box(Modifier.fillMaxWidth().height(38.dp).padding(start = 80.dp, end = 10.dp)) {
        // Arrows and search centred in the window; Downloads and Me pinned to the end.
        Row(Modifier.align(Alignment.Center).widthIn(max = 640.dp), verticalAlignment = Alignment.CenterVertically) {
            Arrow(Icons.AutoMirrored.Filled.ArrowBack, nav.canGoHistoryBack) { nav.historyBack() }
            Arrow(Icons.AutoMirrored.Filled.ArrowForward, nav.canGoForward) { nav.forward() }
            SearchField(Modifier.padding(start = 6.dp).weight(1f)) { q ->
                // Links open as typed; anything else is a web search (SafeSearch while Sites Filter is on).
                val looksLikeUrl = ' ' !in q && '.' in q
                val url = if (looksLikeUrl) (if ("://" in q) q else "https://$q")
                    else "https://www.google.com/search?q=" + q.encodeURLParameter(spaceToPlus = true)
                nav.open(if (settings[AppKeys.hideAdultSites]) SiteFilter.safeSearch(url) else url)
            }
            Arrow(Icons.Filled.Refresh, true) { nav.refresh() }
        }
        Row(Modifier.align(Alignment.CenterEnd), verticalAlignment = Alignment.CenterVertically) {
            Small(Res.drawable.ic_download, activeDownloads) { nav.home(Tab.FILES) }
            Small(Res.drawable.ic_nav_me, 0) { nav.home(Tab.ME) }
        }
    }
}

@Composable
private fun Arrow(icon: ImageVector, enabled: Boolean, onClick: () -> Unit) {
    Box(Modifier.size(26.dp).clip(RoundedCornerShape(6.dp)).clickable(enabled = enabled, onClick = onClick), contentAlignment = Alignment.Center) {
        Icon(icon, null, Modifier.size(16.dp), tint = if (enabled) ViColors.ink else ViColors.hint)
    }
}

@Composable
private fun Small(icon: DrawableResource, badge: Int, onClick: () -> Unit) {
    // Badge is a pill beside the icon so counts like 228 stay readable (99+ above that).
    Row(Modifier.height(28.dp).clip(RoundedCornerShape(6.dp)).clickable(onClick = onClick).padding(horizontal = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Ic(icon, Modifier.size(16.dp), ViColors.ink)
        if (badge > 0) Box(Modifier.padding(start = 4.dp).height(15.dp).clip(CircleShape).background(VRed).padding(horizontal = 4.dp), contentAlignment = Alignment.Center) {
            Text(if (badge > 99) "99+" else "$badge", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, style = TextStyle(lineHeight = 9.sp, lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.Both)))
        }
    }
}

/** Compact text field: type a link or words, Enter opens it; the box clears after. */
@Composable
private fun SearchField(modifier: Modifier, onSubmit: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    val shape = RoundedCornerShape(7.dp)
    BasicTextField(
        value = text, onValueChange = { text = it }, singleLine = true,
        textStyle = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, color = ViColors.ink), cursorBrush = SolidColor(VRed),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
        keyboardActions = KeyboardActions(onGo = { text.trim().takeIf { it.isNotEmpty() }?.let(onSubmit); text = "" }),
        modifier = modifier.height(26.dp).onPreviewKeyEvent { e ->
            if (e.type == KeyEventType.KeyDown && e.key == Key.Enter) { text.trim().takeIf { it.isNotEmpty() }?.let(onSubmit); text = ""; true } else false
        },
        decorationBox = { inner ->
            Row(Modifier.fillMaxSize().clip(shape).background(ViColors.chipIdle).border(1.dp, ViColors.wideHairline, shape).padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Ic(Res.drawable.ic_search, Modifier.size(13.dp), ViColors.sectionLabel)
                // Hint and typed text share one line box, so the cursor sits exactly where the hint starts.
                Box(Modifier.padding(start = 8.dp).weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (text.isEmpty()) Text(stringResource(Res.string.wide_top_search_hint), color = ViColors.sectionLabel, fontSize = 12.sp, lineHeight = 16.sp, maxLines = 1)
                    inner()
                }
            }
        },
    )
}
