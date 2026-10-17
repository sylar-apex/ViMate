package org.app.vimate.sites.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.sites_detected_media
import org.app.vimate.appres.resources.sites_download
import org.app.vimate.appres.resources.sites_play
import org.app.vimate.sites.logic.MediaSniffer
import org.app.vimate.sites.model.Sniffed
import org.jetbrains.compose.resources.stringResource

/** "Detected media" sheet of network-sniffed files. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundSheet(found: List<Sniffed>, onPlay: (Sniffed) -> Unit, onDownload: (Sniffed) -> Unit, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Text(stringResource(Res.string.sites_detected_media), fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 16.dp))
        LazyColumn {
            items(found, key = { it.url }) { f ->
                ListItem(
                    headlineContent = { Text(f.title.ifEmpty { f.url.substringAfterLast('/').substringBefore('?') }, maxLines = 2) },
                    supportingContent = { Text(f.url, maxLines = 1, overflow = TextOverflow.Ellipsis, fontSize = 11.sp) },
                    leadingContent = { Icon(if (MediaSniffer.isAudio(f.mime, f.url)) Icons.Filled.MusicNote else Icons.Filled.Movie, null) },
                    trailingContent = {
                        Row {
                            IconButton(onClick = { onPlay(f) }) { Icon(Icons.Filled.PlayArrow, stringResource(Res.string.sites_play)) }
                            IconButton(onClick = { onDownload(f) }) { Icon(Icons.Filled.Download, stringResource(Res.string.sites_download), tint = VRed) }
                        }
                    })
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
