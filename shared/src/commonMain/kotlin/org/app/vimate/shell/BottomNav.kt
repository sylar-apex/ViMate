package org.app.vimate.shell

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.Dp
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.isViDark
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** The floating rounded bar with Home, Music, My Files and Me. */
@Composable
internal fun BottomNav(selected: MainTab, onSelect: (MainTab) -> Unit) {
    val bar = if (isViDark()) MaterialTheme.colorScheme.surface else ViColors.navBar
    var widthPx by remember { mutableIntStateOf(0) }
    val itemWidth: Dp = with(LocalDensity.current) { (widthPx / MainTab.entries.size).toDp() }
    LazyRow(
        Modifier.padding(horizontal = ShellLayoutSpec.navSidePadding).fillMaxWidth().height(ShellLayoutSpec.navHeight)
            .clip(RoundedCornerShape(ShellLayoutSpec.navCorner)).background(bar).onSizeChanged { widthPx = it.width },
        userScrollEnabled = false,
    ) {
        items(MainTab.entries, key = { it.name }) { tab ->
            NavItem(tab, tab == selected, Modifier.size(itemWidth, ShellLayoutSpec.navHeight)) { onSelect(tab) }
        }
    }
}

@Composable
private fun NavItem(tab: MainTab, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val ink = MaterialTheme.colorScheme.onSurface
    val label = stringResource(tab.label)
    Column(modifier.fillMaxHeight().clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(ShellLayoutSpec.navTopGap))
        Box(Modifier.size(ShellLayoutSpec.navIconBox), contentAlignment = Alignment.Center) {
            if (selected) {
                Image(painterResource(tab.active), label, Modifier.size(ShellLayoutSpec.navIcon))
            } else {
                Icon(painterResource(tab.idle), label, tint = ink, modifier = Modifier.size(ShellLayoutSpec.navIcon))
            }
        }
        Text(
            label, color = if (selected) ViColors.red else ink,
            fontSize = ShellLayoutSpec.navLabel, lineHeight = ShellLayoutSpec.navLabelLine, maxLines = 1,
        )
    }
}
