package org.app.vimate.music.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.app.vimate.music.ui.component.CollectMusicEffects
import org.app.vimate.music.ui.component.text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.component.BackBar
import org.app.vimate.appcore.component.Empty
import org.app.vimate.appcore.component.ErrorBox
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.music_no_songs
import org.app.vimate.appres.resources.music_play_all
import org.app.vimate.appres.resources.music_shuffle
import org.app.vimate.music.ui.component.SongRow
import org.jetbrains.compose.resources.stringResource

/** Album / playlist / mood of YouTube Music (POC MusicCollectionScreen). */
@Composable
fun MusicCollectionScreen(r: Routes.MusicCollection, viewModel: MusicCollectionViewModel = koinViewModel(key = "mc:${r.browseId}|${r.params}")) {
    LaunchedEffect(r) { viewModel.setEvent(MusicCollectionContract.Event.Load(r.browseId, r.params)) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CollectMusicEffects(viewModel.effect)
    MusicCollectionContent(r.title, state, viewModel::setEvent)
}

@Composable
fun MusicCollectionContent(routeTitle: String, state: MusicCollectionContract.State, onEvent: (MusicCollectionContract.Event) -> Unit) {
    val nav = LocalAppNavigator.current
    val songs = state.songs
    val noSongs = stringResource(Res.string.music_no_songs)
    BoxWithConstraints(Modifier.fillMaxSize()) {
    // Desktop / tablets: same cover card + numbered list as YouTube playlists; tapping a song plays it as music.
    if (maxWidth >= 840.dp) return@BoxWithConstraints WideCollection(
        key = routeTitle, title = state.title?.ifEmpty { null } ?: routeTitle, owner = "", videos = songs, loading = state.loading,
        error = state.error?.let { e -> { ErrorBox(e.text()) { onEvent(MusicCollectionContract.Event.Retry) } } }, shareUrl = null,
        onPlayAll = { onEvent(MusicCollectionContract.Event.PlayAll) }, onOpen = { i, _ -> onEvent(MusicCollectionContract.Event.Tap(i)) },
        onShuffle = { onEvent(MusicCollectionContract.Event.Shuffle) },
    )
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        BackBar(state.title?.ifEmpty { null } ?: routeTitle, { nav.back() })
        LazyColumn {
            item {
                Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { onEvent(MusicCollectionContract.Event.PlayAll) }, enabled = songs.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = VRed)) { Icon(Icons.Filled.PlayArrow, null); Text(" " + stringResource(Res.string.music_play_all)) }
                    OutlinedButton(onClick = { onEvent(MusicCollectionContract.Event.Shuffle) }, enabled = songs.isNotEmpty()) {
                        Icon(Icons.Filled.Shuffle, null); Text(" " + stringResource(Res.string.music_shuffle))
                    }
                }
            }
            itemsIndexed(songs, key = { i, v -> "$i${v.id}" }) { i, v ->
                SongRow(v, onPlay = { onEvent(MusicCollectionContract.Event.Tap(i)) }, onDownload = { nav.download(v) })
            }
            item { if (state.loading) Loading() else state.error?.let { e -> ErrorBox(e.text()) { onEvent(MusicCollectionContract.Event.Retry) } } ?: if (songs.isEmpty()) Empty(noSongs) else Unit }
        }
    }
    }
}
