package org.app.vimate.downloads.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.vimate.appcore.component.TopBar
import org.app.vimate.appcore.theme.mutedInk
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.downloads_empty
import org.app.vimate.appres.resources.downloads_title
import org.app.vimate.downloads.ui.component.DownloadRow
import org.app.vimate.downloads.ui.component.DownloadRowActions
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DownloadsScreen(onBack: () -> Unit, viewModel: DownloadsViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DownloadsContent(state, onBack, viewModel::setEvent)
}

@Composable
fun DownloadsContent(state: DownloadsContract.State, onBack: () -> Unit = {}, onEvent: (DownloadsContract.Event) -> Unit = {}) {
    val actions = remember(onEvent) {
        DownloadRowActions(
            onPause = { onEvent(DownloadsContract.Event.Pause(it)) },
            onResume = { onEvent(DownloadsContract.Event.Resume(it)) },
            onCancel = { onEvent(DownloadsContract.Event.Cancel(it)) },
            onPlay = { onEvent(DownloadsContract.Event.Play(it)) },
        )
    }
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopBar(stringResource(Res.string.downloads_title), onBack)
        if (state.items.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(stringResource(Res.string.downloads_empty), color = mutedInk) }
        } else {
            LazyColumn {
                items(state.items, key = { it.id }) { DownloadRow(it, actions) }
            }
        }
    }
}
