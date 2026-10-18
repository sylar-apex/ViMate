package org.app.vimate.files.ui.component

import androidx.compose.foundation.Image
import org.app.vimate.appcore.frame.rememberVideoFrame
import com.psbapp.utils.PlatformType
import com.psbapp.utils.getPlatform
import androidx.compose.material.icons.filled.Movie
import org.app.vimate.appres.resources.wide_files_sort_size
import org.app.vimate.appres.resources.wide_files_sort_name
import org.app.vimate.appres.resources.wide_files_sort_oldest
import org.app.vimate.appres.resources.wide_files_sort_newest
import org.app.vimate.appres.resources.ic_chevron_down
import org.app.vimate.appres.resources.ic_v_list
import org.app.vimate.appres.resources.ic_v_grid
import androidx.compose.foundation.lazy.grid.items as gridItems
import org.app.vimate.appres.resources.files_search
import org.app.vimate.appres.resources.ic_search
import org.app.vimate.appcore.adaptive.WindowClass
import org.app.vimate.appcore.adaptive.LocalWindowClass
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.app.kmediaplayer.model.MediaFile
import org.app.kmediaplayer.platform.PlatformMedia
import org.app.vimate.appcore.component.ErrorBox
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.component.rememberLoad
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.files_more
import org.app.vimate.appres.resources.files_no_data
import org.app.vimate.appres.resources.files_play_as_music
import org.app.vimate.appres.resources.files_share
import org.app.vimate.appres.resources.ic_more_vert
import org.app.vimate.files.usecase.FilesUseCase
import org.app.vimate.sharedlogic.download.vid.fmtSize
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/** POC LocalTab: Videos / Music tabs, device media in 87dp rows ("size | TYPE | date"); "No data" when empty. */
@Composable
internal fun LocalTab(audio: Boolean, control: LocalTabControl? = null) {
    val files = koinInject<FilesUseCase>()
    val (state, retry) = rememberLoad(audio) { files.localMedia(audio) }
    var query by remember(audio) { mutableStateOf("") }
    val tasks by files.tasks.collectAsState()
    // Downloaded files show the video's own thumbnail (matched by file name); others fall back to a frame / note tile.
    val thumbs = remember(tasks) { tasks.filter { it.thumbnail.isNotEmpty() }.associate { it.fileName.substringBeforeLast('.') to it.thumbnail } }
    var sort by remember(audio) { mutableStateOf(LocalSort.Newest) }
    var grid by remember(audio) { mutableStateOf(true) }
    // On wide windows the page row above owns search, sort and view (control); phones keep this tab's own toolbar.
    val q = control?.query ?: query
    val order = control?.let { LocalSort.entries[it.sort].order } ?: sort.order
    val list = state.data.orEmpty().filter { q.isBlank() || it.title.contains(q.trim(), ignoreCase = true) }.sortedWith(order)
    val wideWindow = LocalWindowClass.current != WindowClass.Compact
    val wide = wideWindow && (control?.grid ?: grid)
    Column(Modifier.fillMaxSize()) {
        if (control == null) Row(Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            FilesSearchField(query, { query = it }, Modifier.weight(1f))
            SortMenu(sort) { sort = it }
            if (wideWindow) Row(Modifier.padding(start = 8.dp).clip(RoundedCornerShape(10.dp)).background(ViColors.chipIdle).padding(3.dp)) {
                ViewChip(Res.drawable.ic_v_grid, grid) { grid = true }
                ViewChip(Res.drawable.ic_v_list, !grid) { grid = false }
            }
        }
        when {
            !state.loading && list.isEmpty() -> Box(Modifier.fillMaxSize().padding(bottom = 60.dp), contentAlignment = Alignment.Center) {
                Text(stringResource(Res.string.files_no_data), fontSize = 15.sp, lineHeight = 19.sp, color = ViColors.fileMeta)
            }
            wide -> LazyVerticalGrid(GridCells.Fixed(4), Modifier.fillMaxSize(), contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                if (state.loading) item(span = { GridItemSpan(maxLineSpan) }) { Loading() }
                state.error?.let { item(span = { GridItemSpan(maxLineSpan) }) { ErrorBox(it, retry) } }
                gridItems(list, key = { it.uri }) { f -> LocalCard(f, list, audio, thumbs[f.title]) }
            }
            else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)) {
                if (state.loading) item(key = "loading") { Loading() }
                state.error?.let { item(key = "error") { ErrorBox(it, retry) } }
                items(list, key = { it.uri }) { f -> LocalRow(f, list, audio, thumbs[f.title]) }
            }
        }
    }
}

/** Grid card for wide windows: 16:9 cover (download thumbnail, else video frame / music tile), title, size · type · date, ⋮. */
@Composable
private fun LocalCard(f: MediaFile, all: List<MediaFile>, audio: Boolean, thumb: String?) {
    val files = koinInject<FilesUseCase>()
    var menu by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { files.playLocal(all, all.indexOf(f), audio) }) {
        Cover(f, audio, thumb, Modifier.fillMaxWidth().aspectRatio(16f / 9).clip(RoundedCornerShape(12.dp)))
        Row(Modifier.padding(top = 8.dp)) {
            Column(Modifier.weight(1f)) {
                Text(f.title, fontSize = 13.5.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold, color = ViColors.ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(listOf(fmtSize(f.sizeBytes), f.extension.uppercase(), PlatformMedia.formatDate(f.dateModifiedMs, "yyyy-MM-dd")).joinToString(" • "),
                    Modifier.padding(top = 3.dp), fontSize = 11.5.sp, color = ViColors.fileMeta, maxLines = 1)
            }
            Box {
                Box(Modifier.size(28.dp).clip(CircleShape).clickable { menu = true }, contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_more_vert, Modifier.size(18.dp), ViColors.fileMore, stringResource(Res.string.files_more)) }
                DropdownMenu(menu, { menu = false }) {
                    if (!audio) DropdownMenuItem(text = { Text(stringResource(Res.string.files_play_as_music)) }, onClick = { menu = false; files.playLocal(listOf(f.copy(isAudio = true)), 0, true) })
                    DropdownMenuItem(text = { Text(stringResource(Res.string.files_share)) }, onClick = { menu = false; files.shareFile(f) })
                }
            }
        }
    }
}

@Composable
private fun Cover(f: MediaFile, audio: Boolean, thumb: String?, modifier: Modifier) {
    Box(modifier.background(if (audio && thumb == null) VRed else ViColors.pocDarkSurfaceVariant), contentAlignment = Alignment.Center) {
        when {
            thumb != null -> AsyncImage(thumb, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            audio -> Icon(Icons.Filled.MusicNote, null, Modifier.size(32.dp), tint = ViColors.white)
            // Only platforms with a video-frame decoder (Android) can preview a local file; elsewhere the image loader
            // would read the whole (multi-GB) video into memory, so show a plain video tile instead.
            // Desktop: a frame grabbed with libVLC; the clapper icon shows until it is ready.
            !CanPreviewVideo -> rememberVideoFrame(f.uri, 0)?.let { Image(it, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop) }
                ?: Icon(Icons.Filled.Movie, null, Modifier.size(30.dp), tint = ViColors.white.copy(alpha = .7f))
            else -> AsyncImage(f.uri, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
        if (audio && thumb != null) Box(Modifier.align(Alignment.BottomStart).padding(6.dp).size(22.dp).clip(CircleShape).background(VRed), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.MusicNote, null, Modifier.size(13.dp), tint = ViColors.white)
        }
    }
}

/** Compact search box above the file lists; filters by title as you type. */
@Composable
internal fun FilesSearchField(value: String, onChange: (String) -> Unit, modifier: Modifier = Modifier.fillMaxWidth()) {
    val shape = RoundedCornerShape(10.dp)
    BasicTextField(value, onChange, modifier.height(38.dp), singleLine = true,
        textStyle = TextStyle(fontSize = 13.sp, lineHeight = 17.sp, color = ViColors.ink), cursorBrush = SolidColor(VRed),
        decorationBox = { inner ->
            Row(Modifier.fillMaxSize().clip(shape).background(ViColors.chipIdle).padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Ic(Res.drawable.ic_search, Modifier.size(15.dp), ViColors.fileMeta)
                Box(Modifier.padding(start = 8.dp).weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) Text(stringResource(Res.string.files_search), fontSize = 13.sp, lineHeight = 17.sp, color = ViColors.fileMeta)
                    inner()
                }
            }
        })
}

@Composable
private fun LocalRow(f: MediaFile, all: List<MediaFile>, audio: Boolean, thumb: String?) {
    val files = koinInject<FilesUseCase>()
    var menu by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().height(87.dp).clickable { files.playLocal(all, all.indexOf(f), audio) }.padding(start = 12.dp, top = 8.dp)) {
        Cover(f, audio, thumb, Modifier.size(124.dp, 70.dp).clip(RoundedCornerShape(6.dp)))
        Column(Modifier.weight(1f).padding(start = 10.dp, top = 7.dp)) {
            Text(f.title, fontSize = 15.sp, lineHeight = 20.3.sp, color = ViColors.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                RowMeta(fmtSize(f.sizeBytes))
                MetaBar(); RowMeta(f.extension.uppercase())
                MetaBar(); RowMeta(PlatformMedia.formatDate(f.dateModifiedMs, "yyyy-MM-dd"), Modifier.weight(1f, fill = false))
            }
        }
        Box(Modifier.padding(top = 12.dp).size(35.dp, 45.dp)) {
            Box(Modifier.fillMaxSize().clickable { menu = true }, contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_more_vert, Modifier.size(24.dp), ViColors.fileMore, stringResource(Res.string.files_more)) }
            DropdownMenu(menu, { menu = false }) {
                if (!audio) DropdownMenuItem(text = { Text(stringResource(Res.string.files_play_as_music)) }, onClick = { menu = false; files.playLocal(listOf(f.copy(isAudio = true)), 0, true) })
                DropdownMenuItem(text = { Text(stringResource(Res.string.files_share)) }, onClick = { menu = false; files.shareFile(f) })
            }
        }
        Spacer(Modifier.width(9.dp))
    }
}

@Composable
private fun RowMeta(text: String, modifier: Modifier = Modifier) =
    Text(text, fontSize = 12.5.sp, lineHeight = 16.sp, color = ViColors.fileMeta, maxLines = 1, overflow = TextOverflow.Ellipsis, softWrap = false, modifier = modifier)

/** Sort options for the Videos / Music tabs. */
private enum class LocalSort(val label: org.jetbrains.compose.resources.StringResource, val order: Comparator<MediaFile>) {
    Newest(Res.string.wide_files_sort_newest, compareByDescending { it.dateModifiedMs }),
    Oldest(Res.string.wide_files_sort_oldest, compareBy { it.dateModifiedMs }),
    Name(Res.string.wide_files_sort_name, compareBy { it.title.lowercase() }),
    Size(Res.string.wide_files_sort_size, compareByDescending { it.sizeBytes }),
}

@Composable
private fun SortMenu(sort: LocalSort, onSort: (LocalSort) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box(Modifier.padding(start = 8.dp)) {
        Row(Modifier.height(38.dp).clip(RoundedCornerShape(10.dp)).background(ViColors.chipIdle).clickable { open = true }.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(sort.label), fontSize = 12.5.sp, color = ViColors.ink, maxLines = 1)
            Ic(Res.drawable.ic_chevron_down, Modifier.padding(start = 6.dp).size(12.dp), ViColors.ink)
        }
        DropdownMenu(open, { open = false }) {
            LocalSort.entries.forEach { o -> DropdownMenuItem(text = { Text(stringResource(o.label)) }, onClick = { onSort(o); open = false }) }
        }
    }
}

@Composable
private fun ViewChip(icon: org.jetbrains.compose.resources.DrawableResource, on: Boolean, onClick: () -> Unit) {
    Box(Modifier.size(32.dp).clip(RoundedCornerShape(7.dp)).background(if (on) ViColors.detailDownloadPill else androidx.compose.ui.graphics.Color.Transparent).clickable(onClick = onClick),
        contentAlignment = Alignment.Center) { Ic(icon, Modifier.size(15.dp), if (on) VRed else ViColors.ink) }
}

private val CanPreviewVideo = getPlatform().type == PlatformType.Android

/** Search, sort (index into Newest / Oldest / Name / Size) and grid view, set by the wide My Files toolbar. */
internal data class LocalTabControl(val query: String, val sort: Int, val grid: Boolean)
