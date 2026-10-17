package org.app.vimate.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.app.vimate.music.ui.component.text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.ErrorBox
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.component.VideoRow
import org.app.vimate.appcore.component.rememberShareText
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_d_download
import org.app.vimate.appres.resources.ic_d_save
import org.app.vimate.appres.resources.ic_d_share
import org.app.vimate.appres.resources.ic_download
import org.app.vimate.appres.resources.ic_play
import org.app.vimate.appres.resources.music_play
import org.app.vimate.appres.resources.music_playlist_detail
import org.app.vimate.appres.resources.music_save
import org.app.vimate.appres.resources.music_share
import org.app.vimate.appres.resources.music_videos_count
import org.app.vimate.music.ui.component.PageHeader
import org.jetbrains.compose.resources.stringResource

/** VidMate "PlayList Detail": title + owner, play/share/save row, red download-all button, rows with download and menu. */
@Composable
fun YtPlaylistScreen(listId: String, viewModel: YtPlaylistViewModel = koinViewModel(key = "ytpl:$listId")) {
    LaunchedEffect(listId) { viewModel.setEvent(YtPlaylistContract.Event.Load(listId)) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    YtPlaylistContent(listId, state, viewModel::setEvent)
}

@Composable
fun YtPlaylistContent(listId: String, state: YtPlaylistContract.State, onEvent: (YtPlaylistContract.Event) -> Unit) {
    val nav = LocalAppNavigator.current
    val share = rememberShareText()
    val p = state
    Column(Modifier.fillMaxSize().background(ViColors.surface).statusBarsPadding()) {
        PageHeader(stringResource(Res.string.music_playlist_detail)) { nav.back() }
        LazyColumn(Modifier.fillMaxSize()) {
            item {
                Column(Modifier.fillMaxWidth().background(ViColors.hotSites).padding(start = 13.dp, top = 16.dp, end = 13.dp)) {
                    Text(p.title, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = ViColors.ink, maxLines = 2)
                    Text(p.owner, Modifier.padding(top = 6.dp), fontSize = 15.sp, color = ViColors.metaGrey)
                    Row(Modifier.padding(top = 18.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(30.dp)) {
                        Ic(Res.drawable.ic_play, Modifier.size(28.dp).clickable { p.videos.firstOrNull()?.let { nav.go(Routes.Detail.of(it)) } }, ViColors.ink, stringResource(Res.string.music_play))
                        Ic(Res.drawable.ic_d_share, Modifier.size(28.dp).clickable { share("https://www.youtube.com/playlist?list=$listId") }, ViColors.ink, stringResource(Res.string.music_share))
                        Ic(Res.drawable.ic_d_save, Modifier.size(28.dp), ViColors.ink, stringResource(Res.string.music_save))
                        Spacer(Modifier.weight(1f))
                        Box(Modifier.size(40.dp).clip(CircleShape).background(VRed).clickable {
                            p.videos.takeIf { it.isNotEmpty() }?.let { nav.go(Routes.BatchSelect.of(it)) }
                        }, contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_d_download, Modifier.size(22.dp), ViColors.white) }
                    }
                }
                Text(stringResource(Res.string.music_videos_count, p.videos.size), Modifier.padding(13.dp), fontSize = 15.sp, color = ViColors.metaGrey)
            }
            if (state.loading) item { Loading() }
            state.error?.let { e -> item { ErrorBox(e.text()) { onEvent(YtPlaylistContract.Event.Retry) } } }
            items(p.videos, key = { it.id }) { v ->
                VideoRow(v, { nav.go(Routes.Detail.of(v)) }) {
                    Box(Modifier.size(32.dp).clickable { nav.download(v) }, contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_download, Modifier.size(22.dp), ViColors.metaGrey) }
                }
            }
        }
    }
}
