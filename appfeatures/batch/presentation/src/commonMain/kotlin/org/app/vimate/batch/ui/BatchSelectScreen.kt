package org.app.vimate.batch.ui

import org.app.vimate.batch.ui.component.WideBatchDialog
import org.app.vimate.appcore.adaptive.WindowClass
import org.app.vimate.appcore.adaptive.LocalWindowClass
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.component.Thumb
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.batch_added
import org.app.vimate.appres.resources.batch_done
import org.app.vimate.appres.resources.batch_select_all
import org.app.vimate.appres.resources.batch_selected
import org.app.vimate.batch.ui.component.BatchSheet
import org.app.vimate.batch.ui.component.Check
import org.app.vimate.batch.ui.component.PageHeader
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/** VidMate "Selected": every playlist item ticked, Select All + count + red DONE bar. */
@Composable
fun BatchSelectScreen(videos: List<Video>, viewModel: BatchSelectViewModel = koinViewModel()) {
    val nav = LocalAppNavigator.current
    val toast = LocalToast.current
    LaunchedEffect(Unit) { viewModel.setEvent(BatchSelectContract.Event.Init(videos)) }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { e ->
            if (e is BatchSelectContract.Effect.Added) { toast(getString(Res.string.batch_added, e.count)); nav.home(Tab.FILES) }
        }
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    BatchSelectContent(state, viewModel::setEvent)
}

@Composable
fun BatchSelectContent(state: BatchSelectContract.State, onEvent: (BatchSelectContract.Event) -> Unit) {
    val nav = LocalAppNavigator.current
    val videos = state.videos
    val picked = state.picked
    Column(Modifier.fillMaxSize().background(ViColors.surface).statusBarsPadding()) {
        PageHeader(stringResource(Res.string.batch_selected)) { nav.back() }
        LazyColumn(Modifier.weight(1f)) {
            items(videos, key = { it.id }) { v ->
                val on = v.id in picked
                Row(Modifier.fillMaxWidth().height(83.dp).clickable { onEvent(BatchSelectContract.Event.Toggle(v.id)) }, verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.padding(start = 19.dp).size(19.dp)) { Check(on) }
                    Thumb(v.thumbnail, Modifier.padding(start = 19.dp).size(119.dp, 66.dp), corner = 0, duration = v.duration)
                    Text(v.title, Modifier.padding(start = 16.dp, end = 32.dp), fontSize = 15.sp, lineHeight = 17.7.sp, color = ViColors.ink, maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
                Box(Modifier.padding(end = 34.dp).fillMaxWidth().height(0.7.dp).background(ViColors.rule))
            }
        }
        Row(Modifier.fillMaxWidth().height(48.dp).background(ViColors.surface), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.padding(start = 19.dp).clickable { onEvent(BatchSelectContract.Event.ToggleAll) }, verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(19.dp)) { Check(picked.size == videos.size) }
                Text(stringResource(Res.string.batch_select_all), Modifier.padding(start = 8.dp), fontSize = 15.sp, color = ViColors.muted)
            }
            Spacer(Modifier.weight(1f))
            Text("${picked.size}/${videos.size}", fontSize = 15.sp, color = ViColors.ink)
            Box(Modifier.padding(start = 10.dp, end = 13.dp).size(77.dp, 36.dp).background(if (picked.isEmpty()) ViColors.batchDoneOff else ViColors.batchDoneOn)
                .clickable(enabled = picked.isNotEmpty()) { onEvent(BatchSelectContract.Event.OpenSheet) }, contentAlignment = Alignment.Center) {
                Text(stringResource(Res.string.batch_done), color = ViColors.white, fontSize = 16.sp)
            }
        }
    }
    // Wide windows: centred dialog like a single-video download; phones keep the bottom sheet.
    if (state.sheet) { if (LocalWindowClass.current != WindowClass.Compact) WideBatchDialog(state, onEvent) else BatchSheet(state, onEvent) }
}
