package org.app.vimate.downloads.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.theme.mutedInk
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.cancel
import org.app.vimate.appres.resources.downloads_pause
import org.app.vimate.appres.resources.downloads_resume
import org.app.vimate.appres.resources.play
import org.app.vimate.sharedlogic.download.model.DownloadItem
import org.app.vimate.sharedlogic.logic.Formats
import org.jetbrains.compose.resources.stringResource

internal class DownloadRowActions(
    val onPause: (String) -> Unit,
    val onResume: (String) -> Unit,
    val onCancel: (String) -> Unit,
    val onPlay: (DownloadItem) -> Unit,
)

/** Title, state (with the error), progress, sizes, and the actions the state allows. */
@Composable
internal fun DownloadRow(item: DownloadItem, actions: DownloadRowActions) {
    Column(Modifier.fillMaxWidth().padding(16.dp)) {
        Text(item.title, style = MaterialTheme.typography.titleMedium)
        Text(item.state.label() + (item.error?.let { " · $it" } ?: ""), color = mutedInk)
        LinearProgressIndicator(progress = { item.progress }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp))
        Text(Formats.bytes(item.bytes) + if (item.totalBytes > 0) " / ${Formats.bytes(item.totalBytes)}" else "", color = mutedInk)
        Row {
            if (item.state.canPause) TextButton(onClick = { actions.onPause(item.id) }) { Text(stringResource(Res.string.downloads_pause)) }
            if (item.state.canResume) TextButton(onClick = { actions.onResume(item.id) }) { Text(stringResource(Res.string.downloads_resume)) }
            if (item.state.isActive || item.state.canResume) TextButton(onClick = { actions.onCancel(item.id) }) { Text(stringResource(Res.string.cancel)) }
            if (item.localUri != null) TextButton(onClick = { actions.onPlay(item) }) { Text(stringResource(Res.string.play)) }
        }
    }
}
