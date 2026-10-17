package org.app.vimate.batch.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.batch.model.BatchOption
import org.app.vimate.batch.model.fmtSize
import org.app.vimate.batch.ui.BatchSelectContract
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource

private data class Section(val icon: DrawableResource, val music: Boolean, val opts: List<BatchOption>)

/** "N items selected" sheet: same formats as the single sheet, sizes summed across all items. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BatchSheet(state: BatchSelectContract.State, onEvent: (BatchSelectContract.Event) -> Unit) {
    val videos = state.sheetVideos
    val stat = state.storage
    val sel = state.sel
    val musicTitle = stringResource(Res.string.batch_music)
    val videoTitle = stringResource(Res.string.batch_video)
    ModalBottomSheet(onDismissRequest = { onEvent(BatchSelectContract.Event.DismissSheet) }, containerColor = ViColors.surface, dragHandle = null, shape = RoundedCornerShape(0.dp)) {
        LazyColumn(Modifier.navigationBarsPadding()) {
            item {
                Text(stringResource(Res.string.batch_items_selected, videos.size), Modifier.fillMaxWidth().padding(vertical = 16.dp), textAlign = TextAlign.Center, fontSize = 20.sp, color = ViColors.ink)
                Box(Modifier.padding(horizontal = 6.dp).fillMaxWidth().height(0.7.dp).background(ViColors.rule))
                Row(Modifier.padding(horizontal = 6.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(stringResource(Res.string.batch_path, state.folder), fontSize = 13.5.sp, color = ViColors.ink, maxLines = 1)
                        Text(stringResource(Res.string.batch_free, fmtSize(stat.freeBytes).replace(" ", ""), fmtSize(stat.totalBytes).replace(" ", "")), fontSize = 11.5.sp, color = ViColors.metaGrey)
                    }
                    Text(stringResource(Res.string.batch_change), color = ViColors.link, fontSize = 14.sp)
                }
                Box(Modifier.padding(horizontal = 6.dp).fillMaxWidth().height(0.7.dp).background(ViColors.rule))
                if (state.resolved < videos.size) Text(stringResource(Res.string.batch_reading_formats, state.resolved, videos.size), Modifier.padding(12.dp), color = ViColors.metaGrey, fontSize = 13.sp)
            }
            listOf(Section(Res.drawable.ic_nav_music_on, true, state.music), Section(Res.drawable.ic_yt_play, false, state.video)).forEach { s ->
                item(key = if (s.music) "h_music" else "h_video") {
                    Row(Modifier.padding(start = 15.dp, top = 18.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(if (s.music) 20.dp else 18.dp, if (s.music) 20.dp else 16.dp).clip(if (s.music) CircleShape else RoundedCornerShape(4.dp)).background(VRed),
                            contentAlignment = Alignment.Center) { Ic(s.icon, Modifier.size(12.dp), ViColors.white) }
                        Text(if (s.music) musicTitle else videoTitle, Modifier.padding(start = 13.dp), fontWeight = FontWeight.Bold, fontSize = 16.3.sp, color = ViColors.ink)
                    }
                }
                items(s.opts.chunked(2), key = { row -> (if (s.music) "m_" else "v_") + row.first().key }) { row ->
                    Row(Modifier.padding(start = 12.dp).height(42.dp)) {
                        row.forEach { o -> OptionCell(o, o.key == sel) { onEvent(BatchSelectContract.Event.SelectOption(o.key)) } }
                    }
                }
            }
            item {
                Row(Modifier.padding(12.dp).fillMaxWidth().height(44.dp).clip(CircleShape).background(ViColors.batchDownload).clickable(enabled = state.sheetVideos.isNotEmpty()) { onEvent(BatchSelectContract.Event.Download) }, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(18.dp).clip(CircleShape).background(ViColors.surface), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_d_download, Modifier.size(12.dp), ViColors.batchDownload) }
                    Text(stringResource(Res.string.batch_download), Modifier.padding(start = 6.dp), color = ViColors.white, fontSize = 17.sp)
                }
            }
        }
    }
}

@Composable
private fun OptionCell(o: BatchOption, on: Boolean, onClick: () -> Unit) {
    Box(Modifier.width(168.dp).fillMaxHeight().clickable(onClick = onClick)) {
        Box(Modifier.offset(y = 1.7.dp).size(23.dp, 24.dp), contentAlignment = Alignment.Center) {
            Box(Modifier.size(19.dp).clip(CircleShape).border(1.5.dp, if (on) VRed else ViColors.batchRadioOff, CircleShape), contentAlignment = Alignment.Center) {
                if (on) Box(Modifier.size(12.dp).clip(CircleShape).background(VRed))
            }
        }
        Row(Modifier.padding(start = 25.dp, top = 3.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(o.label, fontWeight = FontWeight.Bold, fontSize = 12.9.sp, color = if (on) VRed else ViColors.ink)
            Text(stringResource(Res.string.batch_container, o.container), Modifier.padding(start = 2.dp), fontSize = 11.6.sp, color = if (on) VRed else ViColors.metaGrey)
        }
        if (o.sizeBytes > 0) Text(fmtSize(o.sizeBytes).replace(" ", ""), Modifier.padding(start = 27.dp, top = 24.dp), fontSize = 11.6.sp, color = if (on) VRed else ViColors.metaGrey)
    }
}
