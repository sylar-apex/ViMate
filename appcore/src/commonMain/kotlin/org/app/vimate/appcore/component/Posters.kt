package org.app.vimate.appcore.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import org.app.vimate.appcore.theme.ViAlpha
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.ViShapes
import org.app.vimate.sharedlogic.logic.Formats
import org.app.vimate.sharedlogic.model.Media

private const val AVATAR_TEXT_SCALE = 0.44f

/** The gradient pair for [id], stable across launches. */
fun posterColors(id: String): List<Color> = ViColors.posterPalettes[abs(id.hashCode()) % ViColors.posterPalettes.size]

/** A gradient card with the title printed on it, used where no video frame is shown. */
@Composable
fun Poster(media: Media, modifier: Modifier = Modifier, showDuration: Boolean = true) {
    Box(modifier.clip(ViShapes.poster).background(Brush.linearGradient(posterColors(media.id)))) {
        Text(
            media.title,
            modifier = Modifier.align(Alignment.Center).padding(12.dp),
            color = ViColors.white,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        val duration = Formats.duration(media.durationMs)
        if (showDuration && duration.isNotEmpty()) {
            Text(
                duration,
                modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                    .background(ViColors.black.copy(alpha = ViAlpha.DURATION_BADGE), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                color = ViColors.white,
                style = MaterialTheme.typography.labelMedium,
            )
        }
    }
}

/** A round gradient avatar with the first letter of [name]. */
@Composable
fun Avatar(name: String, size: Dp) {
    Box(Modifier.size(size).clip(CircleShape).background(Brush.linearGradient(posterColors(name))), contentAlignment = Alignment.Center) {
        Text(name.take(1).uppercase(), color = ViColors.white, fontSize = (size.value * AVATAR_TEXT_SCALE).sp, fontWeight = FontWeight.Medium)
    }
}

/** The duration in a dark pill, drawn over a thumbnail. Nothing for unknown durations. */
@Composable
fun DurationBadge(media: Media, modifier: Modifier = Modifier, fontSize: Float = 12f) {
    val duration = Formats.duration(media.durationMs)
    if (duration.isEmpty()) return
    Text(
        duration,
        color = ViColors.white,
        fontSize = fontSize.sp,
        modifier = modifier.background(ViColors.black.copy(alpha = ViAlpha.DURATION_BADGE), ViShapes.thumb).padding(horizontal = 4.dp),
    )
}
