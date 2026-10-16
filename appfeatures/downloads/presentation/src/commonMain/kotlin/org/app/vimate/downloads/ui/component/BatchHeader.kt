package org.app.vimate.downloads.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.back
import org.app.vimate.appres.resources.batch_edit
import org.app.vimate.appres.resources.batch_title
import org.app.vimate.appres.resources.cancel
import org.app.vimate.appres.resources.dl_video
import org.app.vimate.appres.resources.dl_videos
import org.app.vimate.appres.resources.ic_back
import org.app.vimate.appres.resources.ic_trash
import org.app.vimate.downloads.ui.constant.DownloadLayoutSpec
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.settings.AppChoices
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Back, "N Videos", and the trash (edit) button or Cancel while editing. */
@Composable
internal fun BatchTopBar(count: Int, editing: Boolean, onBack: () -> Unit, onEdit: () -> Unit, onCancelEdit: () -> Unit) {
    val ink = MaterialTheme.colorScheme.onSurface
    Row(Modifier.fillMaxWidth().height(DownloadLayoutSpec.batchTopBar), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.padding(start = 16.dp).size(32.dp).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
            Icon(painterResource(Res.drawable.ic_back), stringResource(Res.string.back), tint = ink, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.width(24.dp))
        Text(stringResource(Res.string.batch_title, count.toString()), color = ink, fontSize = 20.sp, modifier = Modifier.weight(1f))
        if (editing) {
            Text(stringResource(Res.string.cancel), color = ink, fontSize = 15.sp, modifier = Modifier.padding(end = 16.dp).clickable(onClick = onCancelEdit))
        } else {
            Box(Modifier.padding(end = 16.dp).size(32.dp).clickable(onClick = onEdit), contentAlignment = Alignment.Center) {
                Icon(painterResource(Res.drawable.ic_trash), stringResource(Res.string.batch_edit), tint = ink, modifier = Modifier.size(28.dp))
            }
        }
    }
}

/** Collapsed: "Video" with the preset badge. Expanded: every preset to choose from. */
@Composable
internal fun PresetSection(preset: String, expanded: Boolean, onExpand: () -> Unit, onPreset: (String) -> Unit) {
    val ink = MaterialTheme.colorScheme.onSurface
    if (!expanded) {
        Row(Modifier.padding(top = 12.dp).fillMaxWidth().height(24.dp).clickable(onClick = onExpand), verticalAlignment = Alignment.CenterVertically) {
            SectionMark(FileKind.VIDEO)
            Spacer(Modifier.width(12.dp))
            Text(stringResource(Res.string.dl_video), color = ink, fontSize = 16.sp, modifier = Modifier.weight(1f))
            Box(Modifier.size(DownloadLayoutSpec.badgeWidth, 24.dp).border(1.5.dp, ViColors.dot, RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                Text(preset.uppercase(), color = ViColors.dot, fontSize = 14.sp)
            }
        }
        return
    }
    Row(Modifier.padding(top = 12.dp).height(24.dp), verticalAlignment = Alignment.CenterVertically) {
        SectionMark(FileKind.VIDEO)
        Spacer(Modifier.width(12.dp))
        Text(stringResource(Res.string.dl_videos), color = ink, fontSize = 16.sp)
    }
    LazyVerticalGrid(
        GridCells.Adaptive(DownloadLayoutSpec.presetMinWidth + 12.dp),
        Modifier.padding(top = 8.dp).heightIn(max = 120.dp),
        userScrollEnabled = false,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(AppChoices.batchQualities, key = { it }) { option ->
            val on = option.equals(preset, ignoreCase = true)
            Box(
                Modifier.height(DownloadLayoutSpec.presetHeight).defaultMinSize(minWidth = DownloadLayoutSpec.presetMinWidth).clip(RoundedCornerShape(4.dp))
                    .background(if (on) MaterialTheme.colorScheme.surface else ViColors.gap)
                    .then(if (on) Modifier.border(1.5.dp, ViColors.dot, RoundedCornerShape(4.dp)) else Modifier)
                    .clickable { onPreset(option.lowercase()) },
                contentAlignment = Alignment.Center,
            ) {
                Text(option.uppercase(), color = if (on) ViColors.dot else ink, fontSize = 14.sp)
            }
        }
    }
}
