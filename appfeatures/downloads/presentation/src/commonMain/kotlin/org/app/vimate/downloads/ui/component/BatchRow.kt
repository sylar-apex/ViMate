package org.app.vimate.downloads.ui.component

import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.CheckBox
import org.app.vimate.appcore.component.DurationBadge
import org.app.vimate.appcore.component.MediaThumb
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appcore.theme.ViShapes
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_chevron
import org.app.vimate.downloads.domain.model.BatchItem
import org.app.vimate.downloads.ui.constant.DownloadLayoutSpec
import org.app.vimate.sharedlogic.logic.Formats
import org.jetbrains.compose.resources.painterResource

/** Thumbnail, title, the quality pill with every rendition, and the check box. */
@Composable
internal fun BatchRow(item: BatchItem, quality: String, checked: Boolean, onToggle: () -> Unit, onQuality: (String) -> Unit) {
    var menu by remember { mutableStateOf(false) }
    val media = item.media
    Row(Modifier.fillMaxWidth().height(DownloadLayoutSpec.batchRowHeight).clickable(onClick = onToggle).padding(horizontal = 16.dp, vertical = 8.dp)) {
        Box(Modifier.size(DownloadLayoutSpec.batchThumbWidth, DownloadLayoutSpec.batchThumbHeight).clip(ViShapes.thumb)) {
            MediaThumb(media, Modifier.fillMaxSize())
            DurationBadge(media, Modifier.align(Alignment.BottomEnd).padding(end = 4.dp, bottom = 3.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(media.title, color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp, lineHeight = 17.5.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.weight(1f))
            Box {
                Row(
                    Modifier.height(19.dp).border(1.dp, ViColors.pillBlue, RoundedCornerShape(2.dp)).clickable { menu = true }.padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(quality.uppercase(), color = ViColors.pillBlue, fontSize = 12.sp)
                    Icon(painterResource(Res.drawable.ic_chevron), null, tint = ViColors.pillBlue, modifier = Modifier.size(14.dp))
                }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }, containerColor = MaterialTheme.colorScheme.surface) {
                    LazyColumn(Modifier.width(MENU_WIDTH).heightIn(max = MENU_MAX_HEIGHT)) {
                        items(item.renditions, key = { it.quality }) { rendition ->
                            DropdownMenuItem(
                                text = { Text("${rendition.quality.uppercase()}  ·  ${Formats.sheetSize(rendition.bytes)}", fontSize = 14.sp) },
                                onClick = { onQuality(rendition.quality); menu = false },
                            )
                        }
                    }
                }
            }
        }
        Spacer(Modifier.width(16.dp))
        CheckBox(checked)
    }
}

private val MENU_WIDTH = 180.dp
private val MENU_MAX_HEIGHT = 240.dp
