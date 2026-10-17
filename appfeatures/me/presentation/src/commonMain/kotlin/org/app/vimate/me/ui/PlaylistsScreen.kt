package org.app.vimate.me.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.vimate.appcore.component.*
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.me.ui.component.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun PlaylistsScreen(viewModel: LibraryViewModel = koinViewModel()) {
    val nav = LocalAppNavigator.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lists = state.playlists
    var create by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().background(ViColors.surface).statusBarsPadding()) {
        PageHeader(stringResource(Res.string.me_youtube_playlist), bold = false, height = 56, titleStart = 16, titleSize = 18f) { nav.back() }
        if (lists.isEmpty()) {
            // VidMate's sign-in intro (logos, title, subtitle), then the red Sign in bar at the bottom.
            Column(Modifier.fillMaxWidth().weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(65.dp))
                LinkLogos(56, Res.drawable.ic_arrows_lr)
                Text(stringResource(Res.string.me_sign_in_with_youtube), Modifier.padding(top = 12.dp), fontWeight = FontWeight.Bold, fontSize = 22.sp, lineHeight = 27.sp, color = ViColors.ink)
                Text(stringResource(Res.string.me_get_online_playlist), fontSize = 15.5.sp, lineHeight = 19.sp, color = ViColors.meGrey)
                TextButton(onClick = { create = true }, Modifier.padding(top = 32.dp)) { Text(stringResource(Res.string.me_create_local_playlist), color = ViColors.meLink) }
            }
            RedSignInBar(Modifier.padding(start = 13.dp, end = 13.dp, bottom = 30.dp), corner = 2, bold = false, fontSize = 17f) { nav.go(Routes.GoogleLogin) }
        } else LazyColumn {
            item(key = "new") { TextButton(onClick = { create = true }, Modifier.padding(8.dp)) { Text(stringResource(Res.string.me_new_playlist_button), color = ViColors.meLink) } }
            items(lists, key = { it.id }) { p ->
                ListItem(headlineContent = { Text(p.name, fontWeight = FontWeight.SemiBold) }, supportingContent = { Text(stringResource(Res.string.me_n_videos, p.videos.size)) },
                    leadingContent = { Thumb(p.videos.firstOrNull()?.thumbnail.orEmpty(), Modifier.width(110.dp).aspectRatio(16f / 9)) },
                    trailingContent = { IconButton(onClick = { viewModel.setEvent(LibraryContract.Event.DeletePlaylist(p.id)) }) { Ic(Res.drawable.ic_trash_t, Modifier.size(20.dp), ViColors.meIconGrey) } },
                    modifier = Modifier.clickable { nav.go(Routes.PlaylistDetail(p.id)) })
            }
        }
    }
    if (create) {
        var name by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { create = false }, title = { Text(stringResource(Res.string.me_new_playlist)) }, text = { OutlinedTextField(name, { name = it }, singleLine = true) },
            confirmButton = { TextButton(enabled = name.isNotBlank(), onClick = { viewModel.setEvent(LibraryContract.Event.CreatePlaylist(name.trim())); create = false }) { Text(stringResource(Res.string.me_create)) } })
    }
}

@Composable
fun PlaylistDetailScreen(id: Long, viewModel: LibraryViewModel = koinViewModel()) {
    val nav = LocalAppNavigator.current
    val toast = LocalToast.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.effect.collectLatest { if (it is LibraryContract.Effect.Error) toast(it.message) } }
    val p = state.playlists.find { it.id == id } ?: return
    Column(Modifier.fillMaxSize().background(ViColors.surface).statusBarsPadding()) {
        PageHeader(p.name) { nav.back() }
        TextButton(onClick = { viewModel.setEvent(LibraryContract.Event.PlayAllAudio(p.videos)) }, Modifier.padding(start = 8.dp)) { Text(stringResource(Res.string.me_play_all_audio), color = VRed) }
        LazyColumn {
            items(p.videos, key = { it.id }) { v ->
                VideoRow(v, { nav.go(Routes.Detail.of(v)) }) {
                    IconButton(onClick = { viewModel.setEvent(LibraryContract.Event.RemoveFromPlaylist(id, v)) }) { Ic(Res.drawable.ic_close, Modifier.size(20.dp), ViColors.meIconGrey) }
                }
            }
        }
    }
}
