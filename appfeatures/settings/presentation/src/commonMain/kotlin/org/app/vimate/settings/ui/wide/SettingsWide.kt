package org.app.vimate.settings.ui.wide

import org.app.vimate.settings.domain.constant.SettingsGroups as G
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.adaptive.WideCard
import org.app.vimate.appcore.adaptive.WideSpec
import org.app.vimate.appcore.adaptive.hover
import org.app.vimate.appcore.adaptive.rememberHover
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.settings.ui.SettingsGroupBody
import org.app.vimate.settings.ui.groupTitles
import org.app.vimate.settings.ui.settingsEntries
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** Tablet / desktop Settings: group list on the left, the selected group's rows on the right. */
@Composable
fun SettingsWide(initial: String) {
    val nav = LocalAppNavigator.current
    var group by rememberSaveable(initial) { mutableStateOf(initial) }
    // No page header: the side bar already names and leaves the page, so the group list and its panel start at the top.
    Column(Modifier.fillMaxSize().background(ViColors.wideCanvas).statusBarsPadding()) {
        Row(Modifier.fillMaxSize().padding(start = WideSpec.gutter, end = WideSpec.gutter, top = 24.dp, bottom = 24.dp), horizontalArrangement = Arrangement.spacedBy(WideSpec.gap)) {
            WideCard(Modifier.width(300.dp), padding = 10.dp) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    items(settingsEntries, key = { it.group }) { e -> GroupItem(e.icon, e.title, e.sub, group == e.group) { group = e.group } }
                    item(key = G.ABOUT) { GroupItem(Res.drawable.ic_s_info, Res.string.settings_about, null, group == G.ABOUT) { group = G.ABOUT } }
                }
            }
            WideCard(Modifier.weight(1f).widthIn(max = 860.dp).fillMaxSize(), padding = 0.dp) {
                val title = if (group == G.ABOUT) stringResource(Res.string.settings_about) else groupTitles[group]?.let { stringResource(it) } ?: group
                Text(title, Modifier.padding(start = 24.dp, top = 22.dp, bottom = 10.dp), color = ViColors.ink, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Box(Modifier.fillMaxWidth().weight(1f).background(ViColors.surface)) { SettingsGroupBody(group) }
            }
        }
    }
}

@Composable
private fun GroupItem(icon: DrawableResource, title: StringResource, sub: StringResource?, selected: Boolean, onClick: () -> Unit) {
    val h = rememberHover()
    val bg by animateColorAsState(if (selected) ViColors.sideBarSelected else if (h.hovered) ViColors.hover else Color.Transparent)
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(bg).hover(h).clickable(onClick = onClick).padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Ic(icon, Modifier.size(22.dp), if (selected) VRed else ViColors.ink)
        Column(Modifier.padding(start = 14.dp)) {
            Text(stringResource(title), color = if (selected) VRed else ViColors.ink, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold)
            if (sub != null) Text(stringResource(sub), Modifier.padding(top = 2.dp), color = ViColors.sectionLabel, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}
