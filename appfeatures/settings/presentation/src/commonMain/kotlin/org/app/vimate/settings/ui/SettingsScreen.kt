package org.app.vimate.settings.ui

import org.app.vimate.sharedlogic.platform.PlatformActions
import com.psbapp.utils.PlatformType
import com.psbapp.utils.getPlatform
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.remember
import org.app.vimate.sharedlogic.download.vid.VidDownloads
import org.koin.compose.koinInject
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest
import org.app.kmediaplayer.settings.Setting
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.settings.domain.constant.SettingsGroups as G
import org.app.vimate.appcore.adaptive.LocalWindowClass
import org.app.vimate.appcore.adaptive.WindowClass
import org.app.vimate.settings.ui.wide.SettingsWide
import org.app.vimate.settings.ui.component.*
import org.app.vimate.sharedlogic.settings.AppKeys
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

// Geometry measured from VidMate 6.0302 settings (3px = 1dp).

internal data class Entry(val icon: DrawableResource, val group: String, val title: StringResource, val sub: StringResource)

/** Settings groups; Push Notifications and Widgets are Android-only (no push or home-screen widgets on desktop, iOS or the web). */
internal val settingsEntries = listOfNotNull(
    Entry(Res.drawable.ic_s_globe, G.CONTENT, Res.string.settings_content, Res.string.settings_content_sub),
    Entry(Res.drawable.ic_s_dl, G.DOWNLOAD, Res.string.settings_download, Res.string.settings_download_sub),
    Entry(Res.drawable.ic_s_bell, G.PUSH, Res.string.settings_push, Res.string.settings_push_sub)
        .takeIf { getPlatform().type == PlatformType.Android },
    Entry(Res.drawable.ic_s_widgets, G.WIDGETS, Res.string.settings_widgets, Res.string.settings_widgets_sub)
        .takeIf { getPlatform().type == PlatformType.Android },
    Entry(Res.drawable.ic_t_settings, G.OTHERS, Res.string.settings_others, Res.string.settings_others_sub),
)

@Composable
fun SettingsScreen() {
    if (LocalWindowClass.current != WindowClass.Compact) return SettingsWide(G.DOWNLOAD)
    val nav = LocalAppNavigator.current
    Column(Modifier.fillMaxSize().background(ViColors.page).statusBarsPadding()) {
        Box(Modifier.background(ViColors.surface)) { SettingsHeader(stringResource(Res.string.settings_title)) { nav.back() } }
        Spacer(Modifier.height(7.dp))
        LazyColumn(Modifier.background(ViColors.surface), userScrollEnabled = false) {
            itemsIndexed(settingsEntries, key = { _, e -> e.group }) { i, e ->
                androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth().height(81.dp).clickable { nav.go(Routes.SettingsGroup(e.group)) }.padding(start = 15.dp, end = 13.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Ic(e.icon, Modifier.size(24.dp), ViColors.ink)
                    Column(Modifier.padding(start = 33.dp)) {
                        Text(stringResource(e.title), fontSize = 14.5.sp, lineHeight = 21.sp, color = ViColors.ink)
                        Text(stringResource(e.sub), Modifier.padding(top = 6.dp), fontSize = 12.5.sp, lineHeight = 16.sp, color = ViColors.setSub, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
                if (i < settingsEntries.lastIndex) Box(Modifier.padding(start = 60.dp, end = 13.dp).fillMaxWidth().height(0.7.dp).background(ViColors.setRule))
            }
        }
        Spacer(Modifier.height(7.dp))
        androidx.compose.foundation.layout.Row(Modifier.fillMaxWidth().height(60.dp).background(ViColors.surface).clickable { nav.go(Routes.SettingsGroup(G.ABOUT)) }.padding(start = 15.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Ic(Res.drawable.ic_s_info, Modifier.size(26.dp), ViColors.ink)
            Text(stringResource(Res.string.settings_about), Modifier.padding(start = 33.dp), fontSize = 14.5.sp, color = ViColors.ink)
        }
    }
}

internal val groupTitles = mapOf(
    G.CONTENT to Res.string.settings_content, G.DOWNLOAD to Res.string.settings_download, G.PUSH to Res.string.settings_push,
    G.WIDGETS to Res.string.settings_widgets, G.OTHERS to Res.string.settings_others,
)

private val locationNames = mapOf(
    "IN" to Res.string.settings_loc_IN, "US" to Res.string.settings_loc_US, "GB" to Res.string.settings_loc_GB,
    "ID" to Res.string.settings_loc_ID, "BR" to Res.string.settings_loc_BR, "PK" to Res.string.settings_loc_PK,
)

@Composable
fun SettingsGroupScreen(group: String) {
    if (LocalWindowClass.current != WindowClass.Compact) return SettingsWide(group)
    val nav = LocalAppNavigator.current
    Column(Modifier.fillMaxSize().background(ViColors.page).statusBarsPadding()) {
        val title = if (group == G.ABOUT) stringResource(Res.string.settings_about)
            else stringResource(Res.string.settings_group_title, groupTitles[group]?.let { stringResource(it) } ?: group)
        Box(Modifier.background(ViColors.surface)) { SettingsHeader(title) { nav.back() } }
        SettingsGroupBody(group)
    }
}

/** The rows of one settings group (shared by the phone page and the wide two-pane layout). */
@Composable
internal fun SettingsGroupBody(group: String, viewModel: SettingsViewModel = koinViewModel()) {
    val toast = LocalToast.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val cleared = stringResource(Res.string.settings_pin_cleared)
    val widgetHint = stringResource(Res.string.settings_widget_hint)
    LaunchedEffect(Unit) { viewModel.effect.collectLatest { if (it is SettingsContract.Effect.PrivatePasswordCleared) toast(cleared) } }
    val s = viewModel
    val downloads = koinInject<VidDownloads>()
    val platform = koinInject<PlatformActions>()
    fun <T> set(k: Setting<T>, v: T) = viewModel.setEvent(SettingsContract.Event.Set(k, v))
    val always = stringResource(Res.string.settings_always); val never = stringResource(Res.string.settings_never)
    val sysLang = stringResource(Res.string.settings_system_language); val off = stringResource(Res.string.settings_off)
    key(state.version) {
        Column {
            LazyColumn(Modifier.padding(top = 3.dp)) {
                item(key = group) {
                    when (group) {
                        G.DOWNLOAD -> {
                            Block {
                                // The real folder on this device (Downloads/<name> on Android, ~/Downloads/<name> on desktop), not an Android-only template.
                                val path = remember(s[AppKeys.downloadFolder]) { downloads.location() }
                                // Desktop: pick any folder with the system dialog. Android saves under Download/, so only the folder name is asked.
                                if (getPlatform().type == PlatformType.Desktop) Row(s = stringResource(Res.string.settings_dl_path), sub = path, value = stringResource(Res.string.settings_change)) {
                                    platform.pickFolder()?.let { set(AppKeys.downloadFolder, it) }
                                }
                                else TextPref(stringResource(Res.string.settings_dl_path), path, s[AppKeys.downloadFolder]) { set(AppKeys.downloadFolder, it) }
                                Toggle(stringResource(Res.string.settings_wifi_only), null, s[AppKeys.wifiOnly]) { set(AppKeys.wifiOnly, it) }
                                Choice(stringResource(Res.string.settings_max_tasks), s[AppKeys.maxTasks].toString(), G.MAX_TASKS) { set(AppKeys.maxTasks, it.toInt()) }
                                Choice(stringResource(Res.string.settings_fast_download), s[AppKeys.connections].toString(), G.CONNECTIONS, sub = stringResource(Res.string.settings_fast_download_sub), showValue = false) { set(AppKeys.connections, it.toInt()) }
                                Choice(stringResource(Res.string.settings_auto_resume), if (s[AppKeys.autoResume]) always else never, listOf(always, never)) { set(AppKeys.autoResume, it == always) }
                                Choice(stringResource(Res.string.settings_dl_subtitle), if (s[AppKeys.downloadSubtitle]) sysLang else off, listOf(sysLang, off), last = true) { set(AppKeys.downloadSubtitle, it != off) }
                            }
                            Block { Toggle(stringResource(Res.string.settings_clipboard), stringResource(Res.string.settings_clipboard_sub), s[AppKeys.clipboardDetect], last = true) { set(AppKeys.clipboardDetect, it) } }
                            Block { Toggle(stringResource(Res.string.settings_hidden), stringResource(Res.string.settings_hidden_sub), s[AppKeys.showHidden], last = true) { set(AppKeys.showHidden, it) } }
                            Block { Toggle(stringResource(Res.string.settings_complete_notify), null, s[AppKeys.completeNotify], last = true) { set(AppKeys.completeNotify, it) } }
                        }
                        G.CONTENT -> {
                            Block {
                                Choice(stringResource(Res.string.settings_language), s[AppKeys.language], G.LANGUAGES) { viewModel.setEvent(SettingsContract.Event.Language(it)) }
                                Choice(stringResource(Res.string.settings_location), s[AppKeys.location], G.LOCATIONS, last = true,
                                    label = { code -> locationNames[code]?.let { stringResource(it) } ?: code }) { viewModel.setEvent(SettingsContract.Event.Location(it)) }
                            }
                            Block { Toggle(stringResource(Res.string.settings_restricted), stringResource(Res.string.settings_restricted_sub), s[AppKeys.restricted], last = true) { set(AppKeys.restricted, it) } }
                            Block { Toggle(stringResource(Res.string.settings_sites_filter), stringResource(Res.string.settings_sites_filter_sub), s[AppKeys.hideAdultSites], last = true) { set(AppKeys.hideAdultSites, it) } }
                        }
                        G.OTHERS -> {
                            Block {
                                Choice(stringResource(Res.string.settings_online_quality), s[AppKeys.onlineQuality], G.ONLINE_QUALITY) { set(AppKeys.onlineQuality, it) }
                                Toggle(stringResource(Res.string.settings_autoplay), stringResource(Res.string.settings_autoplay_sub), s[AppKeys.autoPlay]) { set(AppKeys.autoPlay, it) }
                                // The popup window and the notification-bar search exist only on Android.
                                val android = getPlatform().type == PlatformType.Android
                                if (android) Toggle(stringResource(Res.string.settings_autopopup), stringResource(Res.string.settings_autopopup_sub), s[AppKeys.autoPopup]) {
                                    set(AppKeys.autoPopup, it)
                                    // The popup is a window over other apps: ask for that permission as soon as it is switched on.
                                    if (it && !platform.canDrawOverlays()) platform.requestOverlayPermission()
                                }
                                Choice(stringResource(Res.string.settings_theme), s[AppKeys.theme], G.THEMES, last = !android) { set(AppKeys.theme, it) }
                                if (android) Toggle(stringResource(Res.string.settings_quick_search), stringResource(Res.string.settings_quick_search_sub), s[AppKeys.quickSearch], last = true) { set(AppKeys.quickSearch, it) }
                            }
                            Block {
                                Row(s = stringResource(Res.string.settings_reset_pin), last = true) { viewModel.setEvent(SettingsContract.Event.ResetPrivatePassword) }
                            }
                        }
                        G.PUSH -> Block {
                            Toggle(stringResource(Res.string.settings_push), null, s[AppKeys.push]) { set(AppKeys.push, it) }
                            Toggle(stringResource(Res.string.settings_daily_push), null, s[AppKeys.dailyPush], last = true) { set(AppKeys.dailyPush, it) }
                        }
                        G.WIDGETS -> Block {
                            Row(s = stringResource(Res.string.settings_w_quick), sub = stringResource(Res.string.settings_w_quick_sub), value = stringResource(Res.string.settings_add)) { toast(widgetHint) }
                            Row(s = stringResource(Res.string.settings_w_music), sub = stringResource(Res.string.settings_w_music_sub), value = stringResource(Res.string.settings_add)) { toast(widgetHint) }
                            Row(s = stringResource(Res.string.settings_w_hot), sub = stringResource(Res.string.settings_w_hot_sub), value = stringResource(Res.string.settings_add), last = true) { toast(widgetHint) }
                        }
                        G.ABOUT -> Column(Modifier.fillMaxWidth().background(ViColors.surface).padding(horizontal = 24.dp, vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.size(72.dp).clip(RoundedCornerShape(20.dp)).background(Brush.linearGradient(listOf(ViColors.brandLight, VRed))), contentAlignment = Alignment.Center) {
                                Ic(Res.drawable.ic_play_fill, Modifier.size(38.dp), ViColors.white)
                            }
                            Text(stringResource(Res.string.settings_app_name), Modifier.padding(top = 14.dp), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = ViColors.ink)
                            Text(stringResource(Res.string.settings_about_tagline), fontSize = 13.sp, color = ViColors.setSub)
                            Text(stringResource(Res.string.settings_version), Modifier.padding(top = 12.dp).clip(RoundedCornerShape(50)).background(ViColors.tintRed)
                                .padding(horizontal = 12.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = VRed)
                            Text(stringResource(Res.string.settings_about_text), Modifier.padding(top = 20.dp).widthIn(max = 460.dp), fontSize = 14.sp, lineHeight = 20.sp,
                                color = ViColors.ink, textAlign = TextAlign.Center)
                            Text(stringResource(Res.string.settings_about_engine), Modifier.padding(top = 16.dp).widthIn(max = 460.dp), fontSize = 12.sp, lineHeight = 17.sp,
                                color = ViColors.setSub, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
