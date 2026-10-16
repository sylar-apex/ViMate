package org.app.vimate.downloads.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.mutedInk
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.dl_change
import org.app.vimate.appres.resources.dl_free
import org.app.vimate.appres.resources.dl_path
import org.app.vimate.appres.resources.ic_folder
import org.app.vimate.downloads.ui.constant.DownloadLayoutSpec
import org.app.vimate.sharedlogic.logic.Formats
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.model.StorageInfo
import org.app.vimate.sharedlogic.settings.DownloadFolder
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Save path with the free-space bar, and "Change" opening the folder menu. */
@Composable
internal fun PathRow(
    kind: FileKind,
    path: String,
    storage: StorageInfo,
    folder: DownloadFolder,
    folderPaths: Map<DownloadFolder, String>,
    onFolder: (DownloadFolder) -> Unit,
    inset: Dp,
) {
    var menu by remember { mutableStateOf(false) }
    val ink = MaterialTheme.colorScheme.onSurface
    Row(Modifier.padding(horizontal = inset).fillMaxWidth().height(DownloadLayoutSpec.pathRowHeight), verticalAlignment = Alignment.CenterVertically) {
        Image(painterResource(Res.drawable.ic_folder), null, Modifier.size(DownloadLayoutSpec.folderIcon))
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(stringResource(Res.string.dl_path, path), color = ink, fontSize = DownloadLayoutSpec.pathText, maxLines = 1, overflow = TextOverflow.MiddleEllipsis)
            if (storage.known) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        stringResource(Res.string.dl_free, Formats.volume(storage.freeBytes), Formats.volume(storage.totalBytes)),
                        color = mutedInk, fontSize = DownloadLayoutSpec.freeText,
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.size(DownloadLayoutSpec.storageBar, 2.dp).background(ViColors.gap)) {
                        Box(Modifier.fillMaxHeight().fillMaxWidth(storage.usedBytes.toFloat() / storage.totalBytes).background(ViColors.storageUsed))
                    }
                }
            }
        }
        Spacer(Modifier.width(12.dp))
        Box {
            Text(stringResource(Res.string.dl_change), color = ViColors.blue, fontSize = 13.sp, modifier = Modifier.clickable { menu = true })
            FolderMenu(menu, kind, folder, folderPaths, onFolder) { menu = false }
        }
    }
}
