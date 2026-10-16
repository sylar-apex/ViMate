package org.app.vimate.downloads.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.vimate.appcore.theme.ViAlpha
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.mutedInk
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.batch_collapse
import org.app.vimate.appres.resources.batch_download_in
import org.app.vimate.appres.resources.batch_expand
import org.app.vimate.appres.resources.dl_change
import org.app.vimate.appres.resources.dl_path
import org.app.vimate.appres.resources.ic_chevron
import org.app.vimate.downloads.ui.component.BatchFooter
import org.app.vimate.downloads.ui.component.BatchRow
import org.app.vimate.downloads.ui.component.BatchTopBar
import org.app.vimate.downloads.ui.component.FolderMenu
import org.app.vimate.downloads.ui.component.PresetSection
import org.app.vimate.downloads.ui.model.BatchUiModel
import org.app.vimate.sharedlogic.model.FileKind
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BatchScreen(onBack: () -> Unit, viewModel: BatchViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            when (effect) {
                BatchContract.Effect.Close -> onBack()
            }
        }
    }
    BatchContent(state, onBack, viewModel::setEvent)
}

@Composable
fun BatchContent(state: BatchContract.State, onBack: () -> Unit = {}, onEvent: (BatchContract.Event) -> Unit = {}) {
    val model = rememberSaveable(saver = BatchUiModel.Saver) { BatchUiModel(state.defaultPreset) }
    LaunchedEffect(state.defaultPreset) { if (model.preset.value.isEmpty()) model.preset.value = state.defaultPreset }
    val ink = MaterialTheme.colorScheme.onSurface
    val items = state.items
    val editing by model.editing
    var expanded by model.expanded
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        BatchTopBar(items.size, editing, onBack, onEdit = { model.editing.value = true }, onCancelEdit = model::stopEditing)
        Box(Modifier.fillMaxWidth().height(5.dp).background(Brush.verticalGradient(listOf(ViColors.black.copy(alpha = ViAlpha.TOP_SHADE), ViColors.transparent))))
        Column(Modifier.padding(horizontal = 16.dp)) {
            Text(stringResource(Res.string.batch_download_in), color = ink, fontSize = 16.sp, modifier = Modifier.padding(top = 11.dp))
            PresetSection(model.preset.value, expanded, onExpand = { expanded = true }, onPreset = model::choosePreset)
            BatchPathRow(state, onEvent)
            Box(Modifier.padding(top = 12.dp).fillMaxWidth().height(1.dp).background(ViColors.gap))
        }
        Box(Modifier.padding(top = 9.dp).fillMaxWidth().height(20.dp).clickable { expanded = !expanded }, contentAlignment = Alignment.Center) {
            Icon(
                painterResource(Res.drawable.ic_chevron), stringResource(if (expanded) Res.string.batch_collapse else Res.string.batch_expand),
                tint = mutedInk, modifier = Modifier.size(22.dp).rotate(if (expanded) 180f else 0f),
            )
        }
        Box(Modifier.padding(top = 8.dp).fillMaxWidth().height(8.dp).background(ViColors.gap))
        LazyColumn(Modifier.weight(1f)) {
            items(items, key = { it.media.id }) { item ->
                BatchRow(
                    item = item,
                    quality = model.qualityOf(item),
                    checked = model.isChecked(item.media.id),
                    onToggle = { model.toggle(item.media.id) },
                    onQuality = { model.override(item.media.id, it) },
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(5.dp).background(Brush.verticalGradient(listOf(ViColors.transparent, ViColors.black.copy(alpha = ViAlpha.BOTTOM_SHADE)))))
        BatchFooter(
            editing = editing,
            markedCount = model.marked.value.size,
            chosenCount = items.count { it.media.id !in model.unchecked.value },
            onSelectAll = { model.marked.value = items.map { it.media.id }.toSet() },
            onDelete = {
                onEvent(BatchContract.Event.Remove(model.marked.value))
                model.stopEditing()
            },
            onDownload = { onEvent(BatchContract.Event.Download(model.choices(items))) },
        )
    }
}

@Composable
private fun BatchPathRow(state: BatchContract.State, onEvent: (BatchContract.Event) -> Unit) {
    var menu by remember { mutableStateOf(false) }
    Row(Modifier.padding(top = 16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(Res.string.dl_path, state.path), color = mutedInk, fontSize = 12.sp, maxLines = 1,
            overflow = TextOverflow.MiddleEllipsis, modifier = Modifier.weight(1f),
        )
        Spacer(Modifier.width(16.dp))
        Box {
            Text(stringResource(Res.string.dl_change), color = ViColors.blue, fontSize = 12.sp, modifier = Modifier.clickable { menu = true })
            FolderMenu(menu, FileKind.VIDEO, state.folder, state.folderPaths, { onEvent(BatchContract.Event.SetFolder(it)) }) { menu = false }
        }
    }
}
