package org.app.vimate.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.clickableNoRipple
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.jetbrains.compose.resources.stringResource

/** Measured: 12dp side margins, 51dp tall, #FCFCFC, fully rounded; red #EB2D33 active, #212121 idle, 11sp labels. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BottomBar(tab: Int, activeDownloads: Int, onTab: (Int) -> Unit) {
    Row(
        Modifier.navigationBarsPadding().padding(horizontal = 12.dp).fillMaxWidth().height(52.dp)
            .shadow(6.dp, RoundedCornerShape(26.dp), ambientColor = ViColors.navShadow, spotColor = ViColors.navShadow)
            .background(ViColors.navBar, RoundedCornerShape(26.dp)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MainTab.entries.forEach { item ->
            val i = item.index
            val label = stringResource(item.label)
            val sel = tab == i
            val tint = if (sel) ViColors.navActive else ViColors.navIdle
            Column(Modifier.weight(1f).clickableNoRipple { onTab(i) }, horizontalAlignment = Alignment.CenterHorizontally) {
                BadgedBox(badge = { if (item == MainTab.FILES && activeDownloads > 0) Badge(containerColor = VRed) { Text("$activeDownloads") } }) {
                    Box(Modifier.offset(y = 3.dp).size(24.7.dp), contentAlignment = Alignment.Center) {
                        Ic(
                            if (sel) item.active else item.idle,
                            Modifier.wrapContentSize(unbounded = true).size(if (sel && item == MainTab.HOME) 27.6.dp else 24.7.dp),
                            tint, label,
                        )
                    }
                }
                Text(label, fontSize = 7.5.sp, lineHeight = 10.sp, color = tint, modifier = Modifier.padding(top = 7.3.dp).offset(y = 0.dp))
            }
        }
    }
}
