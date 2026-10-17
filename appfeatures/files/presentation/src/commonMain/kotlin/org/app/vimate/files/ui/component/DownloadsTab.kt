package org.app.vimate.files.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.app.kmediaplayer.platform.PlatformMedia
import org.app.vimate.appcore.component.Empty
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.component.Thumb
import org.app.vimate.appcore.component.VMenu
import org.app.vimate.appcore.component.hotVideos
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.files.usecase.FilesUseCase
import org.app.vimate.sharedlogic.download.option.Kind
import org.app.vimate.sharedlogic.download.vid.TaskState
import org.app.vimate.sharedlogic.download.vid.VidTask
import org.app.vimate.sharedlogic.download.vid.fmtSize
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/** POC DownloadsTab: select bar, "Downloading N" active rows, "Downloaded" header + kind filter, done rows, hot videos. */
@Composable
internal fun DownloadsTab(selecting: Boolean, setSelecting: (Boolean) -> Unit) {
    val files = koinInject<FilesUseCase>()
    val all by files.tasks.collectAsState()
    var kindFilter by remember { mutableStateOf<Kind?>(null) }
    var filterMenu by remember { mutableStateOf(false) }
    val selected = remember { mutableStateListOf<Long>() }
    val visible = all.filter { !it.hidden }
    val active = visible.filter { it.state != TaskState.Done }
    val done = visible.filter { it.state == TaskState.Done && (kindFilter == null || it.kind == kindFilter) }
    val hot by produceState<List<Video>>(emptyList()) { value = files.hotVideos() }
    val nav = LocalAppNavigator.current

    LazyColumn(Modifier.fillMaxSize()) {
        if (selecting) item(key = "select") {
            Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = { setSelecting(false); selected.clear() }) { Text(stringResource(Res.string.files_cancel)) }
                Text(stringResource(Res.string.files_selected, selected.size), Modifier.weight(1f))
                TextButton(onClick = { selected.clear(); selected += visible.map { it.id } }) { Text(stringResource(Res.string.files_all)) }
                TextButton(onClick = { selected.forEach { files.downloads.delete(it) }; selected.clear(); setSelecting(false) }, enabled = selected.isNotEmpty()) {
                    Text(stringResource(Res.string.files_delete), color = VRed)
                }
            }
        }
        if (active.isNotEmpty()) {
            item(key = "activeHeader") { Text(stringResource(Res.string.files_downloading, active.size), fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(16.dp)) }
            items(active, key = { "a${it.id}" }) { t -> ActiveRow(t, selecting, t.id in selected) { if (it) selected += t.id else selected -= t.id } }
        }
        item(key = "doneHeader") {
            // 50dp header: bold title at x=12, #EDEDED 19dp count circle, "All Files ▾" right.
            Row(Modifier.fillMaxWidth().height(50.dp).padding(start = 12.dp, end = 12.dp, top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.files_downloaded), fontWeight = FontWeight.Bold, fontSize = 16.6.sp, lineHeight = 22.sp, color = ViColors.ink)
                Box(Modifier.padding(start = 6.dp).size(19.dp).clip(CircleShape).background(ViColors.gap), contentAlignment = Alignment.Center) {
                    Text("${done.size}", fontSize = 12.sp, lineHeight = 14.sp, color = ViColors.ink)
                }
                Spacer(Modifier.weight(1f))
                Box {
                    Row(Modifier.clickable { filterMenu = true }, verticalAlignment = Alignment.CenterVertically) {
                        Text(kindFilter?.let { k -> stringResource(Res.string.files_kind_filter, k.name) } ?: stringResource(Res.string.files_all_files), fontSize = 12.5.sp, lineHeight = 19.sp, color = ViColors.ink)
                        Ic(Res.drawable.ic_chevron_down, Modifier.size(24.dp), ViColors.ink)
                    }
                    DropdownMenu(filterMenu, { filterMenu = false }) {
                        val doneAll = visible.filter { it.state == TaskState.Done }
                        DropdownMenuItem(text = { Text(stringResource(Res.string.files_all_files_n, doneAll.size)) }, onClick = { kindFilter = null; filterMenu = false })
                        KindItem(Kind.Video, stringResource(Res.string.files_kind_videos), doneAll) { kindFilter = it; filterMenu = false }
                        KindItem(Kind.Music, stringResource(Res.string.files_kind_music), doneAll) { kindFilter = it; filterMenu = false }
                        KindItem(Kind.Picture, stringResource(Res.string.files_kind_picture), doneAll) { kindFilter = it; filterMenu = false }
                        KindItem(Kind.App, stringResource(Res.string.files_kind_app), doneAll) { kindFilter = it; filterMenu = false }
                    }
                }
            }
        }
        if (done.isEmpty()) item(key = "empty") { Empty(stringResource(Res.string.files_no_downloads)) }
        items(done, key = { "d${it.id}" }) { t -> DoneRow(t, done, selecting, t.id in selected) { if (it) selected += t.id else selected -= t.id } }
        hotVideos(hot, onDownload = { nav.downloadFor = it })
        item(key = "end") { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun KindItem(k: Kind, label: String, doneAll: List<VidTask>, onPick: (Kind) -> Unit) =
    DropdownMenuItem(text = { Text(stringResource(Res.string.files_kind_n, label, doneAll.count { it.kind == k })) }, onClick = { onPick(k) })

@Composable
internal fun ActiveRow(t: VidTask, selecting: Boolean, checked: Boolean, onCheck: (Boolean) -> Unit) {
    val dl = koinInject<FilesUseCase>().downloads
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        if (selecting) Checkbox(checked, onCheck)
        Thumb(t.thumbnail, Modifier.width(120.dp).aspectRatio(16f / 9), duration = t.duration)
        Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
            Text(t.title, maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 14.sp)
            LinearProgressIndicator(progress = { t.progress }, Modifier.fillMaxWidth().padding(vertical = 6.dp), color = VRed)
            val status = when (t.state) {
                TaskState.Running -> stringResource(Res.string.files_running, fmtSize(t.doneBytes), if (t.totalBytes > 0) fmtSize(t.totalBytes) else "?", fmtSize(dl.speedOf(t.id)))
                TaskState.Queued -> stringResource(Res.string.files_waiting)
                TaskState.Paused -> stringResource(Res.string.files_paused, fmtSize(t.doneBytes))
                TaskState.Merging -> stringResource(Res.string.files_merging)
                TaskState.Failed -> stringResource(Res.string.files_failed, t.error.toString())
                TaskState.Done -> ""
            }
            Text(status, fontSize = 12.sp, color = if (t.state == TaskState.Failed) VRed else ViColors.metaGrey, maxLines = 1)
        }
        when (t.state) {
            TaskState.Running, TaskState.Queued -> IconButton(onClick = { dl.pause(t.id) }) { Icon(Icons.Filled.Pause, stringResource(Res.string.files_pause)) }
            TaskState.Paused, TaskState.Failed -> IconButton(onClick = { dl.resume(t.id) }) {
                Icon(if (t.state == TaskState.Failed) Icons.Filled.Refresh else Icons.Filled.PlayArrow, stringResource(Res.string.files_resume))
            }
            else -> {}
        }
        IconButton(onClick = { dl.delete(t.id) }) { Icon(Icons.Outlined.Close, stringResource(Res.string.files_cancel)) }
    }
}

/** POC DoneRow: 88dp row, 124x70dp thumb with CC/quality/duration overlays, 2-line title, meta line, ⋮ menu. */
@Composable
internal fun DoneRow(t: VidTask, list: List<VidTask>, selecting: Boolean = false, checked: Boolean = false, onCheck: (Boolean) -> Unit = {}) {
    val files = koinInject<FilesUseCase>()
    val dl = files.downloads
    val nav = LocalAppNavigator.current
    val toast = LocalToast.current
    var menu by remember { mutableStateOf(false) }
    var rename by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    val unhideToShare = stringResource(Res.string.files_unhide_to_share)
    val movedOut = stringResource(Res.string.files_moved_out)
    val movedIn = stringResource(Res.string.files_moved_in)
    val converting = stringResource(Res.string.files_converting)
    val converted = stringResource(Res.string.files_converted)
    val convertFailed = stringResource(Res.string.files_convert_failed)
    Row(Modifier.fillMaxWidth().height(88.dp).clickable { if (selecting) onCheck(!checked) else files.playTask(t, list) }.padding(start = 12.dp, top = 9.dp)) {
        if (selecting) Checkbox(checked, onCheck)
        Box(Modifier.size(124.dp, 70.dp).clip(RoundedCornerShape(6.dp)).background(if (t.kind == Kind.Music) VRed else ViColors.pocDarkSurfaceVariant)) {
            if (t.kind == Kind.Music && t.thumbnail.isEmpty()) Icon(Icons.Filled.MusicNote, null, Modifier.align(Alignment.Center).size(32.dp), tint = ViColors.white)
            else AsyncImage(t.thumbnail, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(30.dp).background(Brush.verticalGradient(listOf(ViColors.transparent, ViColors.shade60))))
            if (t.subtitleUrl != null) Box(Modifier.align(Alignment.TopEnd).size(24.dp, 18.dp).background(ViColors.ccBadge, RoundedCornerShape(bottomStart = 9.dp, topEnd = 6.dp)), contentAlignment = Alignment.Center) {
                Text(stringResource(Res.string.files_cc), color = ViColors.white, fontSize = 11.sp, lineHeight = 12.sp, fontWeight = FontWeight.Bold)
            }
            if (t.kind == Kind.Video && t.quality.isNotEmpty()) Text(t.quality.uppercase(), color = ViColors.white, fontSize = 12.sp, lineHeight = 14.sp,
                modifier = Modifier.align(Alignment.BottomStart).padding(start = 4.dp, bottom = 2.dp))
            if (t.duration.isNotEmpty()) Text(t.duration, color = ViColors.white, fontSize = 12.sp, lineHeight = 14.sp,
                modifier = Modifier.align(Alignment.BottomEnd).padding(end = 4.dp, bottom = 2.dp))
        }
        Box(Modifier.weight(1f).fillMaxHeight()) {
            Text(t.fileName.substringBeforeLast('.'), maxLines = 2, overflow = TextOverflow.Ellipsis, fontSize = 15.sp, lineHeight = 17.7.sp, color = ViColors.ink,
                modifier = Modifier.padding(start = 10.dp, top = 6.dp, end = 44.dp))
            Row(Modifier.padding(start = 10.dp, top = 52.dp).offset(y = (-0.3).dp), verticalAlignment = Alignment.CenterVertically) {
                Ic(if (t.kind == Kind.Music) Res.drawable.ic_meta_music else Res.drawable.ic_meta_video, Modifier.size(12.dp), ViColors.fileMeta)
                MetaText(fmtSize(t.totalBytes.coerceAtLeast(0)), Modifier.padding(start = 5.dp))
                MetaBar(); MetaText(t.ext.uppercase())
                MetaBar(); MetaText(PlatformMedia.formatDate(t.finishedAt.takeIf { it > 0 } ?: t.createdAt, "yyyy-MM-dd"))
            }
        }
        Box(Modifier.padding(top = 9.dp, end = 12.dp)) {
            Box(Modifier.size(24.dp).clickable { menu = true }, contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_more_thin, Modifier.size(20.dp), ViColors.fileMore, stringResource(Res.string.files_more)) }
            VMenu(menu, { menu = false }, 186, buildList {
                add(Triple(Res.drawable.ic_m_share, stringResource(Res.string.files_share)) { if (!files.shareTask(t)) toast(unhideToShare) })
                add(Triple(Res.drawable.ic_m_hide, stringResource(if (t.hidden) Res.string.files_unhide else Res.string.files_hide)) { dl.setHidden(t.id, !t.hidden); toast(if (t.hidden) movedOut else movedIn) })
                add(Triple(Res.drawable.ic_m_trash, stringResource(Res.string.files_delete)) { confirmDelete = true })
                if (t.kind == Kind.Video) {
                    add(Triple(Res.drawable.ic_m_music, stringResource(Res.string.files_play_as_music)) { files.playTask(t, list, asMusic = true) })
                    add(Triple(Res.drawable.ic_m_convert, stringResource(Res.string.files_convert)) { toast(converting); dl.convertToMusic(t.id) { ok -> toast(if (ok) converted else convertFailed) } })
                }
                if (t.pageUrl.isNotEmpty()) add(Triple(Res.drawable.ic_m_goto, stringResource(Res.string.files_goto)) { nav.open(t.pageUrl) })
                add(Triple(Res.drawable.ic_m_rename, stringResource(Res.string.files_rename)) { rename = true })
                t.videoId?.let { vid -> add(Triple(Res.drawable.ic_m_hd, stringResource(Res.string.files_other_res)) { nav.downloadFor = Video(vid, t.title, thumbnail = t.thumbnail) }) }
            }, offset = DpOffset((-160).dp, (-30).dp))
        }
    }
    if (rename) {
        var name by remember { mutableStateOf(t.title) }
        AlertDialog(onDismissRequest = { rename = false }, title = { Text(stringResource(Res.string.files_rename)) }, text = { OutlinedTextField(name, { name = it }, singleLine = true) },
            confirmButton = { TextButton(onClick = { dl.rename(t.id, name.trim()); rename = false }) { Text(stringResource(Res.string.files_ok)) } },
            dismissButton = { TextButton(onClick = { rename = false }) { Text(stringResource(Res.string.files_cancel)) } })
    }
    if (confirmDelete) AlertDialog(onDismissRequest = { confirmDelete = false }, title = { Text(stringResource(Res.string.files_delete_q)) }, text = { Text(t.fileName) },
        confirmButton = { TextButton(onClick = { dl.delete(t.id); confirmDelete = false }) { Text(stringResource(Res.string.files_delete), color = VRed) } },
        dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text(stringResource(Res.string.files_cancel)) } })
}

@Composable
internal fun MetaBar() = Box(Modifier.padding(horizontal = 8.dp).size(1.dp, 9.dp).background(ViColors.clearCircle))

@Composable
private fun MetaText(text: String, modifier: Modifier = Modifier) =
    Text(text, fontSize = 11.5.sp, lineHeight = 14.7.sp, color = ViColors.fileMeta, maxLines = 1, softWrap = false, modifier = modifier)
