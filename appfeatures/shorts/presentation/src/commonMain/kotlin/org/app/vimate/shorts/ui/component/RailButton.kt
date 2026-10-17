package org.app.vimate.shorts.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.ViColors
import org.jetbrains.compose.resources.DrawableResource

/** Right-rail cell: 50×68dp, 32dp icon, 10.5sp label. */
@Composable
fun RailButton(icon: DrawableResource, label: String, onClick: () -> Unit) {
    Column(Modifier.size(50.dp, 68.dp).clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Ic(icon, Modifier.size(32.dp), ViColors.white, label)
        Text(label, color = ViColors.white, fontSize = 10.5.sp, lineHeight = 16.sp, maxLines = 1, softWrap = false, modifier = Modifier.padding(top = 4.dp).wrapContentWidth(unbounded = true))
    }
}
