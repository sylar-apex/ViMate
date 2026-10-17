package org.app.vimate.detail.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.ViColors
import org.jetbrains.compose.resources.DrawableResource

/** One of the five 64dp actions under the title (POC `Action`). */
@Composable
internal fun DetailAction(icon: DrawableResource, label: String, tint: Color = ViColors.ink, onClick: () -> Unit) {
    Column(Modifier.width(64.dp).fillMaxHeight().clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Ic(icon, Modifier.size(28.dp), tint, label)
        Text(label, fontSize = 10.7.sp, lineHeight = 14.7.sp, color = ViColors.ink, maxLines = 1, softWrap = false, modifier = Modifier.padding(top = 7.dp).wrapContentWidth(unbounded = true))
    }
}
