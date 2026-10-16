package org.app.vimate.downloads.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_nav_music
import org.app.vimate.appres.resources.ic_nav_video_on
import org.app.vimate.downloads.ui.constant.DownloadLayoutSpec
import org.app.vimate.sharedlogic.model.FileKind
import org.jetbrains.compose.resources.painterResource

/** The red music note or the red video square in front of a section title. */
@Composable
internal fun SectionMark(kind: FileKind) {
    if (kind == FileKind.MUSIC) {
        Box(Modifier.size(DownloadLayoutSpec.sectionIcon).background(ViColors.dot, CircleShape), contentAlignment = Alignment.Center) {
            Icon(painterResource(Res.drawable.ic_nav_music), null, tint = ViColors.white, modifier = Modifier.size(15.dp))
        }
    } else {
        Image(painterResource(Res.drawable.ic_nav_video_on), null, Modifier.size(DownloadLayoutSpec.sectionIcon))
    }
}
