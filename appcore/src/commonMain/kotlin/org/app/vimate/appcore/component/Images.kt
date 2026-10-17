package org.app.vimate.appcore.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

/** POC Thumb: rounded image with optional duration pill (bottom end) and bold badge (bottom start). */
@Composable
fun Thumb(url: String, modifier: Modifier = Modifier, corner: Int = 12, duration: String = "", badge: String = "") {
    Box(modifier.clip(RoundedCornerShape(corner.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
        AsyncImage(url, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        if (duration.isNotEmpty()) Text(
            duration, color = Color.White, fontSize = 12.sp,
            modifier = Modifier.align(Alignment.BottomEnd).padding(6.dp).background(ViColors.shade67, RoundedCornerShape(4.dp))
                .padding(horizontal = 4.dp, vertical = 1.dp),
        )
        if (badge.isNotEmpty()) Text(
            badge, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.BottomStart).padding(6.dp),
        )
    }
}

/** POC Avatar: circular image, grey when [url] is empty. */
@Composable
fun Avatar(url: String, size: Int = 40) {
    Box(Modifier.size(size.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant)) {
        if (url.isNotEmpty()) AsyncImage(url, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
    }
}

/** IconBuddy SVG shipped in composeResources/files/icons (multicolour brand logos), e.g. `Svg("site_youtube", ...)`. */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun Svg(name: String, modifier: Modifier) {
    val bytes by produceState<ByteArray?>(null, name) { value = runCatching { Res.readBytes("files/icons/$name.svg") }.getOrNull() }
    bytes?.let { AsyncImage(it, null, modifier) } ?: Box(modifier)
}

/** Vector drawable icon (POC `Ic(R.drawable.x, ...)` → `Ic(Res.drawable.x, ...)`). */
@Composable
fun Ic(id: DrawableResource, modifier: Modifier, tint: Color = Color.Unspecified, desc: String? = null) =
    Icon(painterResource(id), desc, modifier, tint = tint)
