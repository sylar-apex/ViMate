package org.app.vimate.shell

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.adaptive.hover
import org.app.vimate.appcore.adaptive.rememberHover
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val tabLabels = mapOf(
    MainTab.HOME to Res.string.wide_home, MainTab.MUSIC to Res.string.wide_music,
    MainTab.FILES to Res.string.wide_files, MainTab.ME to Res.string.wide_me,
)

/**
 * Permanent left navigation for wide windows (tablet landscape, desktop, web); replaces [BottomBar].
 * Brand at the top, the four tabs, then Explore shortcuts and Settings at the bottom.
 */
@Composable
internal fun SideBar(tab: Int, onTabs: Boolean, activeDownloads: Int, onTab: (Int) -> Unit) {
    val nav = LocalAppNavigator.current
    Row(Modifier.fillMaxHeight()) {
        Column(
            Modifier.width(ShellLayoutSpec.sideBarWidth).fillMaxHeight().background(ViColors.sideBarTint).statusBarsPadding()
                .padding(horizontal = 12.dp),
        ) {
            Brand()
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                MainTab.entries.forEach { item ->
                    val sel = onTabs && tab == item.index
                    NavItem(
                        if (sel) item.active else item.idle, tabLabels.getValue(item), sel,
                        badge = if (item == MainTab.FILES) activeDownloads else 0,
                    ) { onTab(item.index) }
                }
                Section(Res.string.wide_section_explore)
                NavItem(Res.drawable.ic_shorts, Res.string.wide_shorts) { nav.go(Routes.Shorts()) }
                NavItem(Res.drawable.ic_globe, Res.string.wide_sites) { nav.go(Routes.Sites) }
            }
            PremiumCard { nav.go(Routes.SettingsGroup("Download")) }
            Spacer(Modifier.height(14.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(ViColors.wideHairline))
            Spacer(Modifier.height(8.dp))
            NavItem(Res.drawable.ic_settings, Res.string.wide_settings) { nav.go(Routes.Settings) }
            NavItem(Res.drawable.ic_help, Res.string.wide_help) { nav.go(Routes.Help) }
            Spacer(Modifier.height(14.dp))
        }
        Box(Modifier.width(1.dp).fillMaxHeight().background(ViColors.wideHairline))
    }
}

@Composable
private fun Brand() {
    Row(Modifier.padding(start = 6.dp, top = 18.dp, bottom = 20.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(34.dp).clip(RoundedCornerShape(10.dp))
                .background(Brush.linearGradient(listOf(ViColors.brandLight, VRed))),
            contentAlignment = Alignment.Center,
        ) { Ic(Res.drawable.ic_play_fill, Modifier.size(19.dp), Color.White) }
        Column(Modifier.padding(start = 12.dp)) {
            Text(stringResource(Res.string.app_name), fontSize = 17.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold, color = ViColors.ink)
            Text(stringResource(Res.string.wide_tagline), fontSize = 10.5.sp, lineHeight = 13.sp, color = ViColors.sectionLabel)
        }
    }
}

@Composable
private fun Section(label: StringResource) {
    Text(
        stringResource(label).uppercase(), Modifier.padding(start = 12.dp, top = 18.dp, bottom = 6.dp),
        fontSize = 10.sp, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold, color = ViColors.sectionLabel,
    )
}

@Composable
private fun NavItem(icon: DrawableResource, label: StringResource, selected: Boolean = false, badge: Int = 0, onClick: () -> Unit) {
    val h = rememberHover()
    val bg by animateColorAsState(
        when {
            selected -> ViColors.sideBarSelected
            h.hovered -> ViColors.hover
            else -> Color.Transparent
        },
    )
    val tint = if (selected) VRed else ViColors.ink
    val text = stringResource(label)
    Row(
        Modifier.padding(vertical = 1.dp).fillMaxWidth().height(38.dp).clip(RoundedCornerShape(10.dp)).background(bg)
            .hover(h).clickable(onClick = onClick).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Ic(icon, Modifier.size(19.dp), tint, text)
        Text(
            text, Modifier.weight(1f).padding(start = 12.dp), color = tint, fontSize = 13.5.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium, maxLines = 1,
        )
        if (badge > 0) Box(Modifier.size(20.dp).clip(CircleShape).background(VRed), contentAlignment = Alignment.Center) {
            Text("$badge", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Promo card above Settings: fast, HD, ad-free downloads; opens the download settings. */
@Composable
private fun PremiumCard(onClick: () -> Unit) {
    val h = rememberHover()
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Brush.linearGradient(listOf(ViColors.premiumStart, ViColors.premiumEnd)))
            .hover(h).clickable(onClick = onClick).padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.WorkspacePremium, null, Modifier.size(24.dp), tint = ViColors.crown)
        Column(Modifier.weight(1f).padding(start = 10.dp)) {
            Text(stringResource(Res.string.wide_premium_title), color = ViColors.ink, fontSize = 13.sp, lineHeight = 16.sp, fontWeight = FontWeight.Bold)
            Text(stringResource(Res.string.wide_premium_sub), Modifier.padding(top = 2.dp), color = ViColors.sectionLabel, fontSize = 11.sp)
        }
        Box(Modifier.size(26.dp).clip(CircleShape).background(if (h.hovered) VRed else Color.White), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.ChevronRight, null, Modifier.size(18.dp), tint = if (h.hovered) Color.White else ViColors.ink)
        }
    }
}
