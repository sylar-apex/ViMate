package org.app.vimate.home.ui.wide

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.adaptive.hover
import org.app.vimate.appcore.adaptive.rememberHover
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.home.ui.component.ShortcutLogo
import org.app.vimate.home.ui.component.SitesMosaic
import org.app.vimate.home.ui.component.chipLabels
import org.app.vimate.home.ui.component.shortcuts
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource

/** Spacing of the wide Home page. */
internal object WideHomeSpec {
    val gutter = 32.dp
    val gapX = 22.dp
    val gapY = 30.dp
    val cardMin = 300.dp
    val searchMax = 680.dp
}

/** Search pill (opens Search) centred, with Downloads and Me on the right. */
@Composable
internal fun WideTopBar(activeDownloads: Int) {
    val nav = LocalAppNavigator.current
    val h = rememberHover()
    val border by animateColorAsState(if (h.hovered) ViColors.ink.copy(alpha = .35f) else ViColors.searchBorder)
    Row(
        Modifier.fillMaxWidth().padding(horizontal = WideHomeSpec.gutter).padding(top = 18.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            Modifier.weight(1f, fill = false).widthIn(max = WideHomeSpec.searchMax).fillMaxWidth().height(48.dp)
                .shadow(2.dp, CircleShape, ambientColor = ViColors.cardShadow, spotColor = ViColors.cardShadow)
                .clip(CircleShape).background(Color.White).border(1.dp, border, CircleShape)
                .hover(h).clickable { nav.go(Routes.Search()) }.padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Ic(Res.drawable.ic_search, Modifier.size(22.dp), ViColors.searchIcon)
            Text(stringResource(Res.string.wide_search_hint), Modifier.padding(start = 12.dp).weight(1f), color = ViColors.hint, fontSize = 15.sp, maxLines = 1)
        }
        Spacer(Modifier.weight(.001f).widthIn(min = 16.dp))
        RoundAction(Res.drawable.ic_download, stringResource(Res.string.home_downloads), activeDownloads) { nav.home(Tab.FILES) }
        Spacer(Modifier.width(10.dp))
        RoundAction(Res.drawable.ic_nav_me, stringResource(Res.string.wide_me), 0) { nav.home(Tab.ME) }
    }
}

@Composable
private fun RoundAction(icon: DrawableResource, label: String, badge: Int, onClick: () -> Unit) {
    val h = rememberHover()
    val bg by animateColorAsState(if (h.hovered) ViColors.hover else Color.White)
    Box {
        Box(
            Modifier.size(44.dp).clip(CircleShape).background(bg).border(1.dp, ViColors.wideHairline, CircleShape).hover(h).clickable(onClick = onClick),
            contentAlignment = Alignment.Center,
        ) { Ic(icon, Modifier.size(21.dp), ViColors.ink, label) }
        if (badge > 0) Box(
            Modifier.align(Alignment.TopEnd).size(18.dp).clip(CircleShape).background(VRed).border(2.dp, ViColors.wideCanvas, CircleShape),
            contentAlignment = Alignment.Center,
        ) { Text("$badge", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
    }
}

/** Shorts entry then topic chips; selected chip is solid ink, others soft grey with hover. */
@Composable
internal fun WideChips(selected: Int, onChip: (Int) -> Unit) {
    val nav = LocalAppNavigator.current
    LazyRow(
        contentPadding = PaddingValues(horizontal = WideHomeSpec.gutter, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(key = "shorts") {
            Chip(stringResource(Res.string.home_shorts), false, Res.drawable.ic_shorts) { nav.go(Routes.Shorts()) }
        }
        itemsIndexed(chipLabels, key = { i, _ -> i }) { i, label -> Chip(stringResource(label), i == selected) { onChip(i) } }
    }
}

@Composable
private fun Chip(text: String, selected: Boolean, icon: DrawableResource? = null, onClick: () -> Unit) {
    val h = rememberHover()
    val bg by animateColorAsState(
        when {
            selected -> ViColors.ink
            h.hovered -> ViColors.searchBorder
            else -> ViColors.chipIdle
        },
    )
    Row(
        Modifier.height(36.dp).clip(RoundedCornerShape(10.dp)).background(bg).hover(h).clickable(onClick = onClick).padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) Ic(icon, Modifier.padding(end = 7.dp).size(18.dp), ViColors.shortsChipRed)
        Text(text, color = if (selected) Color.White else ViColors.ink, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

/** Site tiles: white cards with the brand logo, lifting on hover; last one opens all sites. */
@Composable
internal fun QuickSites() {
    val nav = LocalAppNavigator.current
    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(vertical = 6.dp)) {
        items(shortcuts, key = { it.second }) { (label, icon, url) ->
            SiteTile(stringResource(label), { ShortcutLogo(icon) }) { nav.go(Routes.Browser(url)) }
        }
        item(key = "all") { SiteTile(stringResource(Res.string.wide_sites), { SitesMosaic() }) { nav.go(Routes.Sites) } }
    }
}

@Composable
private fun SiteTile(name: String, logo: @Composable () -> Unit, onClick: () -> Unit) {
    val h = rememberHover()
    val lift by animateDpAsState(if (h.hovered) 10.dp else 1.dp)
    val shape = RoundedCornerShape(18.dp)
    Column(
        Modifier.width(132.dp).height(112.dp).shadow(lift, shape, ambientColor = ViColors.cardShadow, spotColor = ViColors.cardShadow)
            .clip(shape).background(ViColors.tileBg).border(1.dp, ViColors.wideHairline, shape).hover(h).clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        Box(Modifier.size(48.dp).graphicsLayer(scaleX = 1.3f, scaleY = 1.3f), contentAlignment = Alignment.Center) { logo() }
        Text(name, Modifier.padding(top = 12.dp), color = ViColors.ink, fontSize = 13.5.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}
