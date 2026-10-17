package org.app.vimate.home.ui.wide

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.adaptive.WideVideoCard
import org.app.vimate.appcore.component.ErrorBox
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.wide_quick_access
import org.app.vimate.appres.resources.wide_recommended
import org.app.vimate.home.viewmodel.HomeContract.Event
import org.app.vimate.home.viewmodel.HomeContract.State
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Tablet / desktop Home: top bar with search, topic chips, quick-access sites, then a card grid. */
@Composable
internal fun HomeWide(state: State, onEvent: (Event) -> Unit) {
    val nav = LocalAppNavigator.current
    val grid = remember(state.chip) { LazyGridState() }
    val nearEnd by remember(grid) { derivedStateOf { (grid.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0) >= grid.layoutInfo.totalItemsCount - 6 } }
    LaunchedEffect(nearEnd) { if (nearEnd && state.feed.isNotEmpty()) onEvent(Event.LoadMore) }

    Column(Modifier.fillMaxSize().background(ViColors.wideCanvas).statusBarsPadding()) {
        WideTopBar(state.activeDownloads)
        WideChips(state.chip) { onEvent(Event.SelectChip(it)) }
        LazyVerticalGrid(
            GridCells.Adaptive(WideHomeSpec.cardMin), Modifier.fillMaxSize(), grid,
            contentPadding = PaddingValues(start = WideHomeSpec.gutter, end = WideHomeSpec.gutter, bottom = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(WideHomeSpec.gapX),
            verticalArrangement = Arrangement.spacedBy(WideHomeSpec.gapY),
        ) {
            item(key = "header", span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    SectionTitle(Res.string.wide_quick_access)
                    QuickSites()
                    SectionTitle(Res.string.wide_recommended, last = true)
                }
            }
            items(state.feed, key = { it.id }) { v -> WideVideoCard(v, onDownload = { nav.download(v) }) }
            item(key = "footer", span = { GridItemSpan(maxLineSpan) }) {
                when {
                    state.loading -> Loading()
                    state.error != null -> ErrorBox(state.error!!) { onEvent(Event.Retry) }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: StringResource, last: Boolean = false) {
    Text(
        stringResource(text), Modifier.padding(top = 22.dp, bottom = if (last) 0.dp else 14.dp),
        fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold, color = ViColors.ink,
    )
}
