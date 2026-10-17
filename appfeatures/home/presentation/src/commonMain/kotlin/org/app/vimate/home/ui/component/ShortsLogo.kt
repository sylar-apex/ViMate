package org.app.vimate.home.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import org.app.vimate.appcore.theme.ViColors

/** Red play-bolt mark used on the Shorts chip. */
@Composable
fun ShortsLogo(modifier: Modifier) {
    Canvas(modifier) {
        val w = size.width; val h = size.height
        val body = Path().apply { moveTo(w * .62f, 0f); lineTo(w, h * .2f); lineTo(w * .38f, h); lineTo(0f, h * .8f); close() }
        drawPath(body, ViColors.shortsChipRed)
        val play = Path().apply { moveTo(w * .4f, h * .36f); lineTo(w * .68f, h * .5f); lineTo(w * .4f, h * .64f); close() }
        drawPath(play, Color.White)
    }
}
