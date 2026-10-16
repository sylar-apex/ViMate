package org.app.vimate.downloads.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import org.app.vimate.appcore.theme.mutedInk
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.delete_count
import org.app.vimate.appres.resources.download
import org.app.vimate.appres.resources.ic_download
import org.app.vimate.appres.resources.select_all
import org.app.vimate.downloads.ui.constant.DownloadLayoutSpec
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** Download (normal mode), or Select All and Delete (n) while editing. */
@Composable
internal fun BatchFooter(editing: Boolean, markedCount: Int, chosenCount: Int, onSelectAll: () -> Unit, onDelete: () -> Unit, onDownload: () -> Unit) {
    val shape = RoundedCornerShape(4.dp)
    Row(Modifier.fillMaxWidth().height(DownloadLayoutSpec.footerHeight).navigationBarsPadding().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
        if (editing) {
            Box(Modifier.weight(1f).fillMaxHeight().clickable(onClick = onSelectAll), contentAlignment = Alignment.Center) {
                Text(stringResource(Res.string.select_all), color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp)
            }
            Box(
                Modifier.weight(1f).fillMaxHeight().clip(shape).background(if (markedCount > 0) ViColors.dot else ViColors.gap)
                    .clickable(enabled = markedCount > 0, onClick = onDelete),
                contentAlignment = Alignment.Center,
            ) {
                Text(stringResource(Res.string.delete_count, markedCount.toString()), color = if (markedCount > 0) ViColors.white else mutedInk, fontSize = 15.sp)
            }
        } else {
            Row(
                Modifier.fillMaxSize().clip(shape).background(if (chosenCount > 0) ViColors.dot else ViColors.gap).clickable(enabled = chosenCount > 0, onClick = onDownload),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(20.dp).background(ViColors.white, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(painterResource(Res.drawable.ic_download), null, tint = ViColors.dot, modifier = Modifier.size(14.dp))
                }
                Spacer(Modifier.width(8.dp))
                Text(stringResource(Res.string.download), color = ViColors.white, fontSize = 16.sp)
            }
        }
    }
}
