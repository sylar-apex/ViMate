package org.app.vimate.me.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource

/** VidMate sub-page header: 50dp white, back chevron at x=16 (40dp), title at x=64, 1dp rule. */
@Composable
fun PageHeader(title: String, bold: Boolean = true, height: Int = 50, titleStart: Int = 8, titleSize: Float = 0f, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(height.dp).background(ViColors.surface), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.padding(start = 16.dp).size(40.dp).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
            Ic(Res.drawable.ic_back, Modifier.size(30.dp), ViColors.ink, stringResource(Res.string.me_back))
        }
        Text(title, Modifier.padding(start = titleStart.dp), fontSize = if (titleSize > 0) titleSize.sp else if (bold) 19.sp else 15.5.sp, lineHeight = 27.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal, color = ViColors.ink)
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(ViColors.meDivider))
}

@Composable
internal fun HistoryRow(icon: DrawableResource, label: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(55.dp).clickable(onClick = onClick).padding(start = 14.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Ic(icon, Modifier.size(22.dp), ViColors.ink)
        Text(label, Modifier.padding(start = 13.dp).weight(1f), fontSize = 13.2.sp, lineHeight = 19.sp, color = ViColors.ink)
        Ic(Res.drawable.ic_chev_right, Modifier.size(16.dp), ViColors.meGrey)
    }
    Box(Modifier.padding(start = 46.dp, end = 14.dp).fillMaxWidth().height(0.7.dp).background(ViColors.meRowRule))
}

/** Our app glyph ↔ YouTube tile, used on the sign-in pages. */
@Composable
internal fun LinkLogos(size: Int, arrow: DrawableResource) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(size.dp).clip(RoundedCornerShape((size * .2f).dp)).background(Brush.linearGradient(listOf(ViColors.meAppGlyphStart, ViColors.meAppGlyphEnd))),
            contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_app_glyph, Modifier.size((size * .62f).dp), ViColors.white) }
        Ic(arrow, Modifier.padding(horizontal = 18.dp).size((size * .45f).dp), ViColors.meArrow)
        Box(Modifier.size(size.dp).clip(RoundedCornerShape((size * .1f).dp)).background(ViColors.meYoutube), contentAlignment = Alignment.Center) {
            Ic(Res.drawable.ic_site_youtube_square, Modifier.size((size * .62f).dp), ViColors.white)
        }
    }
}

/** Full-width red "Sign in" bar used by the sign-in pages. */
@Composable
internal fun RedSignInBar(modifier: Modifier, corner: Int, bold: Boolean, fontSize: Float, onClick: () -> Unit) {
    Box(modifier.fillMaxWidth().height(44.dp).clip(RoundedCornerShape(corner.dp)).background(ViColors.meButtonRed).clickable(onClick = onClick),
        contentAlignment = Alignment.Center) {
        Text(stringResource(Res.string.me_sign_in), color = ViColors.white, fontWeight = if (bold) FontWeight.Bold else null, fontSize = fontSize.sp)
    }
}
