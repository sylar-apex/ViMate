package org.app.vimate.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.app.vimate.appcore.component.Thumb
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.music_play
import org.app.vimate.appres.resources.music_play_all
import org.app.vimate.sharedlogic.yt.Collection
import org.app.vimate.sharedlogic.yt.Shelf
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource

/** 41dp tabs, min 72dp, 12dp side padding; 2dp #212121 indicator 6dp wider than the label, over a 1dp #C3C3C3 rule. */
@Composable
internal fun MusicTabs(labels: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    Box(Modifier.fillMaxWidth().height(42.dp)) {
        Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().height(1.dp).background(ViColors.hairline))
        LazyRow(Modifier.fillMaxSize()) {
            itemsIndexed(labels, key = { i, _ -> i }) { i, label ->
                val sel = i == selected
                Box(Modifier.fillMaxHeight().widthIn(min = 72.dp).clickable { onSelect(i) }.padding(horizontal = 6.dp), contentAlignment = Alignment.Center) {
                    Box(Modifier.fillMaxHeight().padding(horizontal = 6.dp), contentAlignment = Alignment.Center) {
                        Text(label, fontSize = if (sel) 13.6.sp else 13.2.sp, lineHeight = 19.sp, fontWeight = if (sel) FontWeight.Medium else FontWeight.Normal, color = if (sel) ViColors.ink else ViColors.tabGrey)
                    }
                    if (sel) Box(Modifier.matchParentSize().padding(start = 5.dp, end = 5.dp, bottom = 1.dp), contentAlignment = Alignment.BottomCenter) { Box(Modifier.fillMaxWidth().height(1.7.dp).background(ViColors.ink)) }
                }
            }
        }
    }
}

@Composable
internal fun ShelfView(shelf: Shelf, onPlayAll: (List<Video>) -> Unit, onTap: (List<Video>, Int) -> Unit, onPlaySingle: (Video) -> Unit) {
    // Section header 40dp: 20sp bold title at x=12, outlined "Play all" 69x30dp at x=275.
    Row(Modifier.fillMaxWidth().height(40.dp).padding(start = 12.dp, end = 16.dp, top = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(shelf.title, fontWeight = FontWeight.Bold, fontSize = 16.15.sp, lineHeight = 24.sp, color = ViColors.ink, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        if (shelf.videos.isNotEmpty()) Box(Modifier.size(69.dp, 30.dp).border(1.dp, ViColors.ink, CircleShape).clip(CircleShape)
            .clickable { onPlayAll(shelf.videos) },
            contentAlignment = Alignment.Center) { Text(stringResource(Res.string.music_play_all), fontSize = 11.5.sp, lineHeight = 16.sp, color = ViColors.ink) }
    }
    if (shelf.videos.isNotEmpty()) {
        // Quick picks: white 312x252dp cards (16dp gap) holding 4 rows of 60dp; next card peeks at x=340.
        LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            itemsIndexed(shelf.videos.chunked(4), key = { i, _ -> i }) { _, col ->
                Column(Modifier.width(312.dp).height(252.dp).clip(RoundedCornerShape(16.dp)).background(ViColors.white).padding(top = 10.dp)) {
                    // Fixed 4-row card (not a scrolling list).
                    col.forEach { v ->
                        QuickPickRow(v) { onTap(shelf.videos, shelf.videos.indexOf(v)) }
                    }
                }
            }
        }
    }
    if (shelf.collections.isNotEmpty()) {
        LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            itemsIndexed(shelf.collections, key = { i, c -> "$i${c.browseId}" }) { i, c -> CollectionCard(c, ViColors.musicCardTints[i % ViColors.musicCardTints.size], onPlaySingle) }
        }
    }
}

/** 60dp row: 48dp art at x=10 (in card), 16sp title / 13sp subtitle at x=70, 24dp #F3F3F3 play circle at x=278. */
@Composable
internal fun QuickPickRow(v: Video, onPlay: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(60.dp).clickable(onClick = onPlay).padding(start = 10.dp, end = 10.dp), verticalAlignment = Alignment.Top) {
        Thumb(v.thumbnail, Modifier.size(48.dp), corner = 4)
        Column(Modifier.padding(start = 12.dp, top = 8.dp).width(144.dp)) {
            Text(v.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 19.sp, color = ViColors.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(listOf(v.channel, v.views).filter { it.isNotEmpty() }.joinToString(" • "), fontSize = 12.5.sp, lineHeight = 16.sp, color = ViColors.muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.weight(1f))
        Box(Modifier.padding(top = 12.dp).size(24.dp).clip(CircleShape).background(ViColors.quickPickPlayBg), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.PlayArrow, stringResource(Res.string.music_play), Modifier.size(16.dp), tint = ViColors.quickPickPlayIcon)
        }
    }
}

/** 140x211dp card: square art on a pastel tile, 16sp title + 2-line 13sp subtitle inset 12dp. */
@Composable
internal fun CollectionCard(c: Collection, tint: Color, onPlaySingle: (Video) -> Unit) {
    val nav = LocalAppNavigator.current
    if (c.thumbnail.isEmpty()) {
        // Moods & genres buttons
        Box(Modifier.width(160.dp).height(48.dp).clip(RoundedCornerShape(10.dp)).background(ViColors.white)
            .clickable { nav.go(Routes.MusicCollection(c.browseId, c.title, c.playlistId.ifEmpty { null })) }.padding(horizontal = 12.dp), contentAlignment = Alignment.CenterStart) {
            Text(c.title, fontWeight = FontWeight.SemiBold, maxLines = 1, color = ViColors.ink)
        }
        return
    }
    Column(Modifier.width(140.dp).height(211.dp).clip(RoundedCornerShape(16.dp)).background(tint).clickable {
        if (c.browseId.startsWith("video:")) onPlaySingle(Video(c.browseId.removePrefix("video:"), c.title))
        else nav.go(Routes.MusicCollection(c.browseId, c.title))
    }) {
        AsyncImage(c.thumbnail, null, Modifier.size(140.dp).clip(RoundedCornerShape(16.dp)), contentScale = ContentScale.Crop)
        Text(c.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 19.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = ViColors.ink,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 10.dp))
        Text(c.subtitle, fontSize = 12.5.sp, lineHeight = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis, color = ViColors.muted, modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 2.dp))
    }
}
