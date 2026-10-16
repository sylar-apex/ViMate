package org.app.vimate.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.math.roundToInt
import org.app.vimate.appcore.component.LocalBottomInset
import org.app.vimate.appcore.theme.mutedInk
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.home_empty
import org.app.vimate.home.ui.component.CardMenuActions
import org.app.vimate.home.ui.component.FeedCard
import org.app.vimate.home.ui.component.FeedCardActions
import org.app.vimate.home.ui.component.HomeHeader
import org.app.vimate.home.ui.component.HomeHeaderActions
import org.app.vimate.home.ui.component.SiteRow
import org.app.vimate.home.ui.constant.HomeLayoutSpec
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/** Where Home leads. */
class HomeNavigation(
    val onSearch: () -> Unit,
    val onNotifications: () -> Unit,
    val onSites: () -> Unit,
    val onDownloads: () -> Unit,
    val onShorts: () -> Unit,
    val onDetail: (String) -> Unit,
)

@Composable
fun HomeScreen(navigation: HomeNavigation, viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeContract.Effect.OpenDetail -> navigation.onDetail(effect.mediaId)
                HomeContract.Effect.OpenShorts -> navigation.onShorts()
                HomeContract.Effect.ScrollToTop -> listState.scrollToItem(0)
            }
        }
    }
    HomeContent(state, listState, navigation, viewModel::setEvent)
}

@Composable
fun HomeContent(
    state: HomeContract.State,
    listState: LazyListState = rememberLazyListState(),
    navigation: HomeNavigation = HomeNavigation({}, {}, {}, {}, {}, {}),
    onEvent: (HomeContract.Event) -> Unit = {},
) {
    val headerPx = with(LocalDensity.current) { HomeLayoutSpec.headerHeight.toPx() }
    var headerOffset by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(state.chip) { headerOffset = 0f }
    // The header slides away while scrolling down and comes back on any scroll up.
    val connection = remember(headerPx) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                headerOffset = (headerOffset + available.y).coerceIn(-headerPx, 0f)
                return Offset.Zero
            }
        }
    }
    val cardActions = remember(onEvent) {
        FeedCardActions(
            onOpen = { onEvent(HomeContract.Event.OpenMedia(it)) },
            onToggleBasket = { onEvent(HomeContract.Event.ToggleBasket(it)) },
            onDownload = { onEvent(HomeContract.Event.Download(it)) },
            menu = CardMenuActions(
                onWatchLater = { onEvent(HomeContract.Event.ToggleWatchLater(it)) },
                onShare = { onEvent(HomeContract.Event.Share(it)) },
                onNotInterested = { onEvent(HomeContract.Event.NotInterested(it)) },
                onReport = { onEvent(HomeContract.Event.Report(it)) },
            ),
        )
    }
    val headerActions = remember(navigation, onEvent) {
        HomeHeaderActions(
            onSearch = navigation.onSearch,
            onNotifications = navigation.onNotifications,
            onSites = navigation.onSites,
            onDownloads = navigation.onDownloads,
            onShorts = { onEvent(HomeContract.Event.OpenShorts) },
            onChip = { onEvent(HomeContract.Event.SelectChip(it)) },
        )
    }
    val feed = state.feed
    Box(Modifier.fillMaxSize().nestedScroll(connection)) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = HomeLayoutSpec.headerHeight, bottom = LocalBottomInset.current + HomeLayoutSpec.cardPadding),
        ) {
            item(key = SITES_KEY) { SiteRow(navigation.onSites) }
            items(feed.items, key = { it.id }) { media ->
                FeedCard(media, media.id in feed.basket, media.id in feed.watchLater, cardActions)
            }
            if (feed.items.isEmpty()) {
                item(key = EMPTY_KEY) {
                    Text(
                        stringResource(Res.string.home_empty), color = mutedInk, fontSize = 14.sp, textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(HomeLayoutSpec.emptyPadding),
                    )
                }
            }
        }
        HomeHeader(feed.chips, state.chip, feed.shortsSeen, headerActions, Modifier.offset { IntOffset(0, headerOffset.roundToInt()) })
    }
}

private const val SITES_KEY = "sites"
private const val EMPTY_KEY = "empty"
