package org.app.vimate.appcore.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.frame.rememberVideoFrame
import org.app.vimate.appcore.theme.ViAlpha
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_nav_music
import org.app.vimate.appres.resources.ic_play
import org.app.vimate.sharedlogic.logic.Renditions
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.model.Media
import org.jetbrains.compose.resources.painterResource

private const val FILE_FRAME_MS = 1000L

/** A real frame of a catalog title over its gradient; the gradient alone while loading or for music. */
@Composable
fun MediaThumb(media: Media, modifier: Modifier = Modifier) {
    val source = Renditions.sorted(media).firstOrNull()
    val frame = if (source != null && !media.isMusic) rememberVideoFrame(source.url, media.thumbAtMs) else null
    Box(modifier.background(Brush.linearGradient(posterColors(media.id)))) {
        if (frame != null) Image(frame, media.title, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
    }
}

/** A frame of a local or downloaded file, or a play/music glyph on dark grey. */
@Composable
fun FileThumb(uri: String, kind: FileKind, modifier: Modifier) {
    val frame = if (kind != FileKind.MUSIC) rememberVideoFrame(uri, FILE_FRAME_MS) else null
    Box(modifier.background(ViColors.fileThumb), contentAlignment = Alignment.Center) {
        if (frame != null) {
            Image(frame, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        } else {
            Icon(
                painterResource(if (kind == FileKind.MUSIC) Res.drawable.ic_nav_music else Res.drawable.ic_play), null,
                tint = ViColors.white.copy(alpha = ViAlpha.FILE_ICON), modifier = Modifier.size(24.dp),
            )
        }
    }
}
