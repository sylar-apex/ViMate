package org.app.vimate.downloads.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.app.vimate.appcore.component.ErrorBox
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.component.rememberLoad
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.downloads_added
import org.app.vimate.appres.resources.downloads_change
import org.app.vimate.appres.resources.downloads_change_toast
import org.app.vimate.appres.resources.downloads_download
import org.app.vimate.appres.resources.downloads_free
import org.app.vimate.appres.resources.downloads_music
import org.app.vimate.appres.resources.downloads_ok
import org.app.vimate.appres.resources.downloads_path
import org.app.vimate.appres.resources.downloads_rename
import org.app.vimate.appres.resources.downloads_video
import org.app.vimate.appres.resources.ic_d_download
import org.app.vimate.appres.resources.ic_play
import org.app.vimate.downloads.ui.component.formatSection
import org.app.vimate.downloads.usecase.DownloadSheetUseCase
import org.app.vimate.sharedlogic.download.option.DownloadOption
import org.app.vimate.sharedlogic.download.vid.fmtSize
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * POC DetailScreen DownloadSheet: VidMate's download sheet. #F6F7F9 sheet, white 130dp info card, white
 * format card with Music and Video sections, 44dp red #F23F40 DOWNLOAD pill.
 */
private val WIDE_DIALOG_MIN = 840.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadSheet(video: Video, onDismiss: () -> Unit) {
    // Desktop and tablets get a centred dialog; phones keep VidMate's bottom sheet.
    val wideWindow = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.width.toDp() } >= WIDE_DIALOG_MIN
    if (wideWindow) return WideDownloadDialog(video, onDismiss)
    val v = video
    val useCase = koinInject<DownloadSheetUseCase>()
    val toast = LocalToast.current
    val (state, retry) = rememberLoad(v.id) { useCase.info(v.id) }
    var title by remember(state.data) { mutableStateOf(state.data?.video?.title ?: v.title) }
    var renaming by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<DownloadOption?>(null) }
    val opts = remember(state.data) { state.data?.let(useCase::options) }
    LaunchedEffect(opts) { if (selected == null && opts != null) selected = useCase.defaultOption(opts) }
    val stat = remember { useCase.storage() }
    var location by remember { mutableStateOf(useCase.location()) }
    val blue = ViColors.link
    val changeToast = stringResource(Res.string.downloads_change_toast)
    val addedToast = stringResource(Res.string.downloads_added)
    val musicTitle = stringResource(Res.string.downloads_music)
    val videoTitle = stringResource(Res.string.downloads_video)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val screenH = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = ViColors.sheet, scrimColor = ViColors.transparent,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), contentWindowInsets = { WindowInsets(0) },
        dragHandle = { Box(Modifier.padding(top = 6.dp).size(30.dp, 2.dp).clip(CircleShape).background(ViColors.sheetHandle)) }) {
        Column(Modifier.height(screenH - 164.dp).navigationBarsPadding()) {
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Spacer(Modifier.height(12.dp))
                Column(Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(12.dp)).background(ViColors.surface).padding(start = 12.dp, end = 12.dp, top = 12.dp)) {
                    Row {
                        Box(Modifier.size(114.dp, 64.dp).clip(RoundedCornerShape(6.dp)).background(ViColors.pocDarkSurfaceVariant)) {
                            AsyncImage(v.thumbnail.ifEmpty { state.data?.video?.thumbnail }, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            Ic(Res.drawable.ic_play, Modifier.align(Alignment.Center).size(36.dp), ViColors.white)
                            Text(state.data?.video?.duration ?: v.duration, Modifier.align(Alignment.BottomEnd).padding(end = 4.dp, bottom = 2.dp), color = ViColors.white, fontSize = 13.sp, lineHeight = 17.sp)
                        }
                        Box(Modifier.weight(1f).height(64.dp).padding(start = 12.dp)) {
                            if (renaming) BasicTextField(title, { title = it }, Modifier.fillMaxWidth(), textStyle = TextStyle(fontSize = 15.sp, lineHeight = 17.sp, color = ViColors.ink, fontWeight = FontWeight.Bold))
                            else Text(title, fontWeight = FontWeight.Bold, fontSize = 15.4.sp, lineHeight = 16.7.sp, color = ViColors.ink, maxLines = 3, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(end = 60.dp))
                            Text(stringResource(if (renaming) Res.string.downloads_ok else Res.string.downloads_rename), Modifier.align(Alignment.BottomEnd).offset(y = (-5).dp).clickable { renaming = !renaming }, color = blue, fontSize = 12.1.sp, lineHeight = 16.sp)
                        }
                    }
                    Row(Modifier.padding(top = 12.dp).fillMaxWidth().height(30.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(13.5.dp, 15.dp).clip(RoundedCornerShape(3.dp)).background(ViColors.ink), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_d_download, Modifier.size(10.dp), ViColors.white) }
                        Column(Modifier.weight(1f).padding(start = 18.5.dp)) {
                            Text(stringResource(Res.string.downloads_path, location), maxLines = 1, softWrap = false, overflow = TextOverflow.StartEllipsis,
                                fontSize = 11.6.sp, lineHeight = 16.3.sp, color = ViColors.ink, modifier = Modifier.width(230.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(stringResource(Res.string.downloads_free, fmtSize(stat.freeBytes).replace(" ", ""), fmtSize(stat.totalBytes).replace(" ", "")), fontSize = 11.5.sp, lineHeight = 13.7.sp, color = ViColors.metaGrey)
                                Box(Modifier.padding(start = 8.dp).size(66.dp, 1.3.dp).clip(CircleShape).background(ViColors.rule)) {
                                    val used = if (stat.totalBytes > 0) 1f - stat.freeBytes.toFloat() / stat.totalBytes else 0f
                                    Box(Modifier.fillMaxHeight().fillMaxWidth(used).background(ViColors.clearCircle))
                                }
                            }
                        }
                        Text(stringResource(Res.string.downloads_change), Modifier.clickable { if (useCase.changeFolder()) location = useCase.location() else toast(changeToast) }, color = blue, fontSize = 12.1.sp, lineHeight = 16.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                LazyVerticalGrid(GridCells.Fixed(2), Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(12.dp)).background(ViColors.surface),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 12.dp)) {
                    when {
                        state.loading -> item(span = { GridItemSpan(maxLineSpan) }) { Loading() }
                        state.error != null -> item(span = { GridItemSpan(maxLineSpan) }) { ErrorBox(state.error!!, retry) }
                        opts != null -> {
                            formatSection(true, musicTitle, opts.first, selected) { selected = it }
                            formatSection(false, videoTitle, opts.second, selected) { selected = it }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(bottom = 12.dp)) {
                Row(Modifier.fillMaxWidth().height(44.dp).clip(CircleShape).background(if (selected != null) ViColors.batchDownload else ViColors.batchDoneOff).clickable(enabled = selected != null) {
                    val o = selected ?: return@clickable
                    val info = state.data ?: return@clickable
                    useCase.enqueue(v, info, o, title)
                    toast(addedToast)
                    onDismiss()
                }, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(18.dp).clip(CircleShape).background(ViColors.surface), contentAlignment = Alignment.Center) {
                        Ic(Res.drawable.ic_d_download, Modifier.size(12.dp), ViColors.batchDownload)
                    }
                    Text(stringResource(Res.string.downloads_download), Modifier.padding(start = 6.dp), color = ViColors.white, fontSize = 15.5.sp, lineHeight = 19.sp)
                }
            }
        }
    }
}
