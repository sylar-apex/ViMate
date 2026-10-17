package org.app.vimate.me.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.component.hotVideos
import org.app.vimate.appcore.component.rememberShareText
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.me.domain.constant.MeUrls
import org.app.vimate.me.ui.component.NotchedCard
import org.app.vimate.me.ui.component.Shortcut
import org.app.vimate.me.ui.component.SiteIcon
import org.app.vimate.me.ui.component.ThemeSwitch
import org.app.vimate.me.ui.component.ToolCell
import org.app.vimate.me.ui.constant.MeTool
import org.app.vimate.me.ui.constant.hotSites
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/** ME tab. Geometry measured from VidMate 6.0302 ME tab (3px = 1dp). */
@Composable
fun MeScreen(viewModel: MeViewModel = koinViewModel()) {
    val nav = LocalAppNavigator.current
    val toast = LocalToast.current
    val share = rememberShareText()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val shareText = stringResource(Res.string.me_share_text)
    val thanks = stringResource(Res.string.me_thanks)
    val latest = stringResource(Res.string.me_latest_version)
    MeContent(
        hot = state.hot, dark = state.dark,
        onSetDark = { viewModel.setEvent(MeContract.Event.SetDark(it)) },
        onSignIn = { nav.go(Routes.Browser(MeUrls.SIGN_IN_PILL)) },
        onFiles = { nav.home(Tab.FILES) },
        onRoute = { nav.go(it) },
        onTool = { t ->
            when (t) {
                MeTool.SHARE -> share(shareText)
                MeTool.LIKE -> toast(thanks)
                MeTool.UPDATE -> toast(latest)
                MeTool.FEEDBACK -> nav.go(Routes.Feedback)
                MeTool.HELP -> nav.go(Routes.Help)
                MeTool.SETTINGS -> nav.go(Routes.Settings)
                MeTool.JUNK -> nav.go(Routes.JunkClean)
                MeTool.BOOST -> nav.go(Routes.Boost)
            }
        },
        onDownload = { nav.downloadFor = it },
        onSite = { nav.go(Routes.Browser(it)) },
    )
}

@Composable
fun MeContent(
    hot: List<Video>, dark: Boolean, onSetDark: (Boolean) -> Unit, onSignIn: () -> Unit, onFiles: () -> Unit,
    onRoute: (Any) -> Unit, onTool: (MeTool) -> Unit, onDownload: (Video) -> Unit, onSite: (String) -> Unit,
) {
    LazyColumn(Modifier.fillMaxSize().background(ViColors.page).statusBarsPadding(), contentPadding = PaddingValues(bottom = 80.dp)) {
        item(key = "profile") {
            // Card 1: 336x159dp at (12, 17.7): sign-in pill, theme switch, 1dp rule, 4 red shortcuts.
            Box(Modifier.padding(start = 12.dp, end = 12.dp, top = 17.7.dp).fillMaxWidth().height(159.dp)) {
                // Pale bubble that shows through VidMate's notch behind the day/night switch.
                Box(Modifier.align(Alignment.TopEnd).padding(top = 9.dp).size(112.dp, 70.dp).clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(Brush.horizontalGradient(listOf(ViColors.meBubble, ViColors.white))))
                Column(Modifier.fillMaxSize().clip(NotchedCard).background(ViColors.white)) {
                    Row(Modifier.fillMaxWidth().height(78.dp).padding(start = 12.dp, end = 11.dp), verticalAlignment = Alignment.CenterVertically) {
                        Row(Modifier.size(107.dp, 40.dp).border(2.dp, ViColors.ink, CircleShape).clip(CircleShape).clickable(onClick = onSignIn)
                            .padding(start = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Ic(Res.drawable.ic_person, Modifier.size(24.dp), ViColors.ink)
                            Text(stringResource(Res.string.me_sign_in), Modifier.padding(start = 6.dp), fontSize = 16.4.sp, lineHeight = 21.7.sp, color = ViColors.ink)
                        }
                    }
                    Box(Modifier.padding(horizontal = 12.dp).fillMaxWidth().height(0.7.dp).background(ViColors.meRule))
                    Row(Modifier.padding(horizontal = 12.dp).fillMaxWidth().weight(1f)) {
                        Shortcut(Res.drawable.ic_me_files, stringResource(Res.string.me_my_files), onClick = onFiles)
                        Shortcut(Res.drawable.ic_me_history, stringResource(Res.string.me_history), size = 28f) { onRoute(Routes.History) }
                        Shortcut(Res.drawable.ic_me_subs, stringResource(Res.string.me_subscription), size = 27f, dx = 1.7f, dy = 1.7f) { onRoute(Routes.Subscriptions) }
                        Shortcut(Res.drawable.ic_me_playlist, stringResource(Res.string.me_playlist), size = 29f, dx = -1.5f, dy = -2f) { onRoute(Routes.Playlists) }
                    }
                }
                Box(Modifier.align(Alignment.TopEnd).padding(top = 20.dp, end = 11.dp)) { ThemeSwitch(dark, onSetDark) }
            }
        }
        item(key = "tools") {
            // Card 2: 336x150dp, 16dp below; 4x2 cells of 84x68dp, 28dp outline icons.
            LazyVerticalGrid(GridCells.Fixed(4), Modifier.padding(start = 12.dp, end = 12.dp, top = 16.3.dp).fillMaxWidth().height(150.dp)
                .clip(RoundedCornerShape(20.dp)).background(ViColors.white).padding(top = 14.dp), userScrollEnabled = false) {
                items(MeTool.entries, key = { it.name }) { t ->
                    Box(Modifier.height(68.dp)) { ToolCell(t.icon, stringResource(t.label), t.iconSize, t == MeTool.LIKE) { onTool(t) } }
                }
            }
        }
        item(key = "gap") { Spacer(Modifier.height(10.dp)) }
        hotVideos(hot, onDownload)
        item(key = "sites") {
            Text(stringResource(Res.string.me_hot_sites), fontWeight = FontWeight.Bold, fontSize = 17.sp, color = ViColors.ink, modifier = Modifier.padding(start = 15.dp, top = 8.dp, bottom = 8.dp))
            LazyRow(contentPadding = PaddingValues(horizontal = 8.dp)) {
                items(hotSites.filter { !it.adult }, key = { it.url }) { site -> SiteIcon(site.name, site.icon, site.color) { onSite(site.url) } }
            }
        }
    }
}
