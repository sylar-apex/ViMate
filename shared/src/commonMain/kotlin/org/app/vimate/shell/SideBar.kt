package org.app.vimate.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.clickableNoRipple
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.jetbrains.compose.resources.stringResource

/** Permanent left navigation for wide windows (tablets in landscape, desktop, web); replaces [BottomBar]. */
@Composable
internal fun SideBar(tab: Int, activeDownloads: Int, onTab: (Int) -> Unit) {
    Column(
        Modifier.width(ShellLayoutSpec.sideBarWidth).fillMaxHeight().background(ViColors.navBar).statusBarsPadding().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Spacer(Modifier.height(12.dp))
        MainTab.entries.forEach { item ->
            val sel = tab == item.index
            val tint = if (sel) ViColors.navActive else ViColors.navIdle
            val label = stringResource(item.label)
            Row(
                Modifier.fillMaxWidth().height(48.dp)
                    .background(if (sel) ViColors.sideBarSelected else ViColors.navBar, RoundedCornerShape(12.dp))
                    .clickableNoRipple { onTab(item.index) }.padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Ic(if (sel) item.active else item.idle, Modifier.size(24.dp), tint, label)
                Text(
                    label, Modifier.weight(1f).padding(start = 16.dp), color = tint, fontSize = 14.sp,
                    fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal,
                )
                if (item == MainTab.FILES && activeDownloads > 0) Badge(containerColor = VRed) { Text("$activeDownloads") }
            }
        }
    }
}
