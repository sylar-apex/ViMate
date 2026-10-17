package org.app.vimate.sites.ui.grabber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_check_t

@Composable
internal fun TypeChip(label: String, count: Int, on: Boolean, onClick: () -> Unit) {
    Row(Modifier.height(30.dp).clip(CircleShape).background(if (on) VRed else ViColors.keypad)
        .clickable(onClick = onClick).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = if (on) ViColors.white else ViColors.ink, fontSize = 13.sp, fontWeight = if (on) FontWeight.Medium else FontWeight.Normal)
        Text(" $count", color = if (on) ViColors.white.copy(alpha = .8f) else ViColors.grabChipCount, fontSize = 12.sp)
    }
}

@Composable
internal fun Check(on: Boolean) {
    Box(Modifier.size(22.dp).clip(RoundedCornerShape(5.dp)).then(if (on) Modifier.background(VRed) else Modifier.border(1.5.dp, ViColors.grabCheckOff, RoundedCornerShape(5.dp))),
        contentAlignment = Alignment.Center) { if (on) Ic(Res.drawable.ic_check_t, Modifier.size(14.dp), ViColors.white) }
}
