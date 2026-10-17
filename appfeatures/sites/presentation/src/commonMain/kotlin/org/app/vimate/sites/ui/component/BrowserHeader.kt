package org.app.vimate.sites.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource

/** Actions of the browser header and its menu. */
class BrowserHeaderActions(
    val onHome: () -> Unit,
    val onEdit: () -> Unit,
    val onGrab: () -> Unit,
    val onReload: () -> Unit,
    val onBack: () -> Unit,
    val onForward: () -> Unit,
    val onToggleDesktop: () -> Unit,
    val onCopy: () -> Unit,
    val onShare: () -> Unit,
    val onDownloads: () -> Unit,
)

/** Measured from VidMate: compact header: home · address pill (tap to edit) · grab (red, count) · reload · menu. */
@Composable
fun BrowserHeader(
    url: String, host: String, siteName: String, foundCount: Int, desktop: Boolean,
    canGoBack: Boolean, canGoForward: Boolean, menu: Boolean, onMenu: (Boolean) -> Unit, a: BrowserHeaderActions,
) {
    Row(Modifier.fillMaxWidth().height(48.dp).shadow(2.dp).background(ViColors.white).padding(start = 4.dp, end = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(36.dp).clip(CircleShape).clickable(onClick = a.onHome), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_home_line, Modifier.size(22.dp), ViColors.ink, stringResource(Res.string.sites_home)) }
        Row(Modifier.weight(1f).padding(horizontal = 4.dp).height(34.dp).clip(CircleShape).background(ViColors.keypad).clickable(onClick = a.onEdit).padding(start = 12.dp, end = 10.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Ic(if (url.startsWith("https")) Res.drawable.ic_lock else Res.drawable.ic_search, Modifier.size(13.dp), ViColors.metaGrey)
            Column(Modifier.padding(start = 6.dp)) {
                Text(siteName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 15.sp, color = ViColors.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(host, fontSize = 10.sp, lineHeight = 12.sp, color = ViColors.metaGrey, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        // Grabber: every downloadable link on this page (page DOM + sniffed network media).
        Box(Modifier.padding(horizontal = 2.dp).size(32.dp).clip(CircleShape).background(VRed).clickable(onClick = a.onGrab), contentAlignment = Alignment.Center) {
            Ic(Res.drawable.ic_grab, Modifier.size(19.dp), ViColors.white, stringResource(Res.string.sites_grab_files))
            if (foundCount > 0) Box(Modifier.align(Alignment.TopEnd).offset(x = 4.dp, y = (-4).dp).size(15.dp).clip(CircleShape).background(ViColors.white).padding(1.5.dp).clip(CircleShape).background(ViColors.ink), contentAlignment = Alignment.Center) {
                Text("${minOf(foundCount, 99)}", color = ViColors.white, fontSize = 8.sp, lineHeight = 9.sp, fontWeight = FontWeight.Bold)
            }
        }
        Box(Modifier.size(36.dp).clip(CircleShape).clickable(onClick = a.onReload), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_reload, Modifier.size(20.dp), ViColors.ink, stringResource(Res.string.sites_reload)) }
        Box(Modifier.size(32.dp, 36.dp)) {
            Box(Modifier.fillMaxSize().clip(CircleShape).clickable { onMenu(true) }, contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_more_vert, Modifier.size(20.dp), ViColors.ink, stringResource(Res.string.sites_more)) }
            DropdownMenu(menu, { onMenu(false) }, Modifier.width(210.dp), shape = RoundedCornerShape(14.dp), containerColor = ViColors.white, shadowElevation = 8.dp) {
                // Navigation row: back · forward · reload.
                Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                    NavIcon(Res.drawable.ic_m_back, stringResource(Res.string.sites_back), canGoBack) { onMenu(false); a.onBack() }
                    NavIcon(Res.drawable.ic_m_fwd, stringResource(Res.string.sites_forward), canGoForward) { onMenu(false); a.onForward() }
                    NavIcon(Res.drawable.ic_reload, stringResource(Res.string.sites_reload), true) { onMenu(false); a.onReload() }
                }
                Box(Modifier.fillMaxWidth().height(0.7.dp).background(ViColors.gap))
                MenuItem(Res.drawable.ic_m_link, stringResource(Res.string.sites_open_url)) { onMenu(false); a.onEdit() }
                MenuItem(if (desktop) Res.drawable.ic_m_mobile else Res.drawable.ic_m_desktop, stringResource(if (desktop) Res.string.sites_mobile_site else Res.string.sites_desktop_site)) { onMenu(false); a.onToggleDesktop() }
                MenuItem(Res.drawable.ic_m_copy, stringResource(Res.string.sites_copy_link)) { onMenu(false); a.onCopy() }
                MenuItem(Res.drawable.ic_d_share, stringResource(Res.string.sites_share_link)) { onMenu(false); a.onShare() }
                MenuItem(Res.drawable.ic_m_dl, stringResource(Res.string.sites_downloads)) { onMenu(false); a.onDownloads() }
            }
        }
    }
}

@Composable
private fun NavIcon(icon: DrawableResource, label: String, enabled: Boolean, onClick: () -> Unit) {
    Box(Modifier.size(40.dp).clip(CircleShape).clickable(enabled = enabled, onClick = onClick), contentAlignment = Alignment.Center) {
        Ic(icon, Modifier.size(20.dp), if (enabled) ViColors.ink else ViColors.browserIconDisabled, label)
    }
}

@Composable
private fun MenuItem(icon: DrawableResource, label: String, onClick: () -> Unit) =
    Row(Modifier.fillMaxWidth().height(42.dp).clickable(onClick = onClick).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Ic(icon, Modifier.size(18.dp), ViColors.browserMenuIcon); Text(label, Modifier.padding(start = 14.dp), fontSize = 14.sp, color = ViColors.ink)
    }
