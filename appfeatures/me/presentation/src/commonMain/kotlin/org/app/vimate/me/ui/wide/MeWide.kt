package org.app.vimate.me.ui.wide

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.adaptive.LocalWindowClass
import org.app.vimate.appcore.adaptive.WideButton
import org.app.vimate.appcore.adaptive.WideCard
import org.app.vimate.appcore.adaptive.WidePage
import org.app.vimate.appcore.adaptive.WideSectionTitle
import org.app.vimate.appcore.adaptive.WideSpec
import org.app.vimate.appcore.adaptive.WideVideoCard
import org.app.vimate.appcore.adaptive.hover
import org.app.vimate.appcore.adaptive.rememberHover
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.me.ui.constant.MeTool
import org.app.vimate.me.ui.constant.hotSites
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource

/** Tablet / desktop Me tab: profile and library cards, tools, hot sites, then a hot-videos grid. */
@Composable
internal fun MeWide(
    hot: List<Video>, dark: Boolean, onSetDark: (Boolean) -> Unit, onSignIn: () -> Unit, onFiles: () -> Unit,
    onRoute: (Any) -> Unit, onTool: (MeTool) -> Unit, onDownload: (Video) -> Unit, onSite: (String) -> Unit,
) {
    WidePage(stringResource(Res.string.wide_me)) {
        LazyVerticalGrid(
            GridCells.Adaptive(WideSpec.cardMin),
            contentPadding = PaddingValues(start = WideSpec.gutter, end = WideSpec.gutter, bottom = WideSpec.bottomInset),
            horizontalArrangement = Arrangement.spacedBy(22.dp), verticalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            item(key = "top", span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(WideSpec.gap)) {
                    if (LocalWindowClass.current.isWide) Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(WideSpec.gap)) {
                        ProfileCard(dark, onSetDark, onSignIn, Modifier.weight(1f).fillMaxHeight())
                        LibraryCard(onFiles, onRoute, Modifier.weight(1.25f).fillMaxHeight(), fill = true)
                    } else {
                        ProfileCard(dark, onSetDark, onSignIn, Modifier.fillMaxWidth())
                        LibraryCard(onFiles, onRoute, Modifier.fillMaxWidth())
                    }
                    WideCard(Modifier.fillMaxWidth()) {
                        WideSectionTitle(stringResource(Res.string.wide_section_tools))
                        Row(Modifier.fillMaxWidth()) {
                            MeTool.entries.forEach { t -> ToolTile(t.icon, stringResource(t.label), t == MeTool.LIKE) { onTool(t) } }
                        }
                    }
                    WideSectionTitle(stringResource(Res.string.me_hot_sites), Modifier.padding(top = 10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(vertical = 4.dp)) {
                        items(hotSites.filter { !it.adult }, key = { it.url }) { site ->
                            SiteChip(site.name, { Icon(site.icon, null, Modifier.size(20.dp), tint = Color.White) }, site.color) { onSite(site.url) }
                        }
                    }
                    if (hot.isNotEmpty()) WideSectionTitle(stringResource(Res.string.common_hot_videos), Modifier.padding(top = 10.dp))
                }
            }
            items(hot, key = { it.id }) { v -> WideVideoCard(v, onDownload = { onDownload(v) }) }
        }
    }
}

@Composable
private fun ProfileCard(dark: Boolean, onSetDark: (Boolean) -> Unit, onSignIn: () -> Unit, modifier: Modifier) {
    val shape = RoundedCornerShape(WideSpec.cardRadius)
    Box(modifier.clip(shape).background(Brush.linearGradient(listOf(ViColors.brandLight, VRed, ViColors.brandDark)))) {
        // Soft decorative circles.
        Box(Modifier.align(Alignment.TopEnd).padding(top = 0.dp).size(180.dp).clip(CircleShape).background(Color.White.copy(alpha = .08f)))
        Box(Modifier.align(Alignment.BottomEnd).padding(end = 90.dp).size(110.dp).clip(CircleShape).background(Color.White.copy(alpha = .06f)))
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(60.dp).clip(CircleShape).background(Color.White.copy(alpha = .22f)).border(2.dp, Color.White.copy(alpha = .6f), CircleShape),
                    contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_person, Modifier.size(32.dp), Color.White) }
                Spacer(Modifier.weight(1f))
                ThemeToggle(dark, onSetDark)
            }
            Text(stringResource(Res.string.wide_me_welcome), Modifier.padding(top = 18.dp), color = Color.White, fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold)
            Text(stringResource(Res.string.wide_me_signin_sub), Modifier.padding(top = 6.dp), color = Color.White.copy(alpha = .85f), fontSize = 14.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(20.dp))
            val h = rememberHover()
            val bg by animateColorAsState(if (h.hovered) Color.White.copy(alpha = .9f) else Color.White)
            Row(Modifier.height(44.dp).clip(RoundedCornerShape(12.dp)).background(bg).hover(h).clickable(onClick = onSignIn).padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Ic(Res.drawable.ic_person, Modifier.size(18.dp), VRed)
                Text(stringResource(Res.string.me_sign_in), Modifier.padding(start = 8.dp), color = VRed, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/** Light / Dark segmented switch on the profile card. */
@Composable
private fun ThemeToggle(dark: Boolean, onSetDark: (Boolean) -> Unit) {
    Row(Modifier.clip(CircleShape).background(Color.Black.copy(alpha = .18f)).padding(3.dp)) {
        listOf(false to Res.string.wide_theme_light, true to Res.string.wide_theme_dark).forEach { (d, label) ->
            val sel = d == dark
            Row(Modifier.clip(CircleShape).background(if (sel) Color.White else Color.Transparent).clickable { onSetDark(d) }.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Ic(if (d) Res.drawable.ic_moon else Res.drawable.ic_sun, Modifier.size(14.dp), if (sel) VRed else Color.White)
                Text(stringResource(label), Modifier.padding(start = 6.dp), color = if (sel) VRed else Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun LibraryCard(onFiles: () -> Unit, onRoute: (Any) -> Unit, modifier: Modifier, fill: Boolean = false) {
    WideCard(modifier) {
        WideSectionTitle(stringResource(Res.string.wide_section_library))
        Row(if (fill) Modifier.fillMaxWidth().weight(1f) else Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LibraryTile(Res.drawable.ic_me_files, stringResource(Res.string.me_my_files), fill, onFiles)
            LibraryTile(Res.drawable.ic_me_history, stringResource(Res.string.me_history), fill) { onRoute(Routes.History) }
            LibraryTile(Res.drawable.ic_me_subs, stringResource(Res.string.me_subscription), fill) { onRoute(Routes.Subscriptions) }
            LibraryTile(Res.drawable.ic_me_playlist, stringResource(Res.string.me_playlist), fill) { onRoute(Routes.Playlists) }
        }
    }
}

@Composable
private fun RowScope.LibraryTile(icon: DrawableResource, label: String, fill: Boolean, onClick: () -> Unit) {
    val h = rememberHover()
    val bg by animateColorAsState(if (h.hovered) ViColors.hover else ViColors.wideCanvas)
    Column(
        Modifier.weight(1f).then(if (fill) Modifier.fillMaxHeight() else Modifier).clip(RoundedCornerShape(16.dp)).background(bg).hover(h).clickable(onClick = onClick).padding(vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
    ) {
        Box(Modifier.size(52.dp).clip(RoundedCornerShape(16.dp)).background(ViColors.tintRed), contentAlignment = Alignment.Center) {
            Ic(icon, Modifier.size(26.dp), ViColors.meRed, label)
        }
        Text(label, Modifier.padding(top = 12.dp), color = ViColors.ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}

@Composable
private fun RowScope.ToolTile(icon: DrawableResource, label: String, dot: Boolean, onClick: () -> Unit) {
    val h = rememberHover()
    val bg by animateColorAsState(if (h.hovered) ViColors.hover else Color.Transparent)
    Column(
        Modifier.weight(1f).clip(RoundedCornerShape(14.dp)).background(bg).hover(h).clickable(onClick = onClick).padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Box(Modifier.size(46.dp).clip(CircleShape).background(ViColors.wideCanvas).border(1.dp, ViColors.wideHairline, CircleShape), contentAlignment = Alignment.Center) {
                Ic(icon, Modifier.size(22.dp), ViColors.ink, label)
            }
            if (dot) Box(Modifier.align(Alignment.TopEnd).size(10.dp).clip(CircleShape).background(ViColors.meRed))
        }
        Text(label, Modifier.padding(top = 10.dp), color = ViColors.ink, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

@Composable
private fun SiteChip(name: String, icon: @Composable () -> Unit, color: Color, onClick: () -> Unit) {
    val h = rememberHover()
    val lift by animateDpAsState(if (h.hovered) 8.dp else 0.dp)
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier.width(170.dp).height(64.dp).shadow(lift, shape, ambientColor = ViColors.cardShadow, spotColor = ViColors.cardShadow)
            .clip(shape).background(ViColors.tileBg).border(1.dp, ViColors.wideHairline, shape).hover(h).clickable(onClick = onClick).padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(color), contentAlignment = Alignment.Center) { icon() }
        Text(name, Modifier.padding(start = 12.dp), color = ViColors.ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
    }
}
