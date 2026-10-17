package org.app.vimate.search.ui.component

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.app.vimate.appcore.component.Empty
import org.app.vimate.appcore.component.ErrorBox
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.component.SongRow
import org.app.vimate.appcore.component.VideoCard
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.common_network_error
import org.app.vimate.appres.resources.search_no_results
import org.app.vimate.search.viewmodel.SearchContract.Event
import org.app.vimate.search.viewmodel.SearchContract.State
import org.jetbrains.compose.resources.stringResource

/** YouTube results with continuation paging (All / Playlist tabs). */
@Composable
internal fun SearchResults(state: State, onEvent: (Event) -> Unit) {
    val nav = LocalAppNavigator.current
    LazyColumn {
        itemsIndexed(state.results, key = { _, v -> v.id }) { i, v ->
            if (i >= state.results.size - 3) LaunchedEffect(state.results.size) { onEvent(Event.LoadMore) }
            VideoCard(v, onDownload = { nav.download(v) })
        }
        item(key = "footer") {
            if (state.loading) Loading()
            else if (state.error != null) ErrorBox(state.error!!) { onEvent(Event.Retry) }
            else if (state.results.isEmpty()) Empty(stringResource(Res.string.search_no_results))
        }
    }
}

/** YouTube Music songs; tap plays (or opens Now Playing), download opens the sheet. */
@Composable
internal fun MusicResults(state: State, onEvent: (Event) -> Unit) {
    val nav = LocalAppNavigator.current
    val networkError = stringResource(Res.string.common_network_error)
    LazyColumn {
        itemsIndexed(state.songs, key = { i, v -> "${v.id}_$i" }) { i, v ->
            SongRow(v, onPlay = { onEvent(Event.PlaySong(i)) }, onDownload = { nav.download(v) })
        }
        item(key = "footer") {
            if (state.musicLoading) Loading() else state.musicError?.let { ErrorBox(it.ifEmpty { networkError }) { onEvent(Event.Retry) } }
        }
    }
}
