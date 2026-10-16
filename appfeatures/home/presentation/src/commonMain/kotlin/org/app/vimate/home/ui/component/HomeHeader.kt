package org.app.vimate.home.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.isViDark
import org.app.vimate.appcore.theme.tabCanvas
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.home_downloads
import org.app.vimate.appres.resources.home_notifications
import org.app.vimate.appres.resources.home_search_hint
import org.app.vimate.appres.resources.home_sites
import org.app.vimate.appres.resources.ic_bell_mark
import org.app.vimate.appres.resources.ic_download
import org.app.vimate.appres.resources.ic_search
import org.app.vimate.appres.resources.ic_sites_mark
import org.app.vimate.appres.resources.search
import org.app.vimate.home.ui.constant.HomeLayoutSpec
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Search pill, notification bell, sites and downloads, then the Shorts chip and the genre chips. */
@Composable
internal fun HomeHeader(
    chips: List<String>,
    selectedChip: String,
    shortsSeen: Boolean,
    actions: HomeHeaderActions,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth().height(HomeLayoutSpec.headerHeight).background(tabCanvas)) {
        SearchRow(actions)
        Row(Modifier.fillMaxWidth().height(HomeLayoutSpec.chipRowHeight)) {
            ShortsChip(seen = shortsSeen, onClick = actions.onShorts)
            FeedChips(chips, selectedChip, actions.onChip, Modifier.weight(1f))
        }
    }
}

/** Header callbacks, one per tappable item. */
internal class HomeHeaderActions(
    val onSearch: () -> Unit,
    val onNotifications: () -> Unit,
    val onSites: () -> Unit,
    val onDownloads: () -> Unit,
    val onShorts: () -> Unit,
    val onChip: (String) -> Unit,
)

@Composable
private fun SearchRow(actions: HomeHeaderActions) {
    val surface = MaterialTheme.colorScheme.surface
    val ink = MaterialTheme.colorScheme.onSurface
    Row(Modifier.fillMaxWidth().height(HomeLayoutSpec.searchRowHeight)) {
        Row(
            Modifier.padding(start = HomeLayoutSpec.searchStart, top = HomeLayoutSpec.searchTop).weight(1f).height(HomeLayoutSpec.searchHeight)
                .clip(CircleShape).background(surface).clickable(onClick = actions.onSearch).padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painterResource(Res.drawable.ic_search), stringResource(Res.string.search),
                tint = if (isViDark()) ink else ViColors.searchIcon, modifier = Modifier.size(HomeLayoutSpec.searchIcon),
            )
            Spacer(Modifier.width(6.dp))
            Text(
                stringResource(Res.string.home_search_hint), color = ViColors.hint, fontSize = HomeLayoutSpec.searchText,
                maxLines = 1, overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(3.dp))
        Box(Modifier.size(HomeLayoutSpec.bellBox, HomeLayoutSpec.searchRowHeight).clickable(onClick = actions.onNotifications), contentAlignment = Alignment.Center) {
            Image(painterResource(Res.drawable.ic_bell_mark), stringResource(Res.string.home_notifications), Modifier.size(HomeLayoutSpec.bellIcon))
        }
        Spacer(Modifier.width(2.dp))
        Box(
            Modifier.padding(top = HomeLayoutSpec.iconTop).size(HomeLayoutSpec.sitesBoxWidth, HomeLayoutSpec.iconBoxHeight).clickable(onClick = actions.onSites),
            contentAlignment = Alignment.Center,
        ) {
            Image(painterResource(Res.drawable.ic_sites_mark), stringResource(Res.string.home_sites), Modifier.size(HomeLayoutSpec.bellIcon))
        }
        Box(
            Modifier.padding(top = HomeLayoutSpec.iconTop).size(HomeLayoutSpec.bellBox, HomeLayoutSpec.iconBoxHeight).clickable(onClick = actions.onDownloads),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(Res.drawable.ic_download), stringResource(Res.string.home_downloads), tint = ink, modifier = Modifier.size(HomeLayoutSpec.downloadIcon))
        }
        Spacer(Modifier.width(4.dp))
    }
}
