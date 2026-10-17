package org.app.vimate.music.ui.wide

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.app.vimate.appcore.adaptive.WideButton
import org.app.vimate.appcore.adaptive.WideCard
import org.app.vimate.appcore.adaptive.WideIconButton
import org.app.vimate.appcore.adaptive.WidePage
import org.app.vimate.appcore.adaptive.WideSectionTitle
import org.app.vimate.appcore.adaptive.WideSpec
import org.app.vimate.appcore.adaptive.WideTabs
import org.app.vimate.appcore.adaptive.hover
import org.app.vimate.appcore.adaptive.rememberHover
import org.app.vimate.appcore.component.ErrorBox
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.music.ui.MusicContract
import org.app.vimate.music.ui.component.text
import org.app.vimate.sharedlogic.yt.Collection
import org.app.vimate.sharedlogic.yt.Shelf
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource

/** Tablet / desktop Music tab: header with search, pill tabs, shelves of song cards and album art. */
@Composable
internal fun MusicWide(state: MusicContract.State, onEvent: (MusicContract.Event) -> Unit) {
    val nav = LocalAppNavigator.current
    WidePage(
        stringResource(Res.string.music_title), stringResource(Res.string.wide_music_sub),
        actions = {
            SearchPill { nav.go(Routes.MusicSearch) }
            WideIconButton(Res.drawable.ic_download, stringResource(Res.string.music_downloads)) { nav.home(Tab.FILES) }
        },
    ) {
        WideTabs(state.tabLabels, state.tab, Modifier.padding(start = WideSpec.gutter, bottom = 8.dp)) { onEvent(MusicContract.Event.SelectTab(it)) }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = WideSpec.bottomInset)) {
            if (state.loading) item { Loading() }
            state.error?.let { e -> item { ErrorBox(e.text()) { onEvent(MusicContract.Event.Retry) } } }
            itemsIndexed(state.shelves, key = { i, s -> "$i${s.title}" }) { _, shelf ->
                ShelfWide(shelf, { onEvent(MusicContract.Event.PlayAll(it)) }, { l, i -> onEvent(MusicContract.Event.Tap(l, i)) }, { onEvent(MusicContract.Event.PlaySingle(it)) })
            }
        }
    }
}

@Composable
private fun SearchPill(onClick: () -> Unit) {
    val h = rememberHover()
    val border by animateColorAsState(if (h.hovered) ViColors.ink.copy(alpha = .35f) else ViColors.searchBorder)
    Row(
        Modifier.widthIn(min = 300.dp).height(44.dp).clip(CircleShape).background(ViColors.tileBg).border(1.dp, border, CircleShape)
            .hover(h).clickable(onClick = onClick).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Ic(Res.drawable.ic_search, Modifier.size(20.dp), ViColors.searchIcon)
        Text(stringResource(Res.string.music_search_hint), Modifier.padding(start = 10.dp), color = ViColors.hint, fontSize = 14.5.sp)
    }
}

@Composable
private fun ShelfWide(shelf: Shelf, onPlayAll: (List<Video>) -> Unit, onTap: (List<Video>, Int) -> Unit, onPlaySingle: (Video) -> Unit) {
    val nav = LocalAppNavigator.current
    Column(Modifier.padding(top = 18.dp)) {
        WideSectionTitle(shelf.title, Modifier.padding(horizontal = WideSpec.gutter)) {
            if (shelf.videos.isNotEmpty()) WideButton(stringResource(Res.string.music_play_all), filled = false, icon = Res.drawable.ic_play_fill) { onPlayAll(shelf.videos) }
        }
        if (shelf.videos.isNotEmpty()) LazyRow(contentPadding = PaddingValues(horizontal = WideSpec.gutter), horizontalArrangement = Arrangement.spacedBy(WideSpec.gap)) {
            itemsIndexed(shelf.videos.chunked(4), key = { i, _ -> i }) { _, col ->
                WideCard(Modifier.width(380.dp), padding = 8.dp) {
                    col.forEach { v -> SongRow(v) { onTap(shelf.videos, shelf.videos.indexOf(v)) } }
                }
            }
        }
        if (shelf.collections.isNotEmpty()) LazyRow(contentPadding = PaddingValues(horizontal = WideSpec.gutter), horizontalArrangement = Arrangement.spacedBy(WideSpec.gap)) {
            itemsIndexed(shelf.collections, key = { i, c -> "$i${c.browseId}" }) { _, c ->
                if (c.thumbnail.isEmpty()) GenreChip(c) { nav.go(Routes.MusicCollection(c.browseId, c.title, c.playlistId.ifEmpty { null })) }
                else AlbumCard(c) {
                    if (c.browseId.startsWith("video:")) onPlaySingle(Video(c.browseId.removePrefix("video:"), c.title))
                    else nav.go(Routes.MusicCollection(c.browseId, c.title))
                }
            }
        }
    }
}

/** 64dp song row: art with play overlay on hover, title, artist · plays. */
@Composable
private fun SongRow(v: Video, onPlay: () -> Unit) {
    val h = rememberHover()
    val bg by animateColorAsState(if (h.hovered) ViColors.hover else Color.Transparent)
    Row(
        Modifier.fillMaxWidth().height(64.dp).clip(RoundedCornerShape(12.dp)).background(bg).hover(h).clickable(onClick = onPlay).padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(ViColors.thumbPlaceholder)) {
            AsyncImage(v.thumbnail, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            if (h.hovered) Box(Modifier.fillMaxSize().background(ViColors.shade40), contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_play_fill, Modifier.size(24.dp), Color.White, stringResource(Res.string.music_play))
            }
        }
        Column(Modifier.weight(1f).padding(start = 14.dp)) {
            Text(v.title, color = ViColors.ink, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(listOf(v.channel, v.views).filter { it.isNotEmpty() }.joinToString("  •  "), Modifier.padding(top = 2.dp),
                color = ViColors.sectionLabel, fontSize = 12.5.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

/** 180dp album / playlist card: square art that zooms on hover, title and subtitle. */
@Composable
private fun AlbumCard(c: Collection, onClick: () -> Unit) {
    val h = rememberHover()
    val zoom by animateDpAsState(if (h.hovered) 10.dp else 0.dp)
    val shape = RoundedCornerShape(16.dp)
    Column(Modifier.width(180.dp).hover(h).clickable(interactionSource = h.source, indication = null, onClick = onClick)) {
        Box(Modifier.size(180.dp).shadow(zoom, shape, ambientColor = ViColors.cardShadow, spotColor = ViColors.cardShadow).clip(shape).background(ViColors.thumbPlaceholder)) {
            val scale = 1f + zoom.value / 200f
            AsyncImage(c.thumbnail, null, Modifier.fillMaxSize().graphicsLayer(scaleX = scale, scaleY = scale), contentScale = ContentScale.Crop)
            if (h.hovered) Box(Modifier.align(Alignment.BottomEnd).padding(10.dp).size(40.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_play_fill, Modifier.size(22.dp), ViColors.ink)
            }
        }
        Text(c.title, Modifier.padding(top = 10.dp), color = ViColors.ink, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(c.subtitle, Modifier.padding(top = 2.dp), color = ViColors.sectionLabel, fontSize = 12.5.sp, lineHeight = 16.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

/** Moods & genres button. */
@Composable
private fun GenreChip(c: Collection, onClick: () -> Unit) {
    val h = rememberHover()
    val bg by animateColorAsState(if (h.hovered) ViColors.hover else ViColors.tileBg)
    Box(
        Modifier.width(200.dp).height(56.dp).clip(RoundedCornerShape(14.dp)).background(bg).border(1.dp, ViColors.wideHairline, RoundedCornerShape(14.dp))
            .hover(h).clickable(onClick = onClick).padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart,
    ) { Text(c.title, color = ViColors.ink, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis) }
}
