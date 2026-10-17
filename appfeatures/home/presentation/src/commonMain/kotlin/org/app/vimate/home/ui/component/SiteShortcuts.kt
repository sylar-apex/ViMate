package org.app.vimate.home.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Svg
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

internal val shortcuts: List<Triple<StringResource, String, String>> = listOf(
    Triple(Res.string.home_site_youtube, "site_youtube", "https://m.youtube.com"),
    Triple(Res.string.home_site_instagram, "site_instagram", "https://www.instagram.com"),
    Triple(Res.string.home_site_facebook, "site_facebook", "https://m.facebook.com"),
    Triple(Res.string.home_site_google, "site_google", "https://www.google.com"),
)

/** Five equal 72dp columns, 87dp tall: 36dp logo at top 16dp, 6dp gap, 12sp label. Fixed 5 cells, so not lazy. */
@Composable
internal fun SiteShortcuts() {
    val nav = LocalAppNavigator.current
    Row(Modifier.fillMaxWidth().height(87.dp)) {
        shortcuts.forEach { (label, icon, url) ->
            LogoShortcut(stringResource(label), Modifier.weight(1f), onClick = { nav.go(Routes.Browser(url)) }) {
                ShortcutLogo(icon)
            }
        }
        LogoShortcut(stringResource(Res.string.home_site_sites), Modifier.weight(1f), onClick = { nav.go(Routes.Sites) }) {
            SitesMosaic()
        }
    }
}

@Composable
internal fun LogoShortcut(name: String, modifier: Modifier, onClick: () -> Unit, logo: @Composable () -> Unit) {
    Column(modifier.fillMaxHeight().clickable(onClick = onClick).padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(36.dp), contentAlignment = Alignment.Center) { logo() }
        Text(name, fontSize = 11.5.sp, lineHeight = 16.sp, color = ViColors.muted, modifier = Modifier.padding(top = 7.dp), maxLines = 1)
    }
}

/** 36dp logo of a [shortcuts] entry, drawn as on VidMate. */
@Composable
internal fun ShortcutLogo(icon: String) {
    when (icon) {
        "site_google" -> Box(Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(ViColors.logoTile), contentAlignment = Alignment.Center) { Svg(icon, Modifier.size(25.7.dp)) }
        "site_youtube" -> Box(Modifier.size(36.dp).clip(RoundedCornerShape(9.dp)).background(ViColors.ytShortcut), contentAlignment = Alignment.Center) {
            Box(Modifier.size(20.dp, 14.dp).clip(RoundedCornerShape(4.dp)).background(Color.White), contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_yt_play, Modifier.size(12.dp), ViColors.ytShortcut)
            }
        }
        "site_facebook" -> Box(Modifier.size(36.dp).clip(RoundedCornerShape(9.dp)).background(ViColors.fbShortcut), contentAlignment = Alignment.BottomCenter) {
            Ic(Res.drawable.ic_fb_f, Modifier.offset(x = 2.dp, y = 2.dp).size(28.dp), Color.White)
        }
        else -> Svg(icon, Modifier.size(36.dp))
    }
}

@Composable
internal fun SitesMosaic() {
    // 2x2 mosaic of site logos on a light tile, like VidMate's "Sites".
    Column(Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(ViColors.logoTile).padding(3.dp), verticalArrangement = Arrangement.SpaceBetween) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Svg("site_facebook", Modifier.size(14.dp)); Svg("site_twitter", Modifier.size(14.dp)) }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Svg("site_pinterest", Modifier.size(14.dp)); Svg("site_youtube", Modifier.size(14.dp)) }
    }
}
