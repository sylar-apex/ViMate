package org.app.vimate.home.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.theme.ViColors

/** VidMate's four-colour "apps" glyph: green, yellow diamond, blue, orange squares. */
@Composable
internal fun AppsGlyph() {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(ViColors.glyphGreen))
            Box(Modifier.size(12.dp).graphicsLayer(rotationZ = 45f, scaleX = .85f, scaleY = .85f).clip(RoundedCornerShape(3.dp)).background(ViColors.glyphYellow))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
            Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(ViColors.glyphBlue))
            Box(Modifier.size(12.dp).clip(RoundedCornerShape(3.dp)).background(ViColors.glyphOrange))
        }
    }
}
