package org.app.vimate.sites.ui.grabber

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.withContext
import org.app.vimate.sites.logic.ioDispatcher
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.grab_sizes
import org.app.vimate.appres.resources.ic_check_t
import org.app.vimate.appres.resources.ic_p_play
import org.app.vimate.sharedlogic.yt.YouTube
import org.app.vimate.sites.logic.fmtSize
import org.app.vimate.sites.model.GrabType
import org.app.vimate.sites.repository.MediaProbe
import org.jetbrains.compose.resources.stringResource

/** Grid tile: square thumbnail, check circle top-right, resolution + size badge bottom, "+n sizes" if deduped. */
@Composable
internal fun GridTile(g: GrabItem, on: Boolean, onToggle: () -> Unit, onPreview: () -> Unit) {
    Box(Modifier.aspectRatio(1f).clip(RoundedCornerShape(6.dp)).background(ViColors.grabTile)
        .then(if (on) Modifier.border(2.5.dp, VRed, RoundedCornerShape(6.dp)) else Modifier).clickable(onClick = onPreview)) {
        val t = g.thumb
        // Fallback icon when there is no thumbnail.
        if (t == null) Column(Modifier.align(Alignment.Center).padding(horizontal = 8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(44.dp).clip(CircleShape).background(tagColor(g.type).copy(alpha = .22f)), contentAlignment = Alignment.Center) {
                Ic(bigIcon(g.type), Modifier.size(24.dp), lerp(tagColor(g.type), ViColors.white, .35f))
            }
            Text(g.name.ifEmpty { g.url.substringAfter("://").substringBefore('/') }, Modifier.padding(top = 6.dp), color = ViColors.white.copy(alpha = .75f),
                fontSize = 9.sp, lineHeight = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        }
        if (t != null) AsyncImage(t, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        if (g.type == GrabType.Image && t == null) AsyncImage(g.url, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        if (g.type != GrabType.Image) {
            val frame = remember(g.url) { mutableStateOf<ByteArray?>(null) }
            if (g.type == GrabType.Video && t == null) LaunchedEffect(g.url) {
                frame.value = withContext(ioDispatcher) { MediaProbe.media(g.url, emptyMap(), frame = true)?.frame }
            }
            if (t == null) frame.value?.let { AsyncImage(it, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
            if (g.type == GrabType.Video && t != null) Ic(Res.drawable.ic_p_play, Modifier.align(Alignment.Center).size(30.dp), ViColors.white)
        }
        Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Brush.verticalGradient(listOf(ViColors.transparent, ViColors.shade80))).padding(horizontal = 5.dp, vertical = 4.dp)) {
            Column {
                if (g.resolution.isNotEmpty()) Text(g.resolution, color = ViColors.white, fontSize = 10.5.sp, fontWeight = FontWeight.Bold, lineHeight = 13.sp)
                val sizes = if (g.variants > 1) stringResource(Res.string.grab_sizes, g.variants - 1) else null
                Text(listOfNotNull(if (g.size > 0) fmtSize(g.size) else null, if (g.duration > 0) YouTube.formatDuration(g.duration / 1000) else null, sizes).joinToString(" · "),
                    color = ViColors.white.copy(alpha = .85f), fontSize = 10.sp, lineHeight = 12.sp, maxLines = 1)
            }
        }
        // Type tag (JPG, MP4, MP3, PDF…) top-left.
        Box(Modifier.align(Alignment.TopStart).padding(6.dp).clip(RoundedCornerShape(4.dp)).background(tagColor(g.type)).padding(horizontal = 5.dp, vertical = 1.dp)) {
            Text((g.ext ?: g.type.label()).uppercase(), color = ViColors.white, fontSize = 9.sp, lineHeight = 11.sp, fontWeight = FontWeight.Bold)
        }
        // Larger touch target for the selection circle.
        Box(Modifier.align(Alignment.TopEnd).size(36.dp).clickable(onClick = onToggle))
        Box(Modifier.align(Alignment.TopEnd).padding(6.dp).size(22.dp).clip(CircleShape)
            .then(if (on) Modifier.background(VRed) else Modifier.background(ViColors.shade33).border(1.5.dp, ViColors.white, CircleShape)), contentAlignment = Alignment.Center) {
            if (on) Ic(Res.drawable.ic_check_t, Modifier.size(14.dp), ViColors.white)
        }
    }
}
