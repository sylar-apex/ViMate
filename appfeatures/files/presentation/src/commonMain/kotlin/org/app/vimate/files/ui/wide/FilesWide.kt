package org.app.vimate.files.ui.wide

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.DrawableResource
import org.app.vimate.appcore.component.openDetail
import org.app.vimate.appcore.component.Thumb
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenu
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.app.kmediaplayer.platform.PlatformMedia
import org.app.vimate.appcore.adaptive.WideButton
import org.app.vimate.appcore.adaptive.WideCard
import org.app.vimate.appcore.adaptive.WideSpec
import org.app.vimate.appcore.adaptive.hover
import org.app.vimate.appcore.adaptive.rememberHover
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.files.ui.component.DoneActions
import org.app.vimate.files.ui.component.LocalTab
import org.app.vimate.files.ui.component.FilesSearchField
import org.app.vimate.files.ui.component.inQueueOrder
import org.app.vimate.files.ui.component.PrivateGate
import org.app.vimate.files.usecase.FilesUseCase
import org.app.vimate.sharedlogic.download.option.Kind
import org.app.vimate.sharedlogic.download.vid.TaskState
import org.app.vimate.sharedlogic.download.vid.VidTask
import org.app.vimate.sharedlogic.download.vid.fmtSize
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/** Tablet / desktop My Files: header with the storage bar, icon tabs, then Downloading / Downloaded / Hot videos cards. */
@Composable
internal fun FilesWide() {
    val nav = LocalAppNavigator.current
    val files = koinInject<FilesUseCase>()
    var tab by rememberSaveable { mutableStateOf(0) }
    val stat = remember { files.storage() }
    val all by files.tasks.collectAsState()
    val activeCount = all.count { !it.hidden && it.state != TaskState.Done }
    // No page header: the side bar names the page; storage, tabs and lists start at the top.
    Column(Modifier.fillMaxSize().background(ViColors.wideCanvas).statusBarsPadding().padding(top = 20.dp)) {
        StorageBar(stat.usedBytes, stat.totalBytes, Modifier.padding(start = WideSpec.gutter, bottom = 12.dp))
        FilesTabs(tab, activeCount, Modifier.padding(start = WideSpec.gutter, bottom = 12.dp)) { tab = it }
        when (tab) {
            0 -> DownloadsWide()
            else -> WideCard(Modifier.padding(start = WideSpec.gutter, end = WideSpec.gutter, bottom = WideSpec.bottomInset).fillMaxSize(), padding = 8.dp) {
                when (tab) {
                    1 -> LocalTab(audio = false)
                    2 -> LocalTab(audio = true)
                    else -> PrivateGate()
                }
            }
        }
    }
}

/** "702.5 GB of 1858.1 GB used" with a red usage bar and the percentage. */
@Composable
private fun StorageBar(used: Long, total: Long, modifier: Modifier) {
    val frac = if (total > 0) (used.toFloat() / total).coerceIn(0f, 1f) else 0f
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(Res.string.wide_files_sub, fmtSize(used), fmtSize(total)), color = ViColors.sectionLabel, fontSize = 12.sp)
        Box(Modifier.padding(start = 12.dp).size(160.dp, 5.dp).clip(CircleShape).background(ViColors.chipIdle)) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(frac).clip(CircleShape).background(VRed))
        }
        Text("${(frac * 100).toInt()}%", Modifier.padding(start = 8.dp), color = ViColors.sectionLabel, fontSize = 11.sp)
    }
}

/** Downloads / Videos / Music / Private with icons; Downloads carries a badge while anything is downloading. */
@Composable
private fun FilesTabs(selected: Int, activeCount: Int, modifier: Modifier, onSelect: (Int) -> Unit) {
    val tabs = listOf(
        Res.drawable.ic_download to Res.string.files_tab_downloads,
        Res.drawable.ic_nav_video to Res.string.files_tab_videos,
        Res.drawable.ic_nav_music to Res.string.files_tab_music,
        Res.drawable.ic_lock to Res.string.files_tab_private,
    )
    Row(modifier.clip(RoundedCornerShape(10.dp)).background(ViColors.chipIdle).padding(3.dp), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
        tabs.forEachIndexed { i, (icon, label) ->
            val sel = i == selected
            val fg = if (sel) VRed else ViColors.ink
            Row(Modifier.height(30.dp).clip(RoundedCornerShape(8.dp)).background(if (sel) ViColors.detailDownloadPill else Color.Transparent)
                .clickable { onSelect(i) }.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Ic(icon, Modifier.size(15.dp), fg)
                Text(stringResource(label), Modifier.padding(start = 6.dp), color = fg, fontSize = 12.5.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Medium)
                // Pill that grows with the number ("228" fits; above 999 shows "999+").
                if (i == 0 && activeCount > 0) Box(Modifier.padding(start = 6.dp).height(17.dp).clip(CircleShape).background(VRed).padding(horizontal = 5.dp), contentAlignment = Alignment.Center) {
                    Text(if (activeCount > 999) "999+" else "$activeCount", color = Color.White, fontSize = 10.sp, style = TextStyle(lineHeight = 10.sp, lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.Both)), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private enum class Sort(val label: StringResource, val order: Comparator<VidTask>) {
    Newest(Res.string.wide_files_sort_newest, compareByDescending { it.finishedAt.takeIf { f -> f > 0 } ?: it.createdAt }),
    Oldest(Res.string.wide_files_sort_oldest, compareBy { it.finishedAt.takeIf { f -> f > 0 } ?: it.createdAt }),
    Name(Res.string.wide_files_sort_name, compareBy { it.title.lowercase() }),
    Size(Res.string.wide_files_sort_size, compareByDescending { it.totalBytes }),
}

@Composable
private fun DownloadsWide() {
    val files = koinInject<FilesUseCase>()
    val nav = LocalAppNavigator.current
    val open = openDetail()
    val all by files.tasks.collectAsState()
    var query by rememberSaveable { mutableStateOf("") }
    val visible = all.filter { !it.hidden && (query.isBlank() || it.title.contains(query.trim(), ignoreCase = true)) }
    val active = visible.filter { it.state != TaskState.Done }.inQueueOrder()
    var sort by rememberSaveable { mutableStateOf(Sort.Newest) }
    var grid by rememberSaveable { mutableStateOf(false) }
    val done = visible.filter { it.state == TaskState.Done }.sortedWith(sort.order)
    val hot by produceState<List<Video>>(emptyList()) { value = files.hotVideos() }
    LazyColumn(
        contentPadding = PaddingValues(start = WideSpec.gutter, end = WideSpec.gutter, bottom = WideSpec.bottomInset),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Same toolbar as Videos / Music: search, sort, grid / list.
        item(key = "search") {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FilesSearchField(query, { query = it }, Modifier.weight(1f))
                Box(Modifier.padding(start = 8.dp)) { SortMenu(sort) { sort = it } }
                Row(Modifier.padding(start = 8.dp).clip(RoundedCornerShape(10.dp)).background(ViColors.chipIdle).padding(3.dp)) {
                    ViewToggle(Res.drawable.ic_v_grid, stringResource(Res.string.wide_files_grid), grid) { grid = true }
                    ViewToggle(Res.drawable.ic_v_list, stringResource(Res.string.wide_files_list), !grid) { grid = false }
                }
            }
        }
        if (active.isNotEmpty()) item(key = "active") {
            WideCard(Modifier.fillMaxWidth(), padding = 12.dp) {
                val anyRunning = active.any { it.state == TaskState.Running || it.state == TaskState.Queued }
                SectionHeader("${stringResource(Res.string.wide_files_downloading)} (${active.size})") {
                    TextAction(if (anyRunning) Res.drawable.ic_pause else Res.drawable.ic_m_resume,
                        stringResource(if (anyRunning) Res.string.files_pause_all else Res.string.files_resume_all)) {
                        if (anyRunning) files.downloads.pauseAll() else files.downloads.resumeAll()
                    }
                }
                active.forEach { t -> ActiveRowWide(t) }
            }
        }
        item(key = "done") {
            WideCard(Modifier.fillMaxWidth(), padding = 16.dp) {
                SectionHeader("${stringResource(Res.string.wide_files_library)} (${done.size})") {}
                when {
                    done.isEmpty() -> Column(Modifier.fillMaxWidth().padding(vertical = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.size(64.dp).clip(CircleShape).background(ViColors.tintRed), contentAlignment = Alignment.Center) {
                            Ic(Res.drawable.ic_download, Modifier.size(30.dp), VRed)
                        }
                        Text(stringResource(Res.string.wide_files_empty_title), Modifier.padding(top = 14.dp), color = ViColors.ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(stringResource(Res.string.wide_files_empty_sub), Modifier.padding(top = 4.dp, bottom = 16.dp), color = ViColors.sectionLabel, fontSize = 13.sp)
                        WideButton(stringResource(Res.string.wide_files_browse)) { nav.home(Tab.HOME) }
                    }
                    grid -> BoxWithConstraints(Modifier.fillMaxWidth()) {
                        val cols = (maxWidth / 260.dp).toInt().coerceAtLeast(1)
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            done.chunked(cols).forEach { row ->
                                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                                    row.forEach { t -> Box(Modifier.weight(1f)) { DownloadCard(t, done) } }
                                    repeat(cols - row.size) { Spacer(Modifier.weight(1f)) }
                                }
                            }
                        }
                    }
                    // List view: compact rows, two per line.
                    else -> done.chunked(2).forEach { pair ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            pair.forEach { t -> Box(Modifier.weight(1f)) { DownloadRow(t, done) } }
                            if (pair.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
        if (hot.isNotEmpty()) item(key = "hot") {
            WideCard(Modifier.fillMaxWidth(), padding = 16.dp) {
                Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Ic(Res.drawable.ic_fire, Modifier.size(16.dp), VRed)
                    Text(stringResource(Res.string.common_hot_videos), Modifier.weight(1f).padding(start = 6.dp), color = ViColors.ink, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                    TextAction(null, stringResource(Res.string.wide_files_see_all)) { nav.home(Tab.HOME) }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    hot.take(4).forEach { v ->
                        Thumb(v.thumbnail, Modifier.weight(1f).aspectRatio(16f / 9).clickable { open(v) }, corner = 10, duration = v.duration)
                    }
                }
            }
        }
    }
}

/** Red accent bar, bold title and trailing actions. */
@Composable
private fun SectionHeader(title: String, trailing: @Composable RowScope.() -> Unit) {
    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(3.dp, 16.dp).clip(CircleShape).background(VRed))
        Text(title, Modifier.weight(1f).padding(start = 8.dp), color = ViColors.ink, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
        trailing()
    }
}

@Composable
private fun TextAction(icon: DrawableResource?, label: String, onClick: () -> Unit) {
    Row(Modifier.clip(RoundedCornerShape(8.dp)).clickable(onClick = onClick).padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        icon?.let { Ic(it, Modifier.padding(end = 5.dp).size(14.dp), ViColors.ink) }
        Text(label, color = ViColors.ink, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SortMenu(sort: Sort, onSort: (Sort) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        Row(Modifier.height(38.dp).clip(RoundedCornerShape(10.dp)).background(ViColors.chipIdle).clickable { open = true }.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(sort.label), color = ViColors.ink, fontSize = 12.sp)
            Ic(Res.drawable.ic_chevron_down, Modifier.padding(start = 4.dp).size(12.dp), ViColors.ink)
        }
        DropdownMenu(open, { open = false }) {
            Sort.entries.forEach { s -> DropdownMenuItem(text = { Text(stringResource(s.label)) }, onClick = { onSort(s); open = false }) }
        }
    }
}

@Composable
private fun ViewToggle(icon: DrawableResource, label: String, selected: Boolean, onClick: () -> Unit) {
    Box(Modifier.size(32.dp).clip(RoundedCornerShape(7.dp)).background(if (selected) ViColors.detailDownloadPill else Color.Transparent).clickable(onClick = onClick),
        contentAlignment = Alignment.Center) {
        Ic(icon, Modifier.size(14.dp), if (selected) VRed else ViColors.ink, label)
    }
}

/** A running / queued / paused download: thumbnail, title, "x of y • speed • time left", red progress and pause / cancel. */
@Composable
private fun ActiveRowWide(t: VidTask) {
    val dl = koinInject<FilesUseCase>().downloads
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp).clip(RoundedCornerShape(10.dp)).border(1.dp, ViColors.wideHairline, RoundedCornerShape(10.dp)).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Thumb(t.thumbnail, Modifier.width(120.dp).aspectRatio(16f / 9), corner = 8, duration = t.duration)
        Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                KindTag(t)
                Text(t.title, Modifier.padding(start = 8.dp).weight(1f), color = ViColors.ink, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            val speed = dl.speedOf(t.id)
            val status = when (t.state) {
                TaskState.Running -> buildList {
                    add(stringResource(Res.string.wide_files_of, fmtSize(t.doneBytes), if (t.totalBytes > 0) fmtSize(t.totalBytes) else "?"))
                    if (speed > 0) add(stringResource(Res.string.wide_files_speed, fmtSize(speed)))
                    if (speed > 0 && t.totalBytes > t.doneBytes) {
                        val secs = ((t.totalBytes - t.doneBytes) / speed).toInt()
                        add(if (secs < 60) stringResource(Res.string.wide_files_eta_s, secs) else stringResource(Res.string.wide_files_eta_m, secs / 60))
                    }
                }.joinToString("  •  ")
                TaskState.Queued -> stringResource(Res.string.files_waiting)
                TaskState.Paused -> stringResource(Res.string.files_paused, fmtSize(t.doneBytes))
                TaskState.Merging -> stringResource(Res.string.files_merging)
                TaskState.Failed -> stringResource(Res.string.files_failed, t.error.toString())
                TaskState.Done -> ""
            }
            Text(status, Modifier.padding(top = 4.dp), color = if (t.state == TaskState.Failed) VRed else ViColors.sectionLabel, fontSize = 12.sp, maxLines = 1)
            Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f).height(6.dp).clip(CircleShape).background(ViColors.chipIdle)) {
                    Box(Modifier.fillMaxHeight().fillMaxWidth(t.progress.coerceIn(0f, 1f)).clip(CircleShape).background(VRed))
                }
                Text("${(t.progress * 100).toInt()}%", Modifier.padding(start = 10.dp).width(36.dp), color = VRed, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        val running = t.state == TaskState.Running || t.state == TaskState.Queued
        RoundButton(if (running) Res.drawable.ic_p_pause else Res.drawable.ic_p_play, if (running) stringResource(Res.string.files_pause) else stringResource(Res.string.files_resume),
            ViColors.detailDownloadPill, VRed) { if (running) dl.pause(t.id) else dl.resume(t.id) }
        Spacer(Modifier.width(8.dp))
        RoundButton(Res.drawable.ic_close, stringResource(Res.string.files_cancel), ViColors.chipIdle, ViColors.ink) { dl.delete(t.id) }
    }
}

@Composable
private fun RoundButton(icon: DrawableResource, label: String, bg: Color, tint: Color, onClick: () -> Unit) {
    Box(Modifier.size(32.dp).clip(CircleShape).background(bg).clickable(onClick = onClick), contentAlignment = Alignment.Center) {
        Ic(icon, Modifier.size(15.dp), tint, label)
    }
}

/** A finished download as a list row: large thumbnail with badges, two-line name, size • type • date and the ⋮ menu. */
@Composable
private fun DownloadRow(t: VidTask, list: List<VidTask>) {
    val files = koinInject<FilesUseCase>()
    val h = rememberHover()
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp).clip(RoundedCornerShape(10.dp)).background(if (h.hovered) ViColors.chipIdle else ViColors.wideCanvas)
        .border(1.dp, ViColors.wideHairline, RoundedCornerShape(10.dp)).hover(h).clickable { files.playTask(t, list) }.padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.width(140.dp).aspectRatio(16f / 9).clip(RoundedCornerShape(8.dp)).background(if (t.kind == Kind.Music) VRed else ViColors.pocDarkSurfaceVariant)) {
            if (t.kind == Kind.Music && t.thumbnail.isEmpty()) Icon(Icons.Filled.MusicNote, null, Modifier.align(Alignment.Center).size(30.dp), tint = Color.White)
            else AsyncImage(t.thumbnail, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Row(Modifier.align(Alignment.BottomStart).padding(5.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                if (t.kind == Kind.Video && t.quality.isNotEmpty()) Badge(t.quality.uppercase())
                if (t.subtitleUrl != null) Badge(stringResource(Res.string.files_cc))
            }
            if (t.duration.isNotEmpty()) Box(Modifier.align(Alignment.BottomEnd).padding(5.dp)) { Badge(t.duration) }
        }
        Column(Modifier.weight(1f).padding(horizontal = 14.dp)) {
            Text(t.fileName.substringBeforeLast('.'), color = ViColors.ink, fontSize = 13.5.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Text(listOf(fmtSize(t.totalBytes.coerceAtLeast(0)), t.ext.uppercase(), PlatformMedia.formatDate(t.finishedAt.takeIf { it > 0 } ?: t.createdAt, "yyyy-MM-dd")).joinToString("  •  "),
                Modifier.padding(top = 4.dp), color = ViColors.sectionLabel, fontSize = 11.5.sp, maxLines = 1)
        }
        Box(Modifier.size(28.dp).clip(CircleShape), contentAlignment = Alignment.Center) { DoneActions(t, list, ViColors.ink) }
    }
}

/** A finished download: thumbnail (or music tile), quality / duration badges, name, size · type · date and the ⋮ menu. */
@Composable
private fun DownloadCard(t: VidTask, list: List<VidTask>) {
    val files = koinInject<FilesUseCase>()
    val h = rememberHover()
    val lift by animateDpAsState(if (h.hovered) 12.dp else 0.dp)
    val shape = RoundedCornerShape(16.dp)
    Column(
        Modifier.fillMaxWidth().shadow(lift, shape, ambientColor = ViColors.cardShadow, spotColor = ViColors.cardShadow)
            .clip(shape).background(ViColors.tileBg).border(1.dp, ViColors.wideHairline, shape).hover(h).clickable { files.playTask(t, list) },
    ) {
        Box(Modifier.fillMaxWidth().aspectRatio(16f / 9).background(if (t.kind == Kind.Music) VRed else ViColors.pocDarkSurfaceVariant)) {
            if (t.kind == Kind.Music && t.thumbnail.isEmpty()) Icon(Icons.Filled.MusicNote, null, Modifier.align(Alignment.Center).size(44.dp), tint = Color.White)
            else AsyncImage(t.thumbnail, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(48.dp).background(Brush.verticalGradient(listOf(Color.Transparent, ViColors.shade60))))
            Row(Modifier.align(Alignment.BottomStart).padding(10.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (t.kind == Kind.Video && t.quality.isNotEmpty()) Badge(t.quality.uppercase())
                if (t.subtitleUrl != null) Badge(stringResource(Res.string.files_cc))
            }
            if (t.duration.isNotEmpty()) Box(Modifier.align(Alignment.BottomEnd).padding(10.dp)) { Badge(t.duration) }
        }
        Row(Modifier.padding(start = 14.dp, end = 8.dp, top = 12.dp, bottom = 12.dp)) {
            Column(Modifier.weight(1f)) {
                Text(t.fileName.substringBeforeLast('.'), color = ViColors.ink, fontSize = 14.5.sp, lineHeight = 19.sp, fontWeight = FontWeight.SemiBold,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(
                    listOf(fmtSize(t.totalBytes.coerceAtLeast(0)), t.ext.uppercase(), PlatformMedia.formatDate(t.finishedAt.takeIf { it > 0 } ?: t.createdAt, "yyyy-MM-dd"))
                        .joinToString("  •  "),
                    Modifier.padding(top = 6.dp), color = ViColors.sectionLabel, fontSize = 12.5.sp, maxLines = 1,
                )
            }
            Spacer(Modifier.size(4.dp))
            Box(Modifier.size(32.dp).clip(CircleShape), contentAlignment = Alignment.Center) { DoneActions(t, list, ViColors.ink) }
        }
    }
}

@Composable
private fun Badge(text: String) {
    Text(text, Modifier.clip(RoundedCornerShape(4.dp)).background(ViColors.shade60).padding(horizontal = 4.dp, vertical = 1.dp),
        color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
}

/** "VIDEO · 720P" / "AUDIO · 48K" chip; the quality is the one asked for until the download resolves it. */
@Composable
private fun KindTag(t: VidTask) {
    val audio = t.kind == Kind.Music
    val c = if (audio) Color(0xFF8B5CF6) else Color(0xFF3B82F6)
    val label = (if (audio) "AUDIO" else "VIDEO") + if (t.quality.isNotEmpty()) " · ${t.quality.uppercase()}" else ""
    Text(label, Modifier.clip(RoundedCornerShape(5.dp)).background(c.copy(alpha = .18f)).padding(horizontal = 6.dp, vertical = 2.dp),
        color = c, fontSize = 10.sp, fontWeight = FontWeight.Bold)
}
