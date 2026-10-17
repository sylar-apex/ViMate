package org.app.vimate.appcore.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.theme.ViColors

/** Sweeping highlight over a placeholder block, in the theme's neutral colours. */
fun Modifier.shimmer(): Modifier = composed {
    val t = rememberInfiniteTransition()
    val x by t.animateFloat(-1f, 2f, infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart))
    val base = ViColors.chipIdle
    val shine = ViColors.hover
    background(Brush.linearGradient(listOf(base, shine, base), start = Offset(x * 800f, 0f), end = Offset(x * 800f + 400f, 200f)))
}

@Composable
private fun Bone(modifier: Modifier) = Box(modifier.clip(RoundedCornerShape(8.dp)).shimmer())

/** Skeleton of a video list (thumbnail + two text lines) shown while data loads, instead of a spinner. */
@Composable
fun ShimmerList(rows: Int = 6, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(rows) {
            Row {
                Bone(Modifier.width(150.dp).aspectRatio(16f / 9))
                Column(Modifier.weight(1f).padding(start = 12.dp, top = 4.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Bone(Modifier.fillMaxWidth(.9f).height(14.dp))
                    Bone(Modifier.fillMaxWidth(.6f).height(14.dp))
                    Bone(Modifier.fillMaxWidth(.35f).height(11.dp))
                }
            }
        }
    }
}

/** One video card placeholder matching WideVideoCard: 16:9 thumbnail, avatar and two title lines + meta. */
@Composable
fun ShimmerVideoCard(modifier: Modifier = Modifier) {
    Column(modifier) {
        Box(Modifier.fillMaxWidth().aspectRatio(16f / 9).clip(RoundedCornerShape(14.dp)).shimmer())
        Row(Modifier.padding(top = 12.dp)) {
            Box(Modifier.width(36.dp).aspectRatio(1f).clip(RoundedCornerShape(50)).shimmer())
            Column(Modifier.weight(1f).padding(start = 10.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Bone(Modifier.fillMaxWidth(.95f).height(13.dp))
                Bone(Modifier.fillMaxWidth(.7f).height(13.dp))
                Bone(Modifier.fillMaxWidth(.45f).height(10.dp))
            }
        }
    }
}

/** A grid of [ShimmerVideoCard] in [columns] columns, for wide feeds while they load. */
@Composable
fun ShimmerCardGrid(columns: Int, rows: Int = 2, gap: androidx.compose.ui.unit.Dp = 18.dp) {
    Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
        repeat(rows) {
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) { repeat(columns) { ShimmerVideoCard(Modifier.weight(1f)) } }
        }
    }
}

/** A plain shimmering block (cover images, text lines). */
@Composable
fun ShimmerBlock(modifier: Modifier, corner: androidx.compose.ui.unit.Dp = 8.dp) = Box(modifier.clip(RoundedCornerShape(corner)).shimmer())
