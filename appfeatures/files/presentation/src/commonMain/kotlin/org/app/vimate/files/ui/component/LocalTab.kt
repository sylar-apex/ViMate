package org.app.vimate.files.ui.component

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
internal fun LocalTab(audio: Boolean) {
    val files = koinInject<FilesUseCase>()
    val (state, retry) = rememberLoad(audio) { files.localMedia(audio) }
    val list = state.data.orEmpty()
    if (!state.loading && list.isEmpty()) {
        Box(Modifier.fillMaxSize().padding(bottom = 60.dp), contentAlignment = Alignment.Center) {
            Text(stringResource(Res.string.files_no_data), fontSize = 15.sp, lineHeight = 19.sp, color = ViColors.fileMeta)
        }
        return
    }
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)) {
        if (state.loading) item(key = "loading") { Loading() }
        state.error?.let { item(key = "error") { ErrorBox(it, retry) } }
        items(list, key = { it.uri }) { f -> LocalRow(f, list, audio) }
    }
}

@Composable
private fun LocalRow(f: MediaFile, all: List<MediaFile>, audio: Boolean) {
    val files = koinInject<FilesUseCase>()
    var menu by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().height(87.dp).clickable { files.playLocal(all, all.indexOf(f), audio) }.padding(start = 12.dp, top = 8.dp)) {
        Box(Modifier.size(124.dp, 70.dp).clip(RoundedCornerShape(6.dp)).background(if (audio) VRed else ViColors.pocDarkSurfaceVariant), contentAlignment = Alignment.Center) {
            if (audio) Icon(Icons.Filled.MusicNote, null, Modifier.size(32.dp), tint = ViColors.white)
            else AsyncImage(f.uri, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        }
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
