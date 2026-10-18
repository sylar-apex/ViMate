package org.app.vimate.me.ui.wide

import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
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

/** Tools shown on the wide Me page; sign-in, subscriptions, Like us, Junk Clean and Boost are left out. */
private val ToolTints = listOf(Color(0xFF3B82F6), Color(0xFF10B981), Color(0xFFEC4899), Color(0xFF06B6D4), Color(0xFF64748B))
private val WideTools = listOf(MeTool.SHARE, MeTool.UPDATE, MeTool.FEEDBACK, MeTool.HELP, MeTool.SETTINGS)

/** Tablet / desktop Me tab: theme switch, one library + tools card, hot sites, then a hot-videos grid. */
@Composable
internal fun MeWide(
    hot: List<Video>, dark: Boolean, onSetDark: (Boolean) -> Unit, onSignIn: () -> Unit, onFiles: () -> Unit,
    onRoute: (Any) -> Unit, onTool: (MeTool) -> Unit, onDownload: (Video) -> Unit, onSite: (String) -> Unit,
) {
    var dialog by remember { mutableStateOf<MeTool?>(null) }
    dialog?.let { d ->
        if (d == MeTool.FEEDBACK) FeedbackDialog { dialog = null }
        else HelpDialog({ dialog = null }) { dialog = MeTool.FEEDBACK }
    }
    WidePage(stringResource(Res.string.wide_me), actions = { ThemeToggle(dark, onSetDark) }) {
        LazyVerticalGrid(
            GridCells.Adaptive(WideSpec.cardMin),
            contentPadding = PaddingValues(start = WideSpec.gutter, end = WideSpec.gutter, bottom = WideSpec.bottomInset),
            horizontalArrangement = Arrangement.spacedBy(22.dp), verticalArrangement = Arrangement.spacedBy(30.dp),
        ) {
            item(key = "top", span = { GridItemSpan(maxLineSpan) }) {
                Column(verticalArrangement = Arrangement.spacedBy(WideSpec.gap)) {
                    // Compact tiles: tinted icon + label, packed in rows (library, then tools).
                    WideSectionTitle(stringResource(Res.string.wide_section_library))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ToolTile(Res.drawable.ic_me_files, stringResource(Res.string.me_my_files), VRed, onFiles)
                        ToolTile(Res.drawable.ic_me_history, stringResource(Res.string.me_history), Color(0xFFF59E0B)) { onRoute(Routes.History) }
                        ToolTile(Res.drawable.ic_me_playlist, stringResource(Res.string.bm_title), Color(0xFF8B5CF6)) { onRoute(Routes.Playlists) }
                    }
                    WideSectionTitle(stringResource(Res.string.wide_section_tools), Modifier.padding(top = 6.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        WideTools.forEachIndexed { k, t -> ToolTile(t.icon, stringResource(t.label), ToolTints[k % ToolTints.size]) { if (t == MeTool.FEEDBACK || t == MeTool.HELP) dialog = t else onTool(t) } }
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
private fun ThemeToggle(dark: Boolean, onSetDark: (Boolean) -> Unit) {
    Row(Modifier.clip(CircleShape).background(ViColors.chipIdle).padding(3.dp)) {
        listOf(false to Res.string.wide_theme_light, true to Res.string.wide_theme_dark).forEach { (d, label) ->
            val sel = d == dark
            Row(Modifier.clip(CircleShape).background(if (sel) VRed else Color.Transparent).clickable { onSetDark(d) }.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Ic(if (d) Res.drawable.ic_moon else Res.drawable.ic_sun, Modifier.size(14.dp), if (sel) Color.White else ViColors.ink)
                Text(stringResource(label), Modifier.padding(start = 6.dp), color = if (sel) Color.White else ViColors.ink, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/** Tile: tinted rounded icon and label side by side on a card, lifting on hover. */
@Composable
private fun RowScope.ToolTile(icon: DrawableResource, label: String, tint: Color, onClick: () -> Unit) {
    val h = rememberHover()
    val bg by animateColorAsState(if (h.hovered) ViColors.hover else ViColors.tileBg)
    val shape = RoundedCornerShape(14.dp)
    Row(
        Modifier.weight(1f).height(64.dp).clip(shape).background(bg).border(1.dp, ViColors.wideHairline, shape).hover(h).clickable(onClick = onClick).padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(38.dp).clip(RoundedCornerShape(11.dp)).background(tint.copy(alpha = .15f)), contentAlignment = Alignment.Center) {
            Ic(icon, Modifier.size(20.dp), tint, label)
        }
        Text(label, Modifier.padding(start = 12.dp), color = ViColors.ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
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
