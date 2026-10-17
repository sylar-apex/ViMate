package org.app.vimate.appcore.adaptive

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.jetbrains.compose.resources.DrawableResource

/** Spacing shared by every wide (tablet / desktop) page. */
object WideSpec {
    val gutter = 32.dp
    val gap = 20.dp
    val cardRadius = 20.dp
    val cardMin = 300.dp

    /** Space under scrolling content: tablets still float the bottom bar over the page. */
    val bottomInset: Dp
        @Composable get() = if (LocalWindowClass.current == WindowClass.Medium) 110.dp else 40.dp
}

/** Wide page: canvas background, large title with optional subtitle and actions, then [content]. */
@Composable
fun WidePage(
    title: String,
    subtitle: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize().background(ViColors.wideCanvas).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(start = WideSpec.gutter, end = WideSpec.gutter, top = 26.dp, bottom = 18.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold, color = ViColors.ink)
                if (subtitle != null) Text(subtitle, Modifier.padding(top = 4.dp), fontSize = 14.sp, color = ViColors.sectionLabel)
            }
            Row(verticalAlignment = Alignment.CenterVertically, content = actions)
        }
        content()
    }
}

/** White rounded surface with a hairline border. */
@Composable
fun WideCard(modifier: Modifier = Modifier, padding: Dp = 20.dp, content: @Composable ColumnScope.() -> Unit) {
    val shape = RoundedCornerShape(WideSpec.cardRadius)
    Column(modifier.clip(shape).background(ViColors.tileBg).border(1.dp, ViColors.wideHairline, shape).padding(padding), content = content)
}

/** Bold section heading used between wide-page blocks. */
@Composable
fun WideSectionTitle(text: String, modifier: Modifier = Modifier, trailing: @Composable RowScope.() -> Unit = {}) {
    Row(modifier.fillMaxWidth().padding(top = 8.dp, bottom = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text, Modifier.weight(1f), fontSize = 20.sp, lineHeight = 26.sp, fontWeight = FontWeight.Bold, color = ViColors.ink)
        trailing()
    }
}

/** Pill tabs on a soft track; the selected tab is a white chip with shadow-free border. */
@Composable
fun WideTabs(labels: List<String>, selected: Int, modifier: Modifier = Modifier, icons: Map<Int, DrawableResource> = emptyMap(), onSelect: (Int) -> Unit) {
    LazyRow(modifier.clip(RoundedCornerShape(14.dp)).background(ViColors.chipIdle).padding(4.dp)) {
        itemsIndexed(labels, key = { i, _ -> i }) { i, label ->
            val sel = i == selected
            val h = rememberHover()
            val bg by animateColorAsState(if (sel) ViColors.tileBg else if (h.hovered) ViColors.searchBorder else Color.Transparent)
            Row(
                Modifier.height(38.dp).clip(RoundedCornerShape(10.dp)).background(bg).hover(h).clickable { onSelect(i) }.padding(horizontal = 18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                icons[i]?.let { Ic(it, Modifier.padding(end = 6.dp).size(16.dp), if (sel) ViColors.ink else ViColors.sectionLabel) }
                Text(label, color = if (sel) ViColors.ink else ViColors.sectionLabel, fontSize = 14.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Medium)
            }
        }
    }
}

/** Round outline action button with hover, optional red badge. */
@Composable
fun WideIconButton(icon: DrawableResource, label: String, badge: Int = 0, onClick: () -> Unit) {
    val h = rememberHover()
    val bg by animateColorAsState(if (h.hovered) ViColors.hover else ViColors.tileBg)
    Box(Modifier.padding(start = 10.dp)) {
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

/** Filled (red) or outlined text button with hover. */
@Composable
fun WideButton(text: String, filled: Boolean = true, icon: DrawableResource? = null, onClick: () -> Unit) {
    val h = rememberHover()
    val bg by animateColorAsState(
        when {
            filled && h.hovered -> ViColors.brandDark
            filled -> VRed
            h.hovered -> ViColors.hover
            else -> ViColors.tileBg
        },
    )
    val fg = if (filled) Color.White else ViColors.ink
    Row(
        Modifier.height(44.dp).clip(RoundedCornerShape(12.dp)).background(bg)
            .then(if (filled) Modifier else Modifier.border(1.dp, ViColors.searchBorder, RoundedCornerShape(12.dp)))
            .hover(h).clickable(onClick = onClick).padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) Ic(icon, Modifier.padding(end = 8.dp).size(18.dp), fg)
        Text(text, color = fg, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, softWrap = false)
    }
}
