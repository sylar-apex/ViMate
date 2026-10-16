package org.app.vimate.appcore.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.ViShapes
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_brand_facebook_f
import org.app.vimate.appres.resources.ic_brand_google
import org.app.vimate.appres.resources.ic_brand_instagram
import org.app.vimate.appres.resources.ic_brand_pinterest
import org.app.vimate.appres.resources.ic_brand_twitter
import org.app.vimate.appres.resources.ic_brand_youtube
import org.app.vimate.appres.resources.ic_camera
import org.app.vimate.appres.resources.ic_columns
import org.app.vimate.appres.resources.ic_film
import org.app.vimate.appres.resources.ic_globe
import org.app.vimate.sharedlogic.catalog.BrandSite
import org.app.vimate.sharedlogic.catalog.OpenSite
import org.jetbrains.compose.resources.painterResource

private val TileSize = 36.dp
private val MarkSize = 23.dp

/** The coloured square logo of a brand shortcut. */
@Composable
fun BrandTile(site: BrandSite, modifier: Modifier = Modifier) {
    when (site) {
        BrandSite.YOUTUBE -> Box(modifier.size(TileSize).clip(ViShapes.tile).background(ViColors.youtube), contentAlignment = Alignment.Center) {
            Icon(painterResource(Res.drawable.ic_brand_youtube), null, tint = ViColors.white, modifier = Modifier.size(MarkSize))
        }
        BrandSite.INSTAGRAM -> Box(
            modifier.size(TileSize).clip(ViShapes.tile).background(
                Brush.linearGradient(ViColors.instagram, start = Offset(0f, Float.POSITIVE_INFINITY), end = Offset(Float.POSITIVE_INFINITY, 0f)),
            ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(painterResource(Res.drawable.ic_brand_instagram), null, tint = ViColors.white, modifier = Modifier.size(MarkSize))
        }
        BrandSite.FACEBOOK -> Box(modifier.size(TileSize).clip(ViShapes.tile).background(ViColors.facebook)) {
            Icon(
                painterResource(Res.drawable.ic_brand_facebook_f), null, tint = ViColors.white,
                modifier = Modifier.align(Alignment.BottomCenter).offset(x = 1.5.dp, y = 1.dp).size(29.dp),
            )
        }
        BrandSite.GOOGLE -> Box(modifier.size(TileSize).clip(ViShapes.tile).background(ViColors.google), contentAlignment = Alignment.Center) {
            Image(painterResource(Res.drawable.ic_brand_google), null, Modifier.size(22.dp))
        }
    }
}

/** The four-logo tile that opens the full Sites list. */
@Composable
fun SitesTile(modifier: Modifier = Modifier) {
    val mini = RoundedCornerShape(4.dp)
    Box(modifier.size(TileSize).clip(ViShapes.tile).background(ViColors.sitesTile)) {
        Box(Modifier.offset(3.5.dp, 3.5.dp).size(13.dp).clip(mini).background(ViColors.facebookMini)) {
            Icon(
                painterResource(Res.drawable.ic_brand_facebook_f), null, tint = ViColors.white,
                modifier = Modifier.align(Alignment.BottomCenter).offset(x = 0.5.dp, y = 0.5.dp).size(10.dp),
            )
        }
        Box(Modifier.offset(19.5.dp, 3.5.dp).size(13.dp).clip(mini).background(ViColors.twitter), contentAlignment = Alignment.Center) {
            Icon(painterResource(Res.drawable.ic_brand_twitter), null, tint = ViColors.white, modifier = Modifier.size(8.5.dp))
        }
        Box(Modifier.offset(3.5.dp, 19.5.dp).size(13.dp).background(ViColors.white, CircleShape), contentAlignment = Alignment.Center) {
            Icon(painterResource(Res.drawable.ic_brand_pinterest), null, tint = ViColors.pinterest, modifier = Modifier.size(13.dp))
        }
        Box(Modifier.offset(19.5.dp, 19.5.dp).size(13.dp).clip(mini).background(ViColors.youtubeMini), contentAlignment = Alignment.Center) {
            Icon(painterResource(Res.drawable.ic_brand_youtube), null, tint = ViColors.white, modifier = Modifier.size(9.dp))
        }
    }
}

/** Tile of an open-licence source on the Sites page. */
@Composable
fun OpenSiteTile(site: OpenSite) {
    val (color, icon) = when (site) {
        OpenSite.ARCHIVE -> ViColors.archive to Res.drawable.ic_columns
        OpenSite.COMMONS -> ViColors.commons to Res.drawable.ic_globe
        OpenSite.PEXELS -> ViColors.pexels to Res.drawable.ic_camera
        OpenSite.BLENDER -> ViColors.blender to Res.drawable.ic_film
    }
    Box(Modifier.size(TileSize).clip(RoundedCornerShape(10.dp)).background(color), contentAlignment = Alignment.Center) {
        Icon(painterResource(icon), null, tint = ViColors.white, modifier = Modifier.size(24.dp))
    }
}
