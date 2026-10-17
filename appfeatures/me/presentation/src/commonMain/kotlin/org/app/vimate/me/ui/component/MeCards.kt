package org.app.vimate.me.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.jetbrains.compose.resources.DrawableResource

/**
 * Card 1 outline: rounded 20dp, with VidMate's step at the top-right (the edge drops ~6dp in an S-curve at x≈208-235dp,
 * leaving a lighter bubble behind the theme switch).
 */
internal val NotchedCard = GenericShape { sz, _ ->
    val u = sz.width / 336f          // 1dp in px (card is 336dp wide)
    val rad = 20f * u; val w = sz.width; val h = sz.height
    moveTo(0f, rad); quadraticTo(0f, 0f, rad, 0f)
    lineTo(228f * u, 0f)
    // Concave sweep down to the right edge, as on VidMate's profile card.
    cubicTo(250f * u, 0f, 248f * u, 50f * u, 278f * u, 56f * u)
    cubicTo(300f * u, 60f * u, 320f * u, 62f * u, w, 70f * u)
    lineTo(w, h - rad); quadraticTo(w, h, w - rad, h)
    lineTo(rad, h); quadraticTo(0f, h, 0f, h - rad); close()
}

/** 78dp column: 32dp red icon 16dp from top, label 6dp under it. */
@Composable
internal fun RowScope.Shortcut(icon: DrawableResource, label: String, size: Float = 32f, dx: Float = 0f, dy: Float = 0f, onClick: () -> Unit) {
    Column(Modifier.weight(1f).fillMaxHeight().clickable(onClick = onClick).padding(start = 4.dp, end = 4.dp, top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) { Ic(icon, Modifier.offset(dx.dp, dy.dp).wrapContentSize(unbounded = true).size(size.dp), ViColors.meRed, label) }
        Text(label, Modifier.padding(top = 6.dp), fontSize = 13.sp, lineHeight = 16.3.sp, color = ViColors.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

/** VidMate's day/night pill: 53x28dp, yellow sun knob on the left in light mode, moon on the right. */
@Composable
internal fun ThemeSwitch(dark: Boolean, onChange: (Boolean) -> Unit) {
    Box(Modifier.size(53.dp, 28.dp).clip(CircleShape).background(ViColors.meSwitchTrack).border(1.dp, ViColors.meSwitchBorder, CircleShape).clickable { onChange(!dark) }) {
        Box(Modifier.align(if (dark) Alignment.CenterEnd else Alignment.CenterStart).padding(horizontal = 2.dp).size(24.dp).clip(CircleShape)
            .background(if (dark) ViColors.meMoonKnob else ViColors.meSunKnob), contentAlignment = Alignment.Center) {
            Ic(if (dark) Res.drawable.ic_moon else Res.drawable.ic_sun, Modifier.size(14.dp), ViColors.white)
        }
        Ic(if (dark) Res.drawable.ic_sun else Res.drawable.ic_moon, Modifier.align(if (dark) Alignment.CenterStart else Alignment.CenterEnd).padding(horizontal = 6.dp).size(14.dp), ViColors.meSwitchIdle)
    }
}

/** One cell of the ME tool grid: 28dp outline icon, label; "Like us" carries a red dot. */
@Composable
internal fun ToolCell(icon: DrawableResource, label: String, iconSize: Float, dot: Boolean, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().fillMaxHeight().clickable(onClick = onClick).padding(top = 2.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box {
            Box(Modifier.size(28.dp), contentAlignment = Alignment.Center) { Ic(icon, Modifier.wrapContentSize(unbounded = true).size(iconSize.dp), ViColors.ink, label) }
            if (dot) Box(Modifier.align(Alignment.TopEnd).offset(x = 5.dp, y = (-4).dp).size(10.dp).clip(CircleShape).background(ViColors.meRed))
        }
        Text(label, Modifier.padding(top = 2.3.dp), fontSize = 13.3.sp, lineHeight = 19.sp, color = ViColors.ink, maxLines = 1)
    }
}

/** POC HomeScreen SiteIcon. */
@Composable
internal fun SiteIcon(name: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Column(Modifier.width(72.dp).clickable(onClick = onClick).padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(36.dp).clip(RoundedCornerShape(9.dp)).background(color), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = ViColors.white, modifier = Modifier.size(22.dp))
        }
        Text(name, fontSize = 11.5.sp, lineHeight = 16.sp, color = ViColors.muted, modifier = Modifier.padding(top = 7.dp), maxLines = 1)
    }
}
