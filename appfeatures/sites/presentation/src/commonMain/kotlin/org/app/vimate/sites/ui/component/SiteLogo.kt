package org.app.vimate.sites.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Svg
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.sites.ui.constant.SiteTile

@Composable
fun SiteLogo(t: SiteTile, size: Dp) {
    val shape = if (t.circle) CircleShape else RoundedCornerShape(size * .25f)
    Box(Modifier.size(size).clip(shape).background(t.tile), contentAlignment = Alignment.Center) {
        when {
            t.svg != null -> Svg(t.svg, Modifier.size(if (t.tile == ViColors.transparent) size else size * .62f))
            t.tile == ViColors.transparent -> t.glyph?.let { Ic(it, Modifier.size(size), t.glyphTint) }
            else -> t.glyph?.let { Ic(it, Modifier.size(size * .58f), t.glyphTint) }
        }
    }
}
