package org.app.vimate.sites.ui

import org.app.vimate.sharedlogic.download.vid.VidDownloads
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.appcore.component.ErrorBox
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Loadable
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.sharedlogic.platform.PlatformActions
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.vimate.sharedlogic.sites.MediaOption
import org.app.vimate.sharedlogic.sites.SiteMedia
import org.app.vimate.sites.logic.fmtSize
import org.app.vimate.sites.usecase.EnqueueSiteMediaUseCase
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/** VidMate's download sheet for site media (Instagram, Facebook…): Music / Video sections of "label · ext · size" rows. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteDownloadSheet(state: Loadable<SiteMedia>, retry: () -> Unit, onDismiss: () -> Unit) {
    val toast = LocalToast.current
    val scope = rememberCoroutineScope()
    val enqueue = koinInject<EnqueueSiteMediaUseCase>()
    val settings = koinInject<ObservableSettings>()
    val platform = koinInject<PlatformActions>()
    val downloads = koinInject<VidDownloads>()
    val media = state.data
    var title by remember(media) { mutableStateOf(media?.title.orEmpty()) }
    var renaming by remember { mutableStateOf(false) }
    var selected by remember(media) { mutableStateOf(media?.options?.firstOrNull { it.section == "Video" } ?: media?.options?.firstOrNull()) }
    val stat = remember { platform.storageInfo() }
    val blue = ViColors.link
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val screenH = with(LocalDensity.current) { LocalWindowInfo.current.containerSize.height.toDp() }
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = ViColors.sheet,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), contentWindowInsets = { WindowInsets(0) },
        dragHandle = { Box(Modifier.padding(top = 6.dp).size(30.dp, 2.dp).clip(CircleShape).background(ViColors.sheetHandle)) }) {
        Column(Modifier.height(screenH - 164.dp).navigationBarsPadding()) {
            Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
                Spacer(Modifier.height(12.dp))
                Column(Modifier.fillMaxWidth().height(130.dp).clip(RoundedCornerShape(12.dp)).background(ViColors.surface).padding(start = 12.dp, end = 12.dp, top = 12.dp)) {
                    Row {
                        Box(Modifier.size(114.dp, 64.dp).clip(RoundedCornerShape(6.dp)).background(ViColors.grabTile)) {
                            AsyncImage(media?.thumbnail, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            Ic(Res.drawable.ic_play, Modifier.align(Alignment.Center).size(36.dp), ViColors.white)
                        }
                        Box(Modifier.weight(1f).height(64.dp).padding(start = 12.dp)) {
                            if (renaming) BasicTextField(title, { title = it }, Modifier.fillMaxWidth(),
                                textStyle = TextStyle(fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = ViColors.ink))
                            else Text(title, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, lineHeight = 17.sp, color = ViColors.ink, maxLines = 3, overflow = TextOverflow.Ellipsis)
                            Text(stringResource(if (renaming) Res.string.sitesheet_ok else Res.string.sitesheet_rename), Modifier.align(Alignment.BottomEnd).clickable { renaming = !renaming }, color = blue, fontSize = 13.sp)
                        }
                    }
                    Row(Modifier.padding(top = 12.dp).fillMaxWidth().height(30.dp), verticalAlignment = Alignment.CenterVertically) {
                        Ic(Res.drawable.ic_nav_files_on, Modifier.size(24.dp), ViColors.ink)
                        Column(Modifier.weight(1f).padding(start = 10.dp)) {
                            Text(stringResource(Res.string.sitesheet_path, remember(settings[AppKeys.downloadFolder]) { downloads.location() }), maxLines = 1, overflow = TextOverflow.Ellipsis,
                                fontSize = 11.5.sp, color = ViColors.ink, modifier = Modifier.width(193.dp))
                            Text(stringResource(Res.string.sitesheet_free, fmtSize(stat.freeBytes).replace(" ", ""), fmtSize(stat.totalBytes).replace(" ", "")), fontSize = 11.5.sp, color = ViColors.metaGrey)
                        }
                        Text(stringResource(Res.string.sitesheet_change), color = blue, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                LazyColumn(Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(12.dp)).background(ViColors.surface)) {
                    when {
                        state.loading -> item(key = "loading") { Loading() }
                        state.error != null -> item(key = "error") { ErrorBox(state.error!!, retry) }
                        media != null -> media.options.groupBy { it.section }.forEach { (section, opts) ->
                            item(key = "s_$section") { SectionHeader(section) }
                            items(opts, key = { "${it.section}_${it.label}_${it.url}" }) { o -> OptionRow(o, o == selected) { selected = o } }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
            Row(Modifier.padding(start = 12.dp, end = 12.dp, bottom = 12.dp).fillMaxWidth().height(44.dp).clip(CircleShape)
                .background(if (selected != null) ViColors.grabButton else ViColors.grabButtonDisabled).clickable(enabled = selected != null && media != null) {
                    enqueue(media!!, selected!!, title)
                    scope.launch { toast(getString(Res.string.sites_added_to_downloads)) }
                    onDismiss()
                }, horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(24.dp).clip(CircleShape).background(ViColors.surface), contentAlignment = Alignment.Center) { Ic(Res.drawable.ic_d_download, Modifier.size(16.dp), ViColors.grabButton) }
                Text(stringResource(Res.string.sitesheet_download), Modifier.padding(start = 8.dp), color = ViColors.white, fontSize = 18.sp)
            }
        }
    }
}

@Composable
private fun SectionHeader(section: String) {
    val music = section == "Music" // MediaOption.section wire value
    Row(Modifier.padding(start = 12.dp, top = 16.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(24.dp).clip(if (music) CircleShape else RoundedCornerShape(3.dp)).background(VRed), contentAlignment = Alignment.Center) {
            Ic(if (music) Res.drawable.ic_meta_music else Res.drawable.ic_meta_video, Modifier.size(15.dp), ViColors.white)
        }
        Text(section, Modifier.padding(start = 10.dp), fontWeight = FontWeight.Bold, fontSize = 17.sp, color = ViColors.ink)
    }
}

@Composable
private fun OptionRow(o: MediaOption, sel: Boolean, onClick: () -> Unit) {
    val c = if (sel) VRed else ViColors.ink
    Column(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.fillMaxWidth().height(44.dp).padding(start = 10.dp, end = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(20.dp).clip(CircleShape).border(1.5.dp, if (sel) VRed else ViColors.radioOffGrey, CircleShape), contentAlignment = Alignment.Center) {
                if (sel) Box(Modifier.size(10.dp).clip(CircleShape).background(VRed))
            }
            Text(o.label, Modifier.padding(start = 20.dp).width(110.dp), fontWeight = FontWeight.Bold, fontSize = 15.sp, color = c)
            Text(o.ext, Modifier.weight(1f), fontSize = 15.sp, color = if (sel) VRed else ViColors.metaGrey)
            Text(if (o.sizeBytes > 0) fmtSize(o.sizeBytes).replace(" ", "") else "", fontSize = 15.sp, color = if (sel) VRed else ViColors.metaGrey)
        }
        Box(Modifier.padding(horizontal = 4.dp).fillMaxWidth().height(0.7.dp).background(ViColors.gap))
    }
}
