package org.app.vimate.downloads.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import org.app.vimate.appcore.component.ErrorBox
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.component.rememberLoad
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.downloads_added
import org.app.vimate.appres.resources.downloads_audio_quality
import org.app.vimate.appres.resources.downloads_change
import org.app.vimate.appres.resources.downloads_change_toast
import org.app.vimate.appres.resources.downloads_container
import org.app.vimate.appres.resources.downloads_dialog_title
import org.app.vimate.appres.resources.downloads_download_size
import org.app.vimate.appres.resources.downloads_not_available
import org.app.vimate.appres.resources.downloads_save_to
import org.app.vimate.appres.resources.downloads_video_quality
import org.app.vimate.appres.resources.ic_chev_right
import org.app.vimate.appres.resources.ic_close
import org.app.vimate.appres.resources.ic_d_download
import org.app.vimate.appres.resources.ic_folder
import org.app.vimate.appres.resources.ic_nav_music_on
import org.app.vimate.appres.resources.ic_yt_play
import org.app.vimate.downloads.usecase.DownloadSheetUseCase
import org.app.vimate.sharedlogic.download.option.DownloadOption
import org.app.vimate.sharedlogic.download.vid.fmtSize
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private val CardShape = RoundedCornerShape(12.dp)

/**
 * The download sheet as a centred dialog on desktop and tablets: video card, Video and Audio quality sections in
 * two columns, the save folder and a Download button showing the chosen size.
 */
@Composable
internal fun WideDownloadDialog(v: Video, onDismiss: () -> Unit) {
    val useCase = koinInject<DownloadSheetUseCase>()
    val toast = LocalToast.current
    val (state, retry) = rememberLoad(v.id) { useCase.info(v.id) }
    val opts = remember(state.data) { state.data?.let(useCase::options) }
    var selected by remember { mutableStateOf<DownloadOption?>(null) }
    LaunchedEffect(opts) { if (selected == null && opts != null) selected = useCase.defaultOption(opts) }
    var location by remember { mutableStateOf(useCase.location()) }
    val changeToast = stringResource(Res.string.downloads_change_toast)
    val addedToast = stringResource(Res.string.downloads_added)
    val info = state.data?.video

    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(Modifier.width(460.dp).heightIn(max = 680.dp).clip(RoundedCornerShape(16.dp)).background(ViColors.white).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(Res.string.downloads_dialog_title), Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = ViColors.ink)
                Box(Modifier.size(26.dp).clip(CircleShape).clickable(onClick = onDismiss), contentAlignment = Alignment.Center) {
                    Ic(Res.drawable.ic_close, Modifier.size(16.dp), ViColors.ink)
                }
            }
            Row(Modifier.padding(top = 10.dp)) {
                Box(Modifier.size(104.dp, 58.dp).clip(RoundedCornerShape(8.dp)).background(ViColors.pocDarkSurfaceVariant)) {
                    AsyncImage(v.thumbnail.ifEmpty { info?.thumbnail }, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    val dur = info?.duration ?: v.duration
                    if (dur.isNotEmpty()) Text(dur, Modifier.align(Alignment.BottomEnd).padding(5.dp).background(ViColors.shade67, RoundedCornerShape(4.dp))
                        .padding(horizontal = 3.dp), color = Color.White, fontSize = 10.sp)
                }
                Column(Modifier.weight(1f).padding(start = 12.dp)) {
                    Text(info?.title ?: v.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, lineHeight = 17.sp, color = ViColors.ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    val channel = info?.channel?.ifEmpty { null } ?: v.channel
                    if (channel.isNotEmpty()) Text(channel, Modifier.padding(top = 2.dp), fontSize = 11.sp, color = ViColors.metaGrey, maxLines = 1)
                    val meta = listOf(info?.views?.ifEmpty { null } ?: v.views, info?.published?.ifEmpty { null } ?: v.published).filter { it.isNotEmpty() }.joinToString(" · ")
                    if (meta.isNotEmpty()) Text(meta, fontSize = 11.sp, color = ViColors.metaGrey, maxLines = 1)
                }
            }
            Column(Modifier.weight(1f, fill = false).padding(top = 10.dp).verticalScroll(rememberScrollState())) {
                when {
                    state.loading -> Loading()
                    state.error != null -> ErrorBox(state.error!!, retry)
                    opts != null -> {
                        Section(false, stringResource(Res.string.downloads_video_quality), opts.second, selected) { selected = it }
                        Box(Modifier.padding(vertical = 6.dp).fillMaxWidth().height(1.dp).background(ViColors.detailRule))
                        Section(true, stringResource(Res.string.downloads_audio_quality), opts.first, selected) { selected = it }
                    }
                }
            }
            Row(Modifier.padding(top = 10.dp).fillMaxWidth().clip(CardShape).background(ViColors.detailLikePill).padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Ic(Res.drawable.ic_folder, Modifier.size(18.dp), ViColors.ink)
                Column(Modifier.weight(1f).padding(horizontal = 10.dp)) {
                    Text(stringResource(Res.string.downloads_save_to), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ViColors.ink)
                    Text(location, fontSize = 11.sp, color = ViColors.metaGrey, maxLines = 1, overflow = TextOverflow.StartEllipsis)
                }
                Row(Modifier.clip(RoundedCornerShape(8.dp)).background(ViColors.white).clickable {
                    if (useCase.changeFolder()) location = useCase.location() else toast(changeToast)
                }.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(Res.string.downloads_change), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ViColors.ink)
                    Ic(Res.drawable.ic_chev_right, Modifier.padding(start = 2.dp).size(14.dp), ViColors.ink)
                }
            }
            val sel = selected
            Row(Modifier.padding(top = 10.dp).fillMaxWidth().height(40.dp).clip(CardShape).background(if (sel != null) VRed else ViColors.batchDoneOff)
                .clickable(enabled = sel != null && state.data != null) {
                    useCase.enqueue(v, state.data ?: return@clickable, sel ?: return@clickable, info?.title ?: v.title)
                    toast(addedToast)
                    onDismiss()
                }, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Ic(Res.drawable.ic_d_download, Modifier.size(16.dp), Color.White)
                val size = sel?.sizeBytes?.takeIf { it > 0 }?.let { fmtSize(it) }
                Text(if (size != null) stringResource(Res.string.downloads_download_size, size) else stringResource(Res.string.downloads_dialog_title),
                    Modifier.padding(start = 6.dp), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

/** A quality section: red icon, title and container, then the options in two columns. */
@Composable
private fun Section(audio: Boolean, title: String, list: List<DownloadOption>, selected: DownloadOption?, onSelect: (DownloadOption) -> Unit) {
    Row(Modifier.padding(bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(18.dp).clip(RoundedCornerShape(5.dp)).background(VRed), contentAlignment = Alignment.Center) {
            Ic(if (audio) Res.drawable.ic_nav_music_on else Res.drawable.ic_yt_play, Modifier.size(11.dp), Color.White)
        }
        Text(title, Modifier.weight(1f).padding(start = 8.dp), fontWeight = FontWeight.Bold, fontSize = 13.sp, color = ViColors.ink)
        list.firstOrNull()?.let { Text(it.container.uppercase(), fontSize = 11.sp, color = ViColors.metaGrey) }
    }
    if (list.isEmpty()) Text(stringResource(Res.string.downloads_not_available), Modifier.padding(8.dp), color = ViColors.metaGrey, fontSize = 13.sp)
    list.chunked(2).forEach { row ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            row.forEach { o -> OptionCell(o, o == selected, Modifier.weight(1f)) { onSelect(o) } }
            if (row.size == 1) Box(Modifier.weight(1f))
        }
    }
}

/** One quality on a single line: radio, label (container) and its size on the right; the chosen one on a soft red card. */
@Composable
private fun OptionCell(o: DownloadOption, sel: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val fg = if (sel) VRed else ViColors.ink
    val dim = if (sel) VRed else ViColors.metaGrey
    Row(modifier.height(34.dp).clip(RoundedCornerShape(8.dp)).background(if (sel) ViColors.detailDownloadPill else Color.Transparent).clickable(onClick = onClick)
        .padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(16.dp).clip(CircleShape).border(1.5.dp, if (sel) VRed else ViColors.batchRadioOff, CircleShape), contentAlignment = Alignment.Center) {
            if (sel) Box(Modifier.size(8.dp).clip(CircleShape).background(VRed))
        }
        Text(o.label, Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = fg, maxLines = 1)
        Text(stringResource(Res.string.downloads_container, o.container.uppercase()), Modifier.padding(start = 3.dp).weight(1f), fontSize = 10.sp, color = dim, maxLines = 1)
        if (o.sizeBytes > 0) Text(fmtSize(o.sizeBytes), fontSize = 11.sp, color = dim, maxLines = 1)
    }
}
