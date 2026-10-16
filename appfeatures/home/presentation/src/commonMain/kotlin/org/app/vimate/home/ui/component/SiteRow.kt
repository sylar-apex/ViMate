package org.app.vimate.home.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextOverflow
import org.app.vimate.appcore.component.BrandTile
import org.app.vimate.appcore.component.SitesTile
import org.app.vimate.appcore.theme.mutedInk
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.home_sites
import org.app.vimate.home.ui.constant.HomeLayoutSpec
import org.app.vimate.sharedlogic.catalog.BrandSite
import org.jetbrains.compose.resources.stringResource

/** Youtube, Instagram, Facebook, Google and Sites; the brand tiles open the website. */
@Composable
internal fun SiteRow(onSites: () -> Unit) {
    val uri = LocalUriHandler.current
    var widthPx by remember { mutableIntStateOf(0) }
    val cell = with(LocalDensity.current) { (widthPx / (BrandSite.entries.size + 1)).toDp() }
    LazyRow(Modifier.fillMaxWidth().height(HomeLayoutSpec.siteRowHeight).onSizeChanged { widthPx = it.width }, userScrollEnabled = false) {
        items(BrandSite.entries, key = { it.name }) { site ->
            SiteCell(site.label, Modifier.width(cell), onClick = { uri.openUri(site.url) }) { BrandTile(site) }
        }
        item(key = SITES_KEY) {
            SiteCell(stringResource(Res.string.home_sites), Modifier.width(cell), onClick = onSites) { SitesTile() }
        }
    }
}

@Composable
private fun SiteCell(label: String, modifier: Modifier, onClick: () -> Unit, tile: @Composable () -> Unit) {
    Column(modifier.fillMaxHeight().clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(HomeLayoutSpec.siteTop))
        tile()
        Spacer(Modifier.height(HomeLayoutSpec.siteGap))
        Text(label, color = mutedInk, fontSize = HomeLayoutSpec.siteLabel, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

private const val SITES_KEY = "sites"
