package org.app.vimate.music.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.app.vimate.music.ui.component.CollectMusicEffects
import org.app.vimate.music.ui.component.text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.component.ErrorBox
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.music_back
import org.app.vimate.appres.resources.music_search_hint
import org.app.vimate.music.ui.component.SongRow
import org.jetbrains.compose.resources.stringResource

/** YouTube Music search (POC MusicSearchScreen). */
@Composable
fun MusicSearchScreen(viewModel: MusicSearchViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CollectMusicEffects(viewModel.effect)
    MusicSearchContent(state, viewModel::setEvent)
}

@Composable
fun MusicSearchContent(state: MusicSearchContract.State, onEvent: (MusicSearchContract.Event) -> Unit) {
    val nav = LocalAppNavigator.current
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }
    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 12.dp)) {
            IconButton(onClick = { nav.back() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(Res.string.music_back)) }
            OutlinedTextField(state.query, { onEvent(MusicSearchContract.Event.Query(it)) }, Modifier.weight(1f).focusRequester(focus), placeholder = { Text(stringResource(Res.string.music_search_hint)) }, singleLine = true, shape = RoundedCornerShape(24.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search), keyboardActions = KeyboardActions(onSearch = { onEvent(MusicSearchContract.Event.Submit) }))
        }
        LazyColumn {
            val songs = state.songs
            itemsIndexed(songs, key = { i, v -> "$i${v.id}" }) { i, v ->
                SongRow(v, onPlay = { onEvent(MusicSearchContract.Event.Tap(i)) }, onDownload = { nav.download(v) })
            }
            item { if (state.loading) Loading() else state.error?.let { e -> ErrorBox(e.text()) { onEvent(MusicSearchContract.Event.Retry) } } }
        }
    }
}
