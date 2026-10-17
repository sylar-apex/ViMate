package org.app.vimate.files.ui.wide

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import org.app.vimate.appcore.adaptive.WideIconButton
import org.app.vimate.appcore.adaptive.WidePage
import org.app.vimate.appcore.adaptive.WideSectionTitle
import org.app.vimate.appcore.adaptive.WideSpec
import org.app.vimate.appcore.adaptive.WideTabs
import org.app.vimate.appcore.adaptive.WideVideoCard
import org.app.vimate.appcore.adaptive.hover
import org.app.vimate.appcore.adaptive.rememberHover
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.files.ui.component.ActiveRow
import org.app.vimate.files.ui.component.DoneActions
import org.app.vimate.files.ui.component.LocalTab
import org.app.vimate.files.ui.component.PrivateGate
import org.app.vimate.files.usecase.FilesUseCase
import org.app.vimate.sharedlogic.download.option.Kind
import org.app.vimate.sharedlogic.download.vid.TaskState
import org.app.vimate.sharedlogic.download.vid.VidTask
import org.app.vimate.sharedlogic.download.vid.fmtSize
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private const val PRIVATE_TAB = 3

/** Tablet / desktop My Files: header with storage and actions, pill tabs, downloads as a card grid. */
@Composable
internal fun FilesWide() {
    val nav = LocalAppNavigator.current
    val files = koinInject<FilesUseCase>()
    var tab by rememberSaveable { mutableStateOf(0) }
    val stat = remember { files.storage() }
    WidePage(
        stringResource(Res.string.files_title),
        stringResource(Res.string.wide_files_sub, fmtSize(stat.usedBytes), fmtSize(stat.totalBytes)),
        actions = {
            WideIconButton(Res.drawable.ic_m_pausec, stringResource(Res.string.files_pause_all)) { files.downloads.pauseAll() }
            WideIconButton(Res.drawable.ic_m_resume, stringResource(Res.string.files_resume_all)) { files.downloads.resumeAll() }
            WideIconButton(Res.drawable.ic_junk, stringResource(Res.string.files_junk)) { nav.go(Routes.JunkClean) }
            WideIconButton(Res.drawable.ic_m_setting, stringResource(Res.string.files_setting)) { nav.go(Routes.SettingsGroup("Download")) }
        },
    ) {
        WideTabs(
            listOf(Res.string.files_tab_downloads, Res.string.files_tab_videos, Res.string.files_tab_music, Res.string.files_tab_private).map { stringResource(it) },
            tab, Modifier.padding(start = WideSpec.gutter, bottom = 18.dp), icons = mapOf(PRIVATE_TAB to Res.drawable.ic_lock),
        ) { tab = it }
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

@Composable
private fun DownloadsWide() {
    val files = koinInject<FilesUseCase>()
    val nav = LocalAppNavigator.current
    val all by files.tasks.collectAsState()
    val visible = all.filter { !it.hidden }
    val active = visible.filter { it.state != TaskState.Done }
    val done = visible.filter { it.state == TaskState.Done }
    val hot by produceState<List<Video>>(emptyList()) { value = files.hotVideos() }
    LazyVerticalGrid(
        GridCells.Adaptive(260.dp),
        contentPadding = PaddingValues(start = WideSpec.gutter, end = WideSpec.gutter, bottom = WideSpec.bottomInset),
        horizontalArrangement = Arrangement.spacedBy(20.dp), verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        if (active.isNotEmpty()) item(key = "active", span = { GridItemSpan(maxLineSpan) }) {
            WideCard(Modifier.fillMaxWidth(), padding = 12.dp) {
                WideSectionTitle("${stringResource(Res.string.wide_files_downloading)}  ·  ${active.size}", Modifier.padding(start = 8.dp, top = 6.dp))
                active.forEach { t -> ActiveRow(t, false, false) {} }
            }
        }
        item(key = "doneTitle", span = { GridItemSpan(maxLineSpan) }) {
            WideSectionTitle("${stringResource(Res.string.wide_files_library)}  ·  ${done.size}")
        }
        if (done.isEmpty()) item(key = "empty", span = { GridItemSpan(maxLineSpan) }) {
            WideCard(Modifier.fillMaxWidth(), padding = 40.dp) {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(72.dp).clip(CircleShape).background(ViColors.tintRed), contentAlignment = Alignment.Center) {
                        Ic(Res.drawable.ic_download, Modifier.size(34.dp), VRed)
                    }
                    Text(stringResource(Res.string.wide_files_empty_title), Modifier.padding(top = 18.dp), color = ViColors.ink, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(stringResource(Res.string.wide_files_empty_sub), Modifier.padding(top = 6.dp, bottom = 20.dp), color = ViColors.sectionLabel, fontSize = 14.sp)
                    WideButton(stringResource(Res.string.wide_files_browse)) { nav.home(Tab.HOME) }
                }
            }
        }
        items(done, key = { "d${it.id}" }) { t -> DownloadCard(t, done) }
        if (hot.isNotEmpty()) item(key = "hotTitle", span = { GridItemSpan(maxLineSpan) }) {
            WideSectionTitle(stringResource(Res.string.common_hot_videos), Modifier.padding(top = 12.dp))
        }
        items(hot, key = { "h${it.id}" }) { v -> WideVideoCard(v, onDownload = { nav.downloadFor = v }) }
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
    Text(text, Modifier.clip(RoundedCornerShape(6.dp)).background(ViColors.shade60).padding(horizontal = 6.dp, vertical = 2.dp),
        color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold)
}
