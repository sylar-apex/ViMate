package org.app.vimate.music.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Thumb
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_back
import org.app.vimate.appres.resources.music_back
import org.app.vimate.appres.resources.music_download
import org.app.vimate.appres.resources.music_play
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource

/** POC HomeScreen SongRow (private copy until appcore has one). */
@Composable
internal fun SongRow(v: Video, onPlay: () -> Unit, onDownload: (() -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().clickable(onClick = onPlay).padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Thumb(v.thumbnail, Modifier.size(56.dp), corner = 6)
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(v.title, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(listOf(v.channel, v.views).filter { it.isNotEmpty() }.joinToString(" • "), fontSize = 13.sp, maxLines = 1,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = .6f))
        }
        if (onDownload != null) IconButton(onClick = onDownload) { Icon(Icons.Outlined.FileDownload, stringResource(Res.string.music_download)) }
        Box(Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.PlayArrow, stringResource(Res.string.music_play))
        }
    }
}

/** POC MeScreen PageHeader: 50dp white, back chevron at x=16 (40dp), title at x=64, 1dp rule. */
@Composable
internal fun PageHeader(title: String, onBack: () -> Unit) {
    Row(Modifier.fillMaxWidth().height(50.dp).background(ViColors.white), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.padding(start = 16.dp).size(40.dp).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
            Ic(Res.drawable.ic_back, Modifier.size(30.dp), ViColors.ink, stringResource(Res.string.music_back))
        }
        Text(title, Modifier.padding(start = 8.dp), fontSize = 19.sp, lineHeight = 27.sp, fontWeight = FontWeight.Bold, color = ViColors.ink)
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(ViColors.rule))
}
