package org.app.vimate.detail.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.detail_cancel
import org.app.vimate.appres.resources.detail_create
import org.app.vimate.appres.resources.detail_new_playlist
import org.app.vimate.appres.resources.detail_playlist_entry
import org.app.vimate.appres.resources.detail_save_to_playlist
import org.app.vimate.appres.resources.detail_saved
import org.app.vimate.appres.resources.detail_saved_to
import org.app.vimate.detail.repository.DetailRepository
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/** POC SaveToPlaylistDialog: pick a playlist or create one, then save [v] into it. */
@Composable
fun SaveToPlaylistDialog(v: Video, onDismiss: () -> Unit) {
    val repo = koinInject<DetailRepository>()
    val toast = LocalToast.current
    val lists by repo.playlists.collectAsState()
    var name by remember { mutableStateOf("") }
    val saved = stringResource(Res.string.detail_saved)
    val savedTo = stringResource(Res.string.detail_saved_to, "%s")
    AlertDialog(onDismissRequest = onDismiss, title = { Text(stringResource(Res.string.detail_save_to_playlist)) },
        text = {
            Column {
                LazyColumn(Modifier.heightIn(max = 320.dp)) {
                    items(lists, key = { it.id }) { p ->
                        Text(stringResource(Res.string.detail_playlist_entry, p.name, p.videos.size.toString()),
                            Modifier.fillMaxWidth().clickable { repo.saveToPlaylist(p.id, v); toast(savedTo.replace("%s", p.name)); onDismiss() }.padding(vertical = 12.dp))
                    }
                }
                OutlinedTextField(name, { name = it }, label = { Text(stringResource(Res.string.detail_new_playlist)) }, singleLine = true)
            }
        },
        confirmButton = { TextButton(enabled = name.isNotBlank(), onClick = { repo.saveToPlaylist(repo.createPlaylist(name.trim()), v); toast(saved); onDismiss() }) { Text(stringResource(Res.string.detail_create)) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(Res.string.detail_cancel)) } })
}
