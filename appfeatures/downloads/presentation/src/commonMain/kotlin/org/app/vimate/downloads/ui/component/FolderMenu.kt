package org.app.vimate.downloads.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.mutedInk
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.dl_download_folder
import org.app.vimate.appres.resources.dl_movies_folder
import org.app.vimate.appres.resources.dl_music_folder
import org.app.vimate.appres.resources.dl_no_folder
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.settings.DownloadFolder
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** "Movies folder" (or "Music folder") and "Download folder", each with its path. */
@Composable
internal fun FolderMenu(
    open: Boolean,
    kind: FileKind,
    selected: DownloadFolder,
    paths: Map<DownloadFolder, String>,
    onSelect: (DownloadFolder) -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenu(expanded = open, onDismissRequest = onDismiss, containerColor = MaterialTheme.colorScheme.surface) {
        FolderItem(DownloadFolder.MEDIA, if (kind == FileKind.MUSIC) Res.string.dl_music_folder else Res.string.dl_movies_folder, selected, paths, onSelect, onDismiss)
        FolderItem(DownloadFolder.DOWNLOAD, Res.string.dl_download_folder, selected, paths, onSelect, onDismiss)
    }
}

@Composable
private fun FolderItem(
    folder: DownloadFolder,
    label: StringResource,
    selected: DownloadFolder,
    paths: Map<DownloadFolder, String>,
    onSelect: (DownloadFolder) -> Unit,
    onDismiss: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            Column {
                Text(stringResource(label), fontSize = 14.sp, color = if (folder == selected) ViColors.red else MaterialTheme.colorScheme.onSurface)
                Text(paths[folder].orEmpty().ifEmpty { stringResource(Res.string.dl_no_folder) }, fontSize = 11.sp, color = mutedInk)
            }
        },
        onClick = {
            onSelect(folder)
            onDismiss()
        },
    )
}
