package org.app.vimate.batch.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Thumb
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.batch.model.BatchOption
import org.app.vimate.batch.ui.BatchSelectContract
import org.app.vimate.sharedlogic.download.vid.VidDownloads
import org.app.vimate.sharedlogic.platform.PlatformActions
import org.app.vimate.sharedlogic.settings.AppKeys
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/** Batch download as the same centred dialog as a single video: stacked covers, quality grids, save folder, Download. */
@Composable
internal fun WideBatchDialog(state: BatchSelectContract.State, onEvent: (BatchSelectContract.Event) -> Unit) {
    val downloads = koinInject<VidDownloads>()
    val platform = koinInject<PlatformActions>()
    val settings = koinInject<ObservableSettings>()
    var location by remember { mutableStateOf(downloads.location()) }
    val videos = state.sheetVideos
    val close = { onEvent(BatchSelectContract.Event.DismissSheet) }
    Dialog(onDismissRequest = close, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(Modifier.width(460.dp).clip(RoundedCornerShape(16.dp)).background(ViColors.surface).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.downloads_dialog_title), Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ViColors.ink)
                Box(Modifier.size(26.dp).clip(CircleShape).clickable(onClick = close), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_close, Modifier.size(16.dp), ViColors.ink) }
            }
            Row(Modifier.padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                // Up to three covers fanned out, newest pick on top.
                Box(Modifier.width(132.dp).height(62.dp)) {
                    videos.take(3).reversed().forEachIndexed { i, v ->
                        val back = minOf(videos.size, 3) - 1 - i
                        Thumb(v.thumbnail, Modifier.offset(x = (back * 14).dp, y = (back * 2).dp).width(104.dp).height(58.dp).border(1.dp, ViColors.surface, RoundedCornerShape(8.dp)), corner = 8)
                    }
                }
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(stringResource(Res.string.batch_items_selected, videos.size), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ViColors.ink)
                    Text(videos.firstOrNull()?.title.orEmpty(), Modifier.padding(top = 2.dp), fontSize = 11.sp, color = ViColors.metaGrey, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            Section(false, stringResource(Res.string.downloads_video_quality), state.video, state.sel, onEvent)
            Box(Modifier.padding(vertical = 6.dp).fillMaxWidth().height(1.dp).background(ViColors.detailRule))
            Section(true, stringResource(Res.string.downloads_audio_quality), state.music, state.sel, onEvent)
            Text(stringResource(Res.string.batch_nearest_note), Modifier.padding(top = 8.dp), fontSize = 11.sp, color = ViColors.sectionLabel)
            Row(Modifier.padding(top = 10.dp).fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(ViColors.detailLikePill).padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Ic(Res.drawable.ic_folder, Modifier.size(18.dp), ViColors.ink)
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text(stringResource(Res.string.downloads_save_to), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ViColors.ink)
                    Text(location, fontSize = 11.sp, color = ViColors.metaGrey, maxLines = 1, overflow = TextOverflow.StartEllipsis)
                }
                Row(Modifier.clip(RoundedCornerShape(8.dp)).background(ViColors.surface).clickable {
                    platform.pickFolder()?.let { settings[AppKeys.downloadFolder] = it; location = downloads.location() }
                }.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(Res.string.downloads_change), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ViColors.ink)
                    Ic(Res.drawable.ic_chev_right, Modifier.padding(start = 2.dp).size(14.dp), ViColors.ink)
                }
            }
            Row(Modifier.padding(top = 10.dp).fillMaxWidth().height(40.dp).clip(RoundedCornerShape(12.dp)).background(if (videos.isNotEmpty()) VRed else ViColors.batchDoneOff)
                .clickable(enabled = videos.isNotEmpty()) { onEvent(BatchSelectContract.Event.Download) }, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Ic(Res.drawable.ic_d_download, Modifier.size(16.dp), Color.White)
                Text(stringResource(Res.string.batch_download_n, videos.size), Modifier.padding(start = 6.dp), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun Section(audio: Boolean, title: String, list: List<BatchOption>, sel: String, onEvent: (BatchSelectContract.Event) -> Unit) {
    Row(Modifier.padding(top = 10.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(18.dp).clip(RoundedCornerShape(5.dp)).background(VRed), contentAlignment = Alignment.Center) {
            Ic(if (audio) Res.drawable.ic_nav_music_on else Res.drawable.ic_yt_play, Modifier.size(11.dp), Color.White)
        }
        Text(title, Modifier.weight(1f).padding(start = 8.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ViColors.ink)
        Text(if (audio) "M4A" else "MP4", fontSize = 11.sp, color = ViColors.metaGrey)
    }
    list.chunked(2).forEach { row ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            row.forEach { o ->
                val on = o.key == sel
                val fg = if (on) VRed else ViColors.ink
                Row(Modifier.weight(1f).height(34.dp).clip(RoundedCornerShape(8.dp)).background(if (on) ViColors.detailDownloadPill else Color.Transparent)
                    .clickable { onEvent(BatchSelectContract.Event.SelectOption(o.key)) }.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(16.dp).clip(CircleShape).border(1.5.dp, if (on) VRed else ViColors.batchRadioOff, CircleShape), contentAlignment = Alignment.Center) {
                        if (on) Box(Modifier.size(8.dp).clip(CircleShape).background(VRed))
                    }
                    Text(o.label, Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = fg)
                    Text("(${o.container})", Modifier.padding(start = 3.dp), fontSize = 10.sp, color = if (on) VRed else ViColors.metaGrey)
                }
            }
            if (row.size == 1) Box(Modifier.weight(1f))
        }
    }
}

/** The batch download dialog for any list of [videos] (e.g. a playlist in select mode); [onAdded] gets how many were queued. */
@Composable
fun BatchDownloadDialog(videos: List<org.app.vimate.sharedlogic.yt.Video>, onDismiss: () -> Unit, onAdded: (Int) -> Unit) {
    val enqueue = koinInject<org.app.vimate.batch.usecase.EnqueueBatchUseCase>()
    var sel by remember { mutableStateOf("360P|MP4") }
    val state = BatchSelectContract.State(sheetVideos = videos, music = org.app.vimate.batch.usecase.BatchQualities.music,
        video = org.app.vimate.batch.usecase.BatchQualities.video, sel = sel)
    WideBatchDialog(state) { e ->
        when (e) {
            is BatchSelectContract.Event.SelectOption -> sel = e.key
            BatchSelectContract.Event.Download -> { val n = enqueue(videos, sel); onDismiss(); onAdded(n) }
            BatchSelectContract.Event.DismissSheet -> onDismiss()
            else -> {}
        }
    }
}
