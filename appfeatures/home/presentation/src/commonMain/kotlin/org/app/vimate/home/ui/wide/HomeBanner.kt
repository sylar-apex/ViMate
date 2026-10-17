package org.app.vimate.home.ui.wide

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.adaptive.hover
import org.app.vimate.appcore.adaptive.rememberHover
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Svg
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.jetbrains.compose.resources.stringResource

/**
 * Home hero: "Download videos from Any Site, Anytime", three feature badges and a paste-link box whose
 * Download button opens the link (or searches the text). Floating site icons fill the right side on wide pages.
 */
@Composable
internal fun HomeBanner() {
    val shape = RoundedCornerShape(26.dp)
    BoxWithConstraints(
        Modifier.fillMaxWidth().height(222.dp).clip(shape)
            .background(Brush.linearGradient(listOf(ViColors.bannerStart, ViColors.bannerMid, ViColors.bannerEnd))),
    ) {
        val showArt = maxWidth > 820.dp
        // Soft light blobs.
        Box(Modifier.offset(x = maxWidth * .55f, y = (-60).dp).size(320.dp).clip(CircleShape).background(ViColors.bannerBubble))
        Box(Modifier.offset(x = maxWidth * .78f, y = 120.dp).size(220.dp).clip(CircleShape).background(ViColors.bannerBubble))
        if (showArt) FloatingIcons(Modifier.align(Alignment.CenterEnd).fillMaxHeight().fillMaxWidth(.42f).widthIn(max = 520.dp))
        Column(Modifier.padding(start = 32.dp, top = 24.dp, end = if (showArt) maxWidth * .4f else 40.dp)) {
            Text(stringResource(Res.string.wide_banner_kicker), color = ViColors.ink.copy(alpha = .7f), fontSize = 11.5.sp, letterSpacing = 2.5.sp, fontWeight = FontWeight.SemiBold)
            Text(
                buildAnnotatedString {
                    append(stringResource(Res.string.wide_banner_title_a))
                    withStyle(SpanStyle(brush = Brush.horizontalGradient(listOf(ViColors.bannerOrange, ViColors.bannerPink)))) { append(stringResource(Res.string.wide_banner_title_b)) }
                },
                Modifier.padding(top = 4.dp), color = ViColors.ink, fontSize = 36.sp, lineHeight = 42.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1,
            )
            Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                Feature(Icons.Filled.Bolt, stringResource(Res.string.wide_banner_fast), VRed)
                Feature(Icons.Filled.Diamond, stringResource(Res.string.wide_banner_quality), ViColors.bannerPink)
                Feature(Icons.Filled.AllInclusive, stringResource(Res.string.wide_banner_free), ViColors.bannerOrange)
            }
            PasteBox(Modifier.padding(top = 20.dp).widthIn(max = 620.dp).fillMaxWidth())
        }
    }
}

@Composable
private fun Feature(icon: ImageVector, text: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, Modifier.size(17.dp), tint = tint)
        Text(text, Modifier.padding(start = 8.dp), color = ViColors.ink.copy(alpha = .8f), fontSize = 13.5.sp, fontWeight = FontWeight.Medium)
    }
}

/** Link field + gradient Download button; Enter works too. */
@Composable
private fun PasteBox(modifier: Modifier) {
    val nav = LocalAppNavigator.current
    var text by remember { mutableStateOf("") }
    val submit = {
        val t = text.trim()
        if (t.isNotEmpty()) {
            if (t.startsWith("http://") || t.startsWith("https://")) nav.open(t) else nav.go(Routes.Search(t))
            text = ""
        }
    }
    Row(
        modifier.height(52.dp).shadow(6.dp, CircleShape, ambientColor = ViColors.cardShadow, spotColor = ViColors.cardShadow)
            .clip(CircleShape).background(Color.White).padding(start = 20.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.Link, null, Modifier.size(19.dp), tint = ViColors.ink.copy(alpha = .7f))
        Box(Modifier.weight(1f).padding(start = 14.dp, end = 12.dp)) {
            if (text.isEmpty()) Text(stringResource(Res.string.wide_banner_paste), color = ViColors.sectionLabel, fontSize = 14.sp, maxLines = 1)
            BasicTextField(
                text, { text = it }, Modifier.fillMaxWidth(), singleLine = true,
                textStyle = TextStyle(color = ViColors.ink, fontSize = 14.sp), cursorBrush = SolidColor(VRed),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go), keyboardActions = KeyboardActions(onGo = { submit() }),
            )
        }
        val h = rememberHover()
        Row(
            Modifier.height(40.dp).clip(CircleShape)
                .background(Brush.horizontalGradient(if (h.hovered) listOf(ViColors.btnGradEnd, ViColors.brandDark) else listOf(ViColors.btnGradStart, ViColors.btnGradEnd)))
                .hover(h).clickable { submit() }.padding(horizontal = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Ic(Res.drawable.ic_download, Modifier.size(17.dp), Color.White)
            Text(stringResource(Res.string.wide_banner_download), Modifier.padding(start = 8.dp), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/** Tilted, gently bobbing site icons (YouTube, Instagram, Facebook, TikTok, music). */
@Composable
private fun FloatingIcons(modifier: Modifier) {
    val bob by rememberInfiniteTransition().animateFloat(-5f, 5f, infiniteRepeatable(tween(2600), RepeatMode.Reverse))
    BoxWithConstraints(modifier) {
        val w = maxWidth
        Tile(w * .08f, 44.dp, 46.dp, -12f, bob * .8f, Brush.linearGradient(listOf(ViColors.btnGradStart, VRed))) {
            Icon(Icons.Filled.MusicNote, null, Modifier.size(24.dp), tint = Color.White)
        }
        Tile(w * .26f, 34.dp, 120.dp, -8f, -bob, Brush.linearGradient(listOf(ViColors.brandLight, VRed, ViColors.brandDark))) {
            Ic(Res.drawable.ic_play_fill, Modifier.size(62.dp), Color.White)
        }
        Tile(w * .62f, 12.dp, 76.dp, 10f, bob, null) { Svg("site_instagram", Modifier.fillMaxSize()) }
        Tile(w * .80f, 88.dp, 60.dp, 12f, -bob * .7f, null) { Svg("site_facebook", Modifier.fillMaxSize()) }
        Tile(w * .50f, 120.dp, 78.dp, -10f, bob * .9f, Brush.linearGradient(listOf(ViColors.tiktokTile, ViColors.tiktokTile))) {
            Svg("site_tiktok", Modifier.size(48.dp))
        }
    }
}

@Composable
private fun BoxScope.Tile(x: Dp, y: Dp, size: Dp, angle: Float, dy: Float, bg: Brush?, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(size * .26f)
    Box(
        Modifier.offset(x = x, y = y + dy.dp).size(size).rotate(angle)
            .shadow(size * .18f, shape, ambientColor = ViColors.cardShadow, spotColor = ViColors.cardShadow)
            .clip(shape).then(if (bg != null) Modifier.background(bg) else Modifier),
        contentAlignment = Alignment.Center,
    ) { content() }
}
