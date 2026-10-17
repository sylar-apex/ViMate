package org.app.vimate.home.ui.wide

import androidx.compose.foundation.layout.BoxWithConstraints
import org.app.vimate.appcore.component.ShimmerCardGrid
import org.app.vimate.appcore.adaptive.LocalInTitleBar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import org.app.vimate.appcore.adaptive.WideSpec
import org.app.vimate.appcore.adaptive.WideVideoCard
import org.app.vimate.appcore.adaptive.bleed
import org.app.vimate.appcore.navigation.Routes
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
        // macOS: the shell shows this bar in the window title bar instead.
        if (!LocalInTitleBar.current) WideTopBar(state.activeDownloads)
        LazyVerticalGrid(
            GridCells.Adaptive(WideHomeSpec.cardMin), Modifier.fillMaxSize(), grid,
            contentPadding = PaddingValues(start = WideHomeSpec.gutter, end = WideHomeSpec.gutter, bottom = WideSpec.bottomInset),
            horizontalArrangement = Arrangement.spacedBy(WideHomeSpec.gapX),
            verticalArrangement = Arrangement.spacedBy(WideHomeSpec.gapY),
        ) {
            item(key = "header", span = { GridItemSpan(maxLineSpan) }) {
                Column {
                    Box(Modifier.padding(top = if (LocalInTitleBar.current) 16.dp else 0.dp)) { HomeBanner() }
                    Box(Modifier.padding(top = 18.dp).bleed(WideHomeSpec.gutter)) { WideChips(state.chip) { onEvent(Event.SelectChip(it)) } }
                    SeeAllTitle(Res.string.wide_quick_access) { nav.go(Routes.Sites) }
                    QuickSites()
                    SeeAllTitle(Res.string.wide_recommended, last = true) { nav.go(Routes.Search()) }
                }
            }
            items(state.feed, key = { it.id }) { v -> WideVideoCard(v, onDownload = { nav.download(v) }) }
            item(key = "footer", span = { GridItemSpan(maxLineSpan) }) {
                when {
                    // Placeholder cards in the feed's own column count, so loading looks like the content.
                    state.loading -> BoxWithConstraints { ShimmerCardGrid(((maxWidth + WideHomeSpec.gapX) / (WideHomeSpec.cardMin + WideHomeSpec.gapX)).toInt().coerceAtLeast(1), if (state.feed.isEmpty()) 2 else 1, WideHomeSpec.gapX) }
                    state.error != null -> ErrorBox(state.error!!) { onEvent(Event.Retry) }
                }
            }
        }
    }
}
