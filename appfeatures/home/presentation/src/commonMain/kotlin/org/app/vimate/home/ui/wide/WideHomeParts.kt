package org.app.vimate.home.ui.wide

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.SentimentSatisfied
import androidx.compose.material.icons.outlined.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.app.vimate.appcore.adaptive.WideIconButton
import org.app.vimate.appcore.adaptive.hover
import org.app.vimate.appcore.adaptive.rememberHover
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Svg
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.home.ui.component.ShortcutLogo
import org.app.vimate.home.ui.component.chipLabels
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Spacing of the wide Home page. */
internal object WideHomeSpec {
    val gutter = 24.dp
    val gapX = 18.dp
    val gapY = 22.dp
    val cardMin = 270.dp
}

/** Icons for [chipLabels], in order (All, Music, Jukebox, Playlists, 1990s, Movies, Comedy, News, Gaming, Bhojpuri, Punjabi). */
private val chipIcons: List<ImageVector> = listOf(
    Icons.Filled.GridView, Icons.Outlined.MusicNote, Icons.Outlined.Headphones, Icons.AutoMirrored.Filled.List, Icons.Outlined.CalendarToday,
    Icons.Outlined.Movie, Icons.Outlined.SentimentSatisfied, Icons.AutoMirrored.Outlined.Article, Icons.Outlined.SportsEsports,
    Icons.Outlined.LibraryMusic, Icons.Outlined.LibraryMusic,
)

/** Full-width search pill (opens Search) with Downloads and Me on the right. */
@Composable
internal fun WideTopBar(activeDownloads: Int) {
    val nav = LocalAppNavigator.current
    val h = rememberHover()
    val border by animateColorAsState(if (h.hovered) ViColors.ink.copy(alpha = .3f) else ViColors.wideHairline)
    Row(Modifier.fillMaxWidth().padding(horizontal = WideHomeSpec.gutter).padding(top = 14.dp, bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Row(
            Modifier.weight(1f).height(42.dp).clip(CircleShape).background(ViColors.tileBg).border(1.dp, border, CircleShape)
                .hover(h).clickable { nav.go(Routes.Search()) }.padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Ic(Res.drawable.ic_search, Modifier.size(19.dp), ViColors.ink)
            Text(stringResource(Res.string.wide_top_search_hint), Modifier.padding(start = 12.dp), color = ViColors.sectionLabel, fontSize = 13.5.sp, maxLines = 1)
        }
        Box(Modifier.padding(start = 6.dp)) { WideIconButton(Res.drawable.ic_download, stringResource(Res.string.home_downloads), activeDownloads) { nav.home(Tab.FILES) } }
        WideIconButton(Res.drawable.ic_nav_me, stringResource(Res.string.wide_me)) { nav.home(Tab.ME) }
    }
}

/** "All" plus topic chips with icons, Shorts second; the arrow scrolls the row. */
@Composable
internal fun WideChips(selected: Int, onChip: (Int) -> Unit) {
    val nav = LocalAppNavigator.current
    val list = rememberLazyListState()
    val scope = rememberCoroutineScope()
    Row(Modifier.fillMaxWidth().padding(end = WideHomeSpec.gutter), verticalAlignment = Alignment.CenterVertically) {
        LazyRow(
            Modifier.weight(1f), list,
            contentPadding = PaddingValues(start = WideHomeSpec.gutter, end = 12.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(chipLabels, key = { i, _ -> i }) { i, label ->
                Row {
                    Chip(stringResource(label), chipIcons.getOrElse(i) { Icons.Outlined.MusicNote }, i == selected) { onChip(i) }
                    if (i == 0) Box(Modifier.padding(start = 8.dp)) {
                        Chip(stringResource(Res.string.home_shorts), Icons.Filled.Bolt, false, iconTint = VRed) { nav.go(Routes.Shorts()) }
                    }
                }
            }
        }
        val h = rememberHover()
        Box(
            Modifier.size(34.dp).shadow(2.dp, CircleShape, ambientColor = ViColors.cardShadow, spotColor = ViColors.cardShadow).clip(CircleShape)
                .background(if (h.hovered) ViColors.hover else ViColors.tileBg).hover(h).clickable { scope.launch { list.animateScrollBy(400f) } },
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Filled.ChevronRight, stringResource(Res.string.wide_more_chips), Modifier.size(19.dp), tint = ViColors.ink) }
    }
}

@Composable
private fun Chip(text: String, icon: ImageVector, selected: Boolean, iconTint: Color? = null, onClick: () -> Unit) {
    val h = rememberHover()
    val bg by animateColorAsState(if (selected) VRed else if (h.hovered) ViColors.hover else ViColors.tileBg)
    val shape = RoundedCornerShape(11.dp)
    Row(
        Modifier.height(36.dp).shadow(if (selected) 4.dp else 1.dp, shape, ambientColor = ViColors.cardShadow, spotColor = if (selected) VRed else ViColors.cardShadow)
            .clip(shape).background(bg).hover(h).clickable(onClick = onClick).padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, Modifier.size(16.dp), tint = if (selected) Color.White else iconTint ?: ViColors.ink)
        Text(text, Modifier.padding(start = 7.dp), color = if (selected) Color.White else ViColors.ink, fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium, maxLines = 1)
    }
}

/** Section heading with a "See all →" link on the right. */
@Composable
internal fun SeeAllTitle(title: StringResource, last: Boolean = false, onSeeAll: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(top = 20.dp, bottom = if (last) 0.dp else 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(title), Modifier.weight(1f), fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold, color = ViColors.ink)
        val h = rememberHover()
        Row(Modifier.clip(RoundedCornerShape(8.dp)).hover(h).clickable(onClick = onSeeAll).padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.wide_see_all), color = if (h.hovered) VRed else ViColors.sectionLabel, fontSize = 12.5.sp, fontWeight = FontWeight.Medium)
            Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.padding(start = 4.dp).size(14.dp), tint = if (h.hovered) VRed else ViColors.ink)
        }
    }
}

private class QuickSite(val label: StringResource, val url: String?, val logo: @Composable () -> Unit)

private val quickSites = listOf(
    QuickSite(Res.string.wide_site_youtube, "https://m.youtube.com") { ShortcutLogo("site_youtube") },
    QuickSite(Res.string.home_site_instagram, "https://www.instagram.com") { ShortcutLogo("site_instagram") },
    QuickSite(Res.string.home_site_facebook, "https://m.facebook.com") { ShortcutLogo("site_facebook") },
    QuickSite(Res.string.wide_site_tiktok, "https://www.tiktok.com") {
        Box(Modifier.size(36.dp).clip(RoundedCornerShape(9.dp)).background(ViColors.tiktokTile), contentAlignment = Alignment.Center) { Svg("site_tiktok", Modifier.size(26.dp)) }
    },
    QuickSite(Res.string.home_site_google, "https://www.google.com") { Svg("site_google", Modifier.size(34.dp)) },
    QuickSite(Res.string.wide_sites, null) {
        Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(ViColors.chipIdle), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Add, null, Modifier.size(22.dp), tint = ViColors.ink)
        }
    },
)

/** Six equal site tiles (YouTube, Instagram, Facebook, TikTok, Google, All sites); a fixed set, so a plain row. */
@Composable
internal fun QuickSites() {
    val nav = LocalAppNavigator.current
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        quickSites.forEach { s ->
            val h = rememberHover()
            val lift by animateDpAsState(if (h.hovered) 10.dp else 2.dp)
            val shape = RoundedCornerShape(14.dp)
            Column(
                Modifier.weight(1f).height(80.dp).shadow(lift, shape, ambientColor = ViColors.cardShadow, spotColor = ViColors.cardShadow)
                    .clip(shape).background(ViColors.tileBg).hover(h).clickable { s.url?.let { nav.go(Routes.Browser(it)) } ?: nav.go(Routes.Sites) },
                horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
            ) {
                Box(Modifier.size(36.dp).graphicsLayer(scaleX = .85f, scaleY = .85f), contentAlignment = Alignment.Center) { s.logo() }
                Text(stringResource(s.label), Modifier.padding(top = 4.dp), color = ViColors.ink, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            }
        }
    }
}
