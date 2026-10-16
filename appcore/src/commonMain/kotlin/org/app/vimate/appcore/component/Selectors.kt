package org.app.vimate.appcore.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_check
import org.jetbrains.compose.resources.painterResource

/** Red square check box of the selection lists. */
@Composable
fun CheckBox(checked: Boolean) {
    val shape = RoundedCornerShape(3.dp)
    Box(
        Modifier.size(22.dp).clip(shape)
            .background(if (checked) ViColors.checkRed else ViColors.transparent)
            .then(if (checked) Modifier else Modifier.border(1.5.dp, ViColors.radioOff, shape)),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) Icon(painterResource(Res.drawable.ic_check), null, tint = ViColors.white, modifier = Modifier.size(18.dp))
    }
}

/** Round radio button with a red dot when [on]. */
@Composable
fun RadioDot(on: Boolean, size: Dp = 21.dp, dot: Dp = 12.dp, border: Dp = 1.5.dp, offColor: Color = ViColors.radioOff) {
    Box(Modifier.size(size).border(border, if (on) ViColors.radioRed else offColor, CircleShape), contentAlignment = Alignment.Center) {
        if (on) Box(Modifier.size(dot).background(ViColors.radioRed, CircleShape))
    }
}

/** The short grey bar on top of the styled sheets. */
@Composable
fun SheetHandle() {
    Box(Modifier.padding(top = 6.dp, bottom = 12.dp).size(30.dp, 2.dp).background(ViColors.sheetHandle, RoundedCornerShape(1.dp)))
}
