package org.app.vimate.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.adaptive.feedColumns
import org.app.vimate.appcore.component.ErrorBox
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.component.VideoCard
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.home.ui.component.ChipRow
import org.app.vimate.home.ui.component.SearchBarRow
import org.app.vimate.home.ui.component.SiteShortcuts
import org.app.vimate.home.viewmodel.HomeContract.Event
import org.app.vimate.home.viewmodel.HomeContract.State

// Geometry measured from VidMate 6.0302 at 480dpi (3px = 1dp).
@Composable
fun HomeContent(state: State, onEvent: (Event) -> Unit) {
    val nav = LocalAppNavigator.current
    val list = remember(state.chip) { LazyGridState() }
    val nearEnd by remember(list) { derivedStateOf { (list.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) >= list.layoutInfo.totalItemsCount - 4 } }
    LaunchedEffect(nearEnd) { if (nearEnd && state.feed.isNotEmpty()) onEvent(Event.LoadMore) }

    Column(Modifier.fillMaxSize().background(ViColors.page).statusBarsPadding()) {
        SearchBarRow(state.activeDownloads)
        ChipRow(state.chip, onChip = { onEvent(Event.SelectChip(it)) })
        // One column on phones, a card grid on tablets and desktop.
        LazyVerticalGrid(feedColumns(), Modifier.fillMaxSize(), state = list, contentPadding = PaddingValues(bottom = 80.dp)) {
            item(key = "shortcuts", span = { GridItemSpan(maxLineSpan) }) { SiteShortcuts() }
            items(state.feed, key = { it.id }) { v -> VideoCard(v, onDownload = { nav.download(v) }) }
            item(key = "footer", span = { GridItemSpan(maxLineSpan) }) {
                when {
                    state.loading -> Loading()
                    state.error != null -> ErrorBox(state.error!!) { onEvent(Event.Retry) }
                }
            }
        }
    }
}
