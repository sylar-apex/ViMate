package org.app.vimate.downloads.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.downloads_container
import org.app.vimate.appres.resources.downloads_not_available
import org.app.vimate.appres.resources.ic_nav_music_on
import org.app.vimate.appres.resources.ic_yt_play
import org.app.vimate.sharedlogic.download.option.DownloadOption
import org.app.vimate.sharedlogic.download.vid.fmtSize
import org.jetbrains.compose.resources.stringResource

/**
 * POC FormatSection, as grid items: header icon 24 at x=12, title at x=44; rows 42 tall in two columns;
 * radio at x=10, label at x=35, size at x=37 under it.
 */
internal fun LazyGridScope.formatSection(music: Boolean, title: String, options: List<DownloadOption>, selected: DownloadOption?, onSelect: (DownloadOption) -> Unit) {
    item(key = "h$title", span = { GridItemSpan(maxLineSpan) }) {
        Row(Modifier.fillMaxWidth().height(51.dp).padding(start = 12.dp, top = 20.dp), verticalAlignment = Alignment.Top) {
            Box(Modifier.size(width = if (music) 20.dp else 18.dp, height = if (music) 20.dp else 16.dp).offset(y = 2.dp).clip(if (music) CircleShape else RoundedCornerShape(4.dp)).background(VRed), contentAlignment = Alignment.Center) {
                Ic(if (music) Res.drawable.ic_nav_music_on else Res.drawable.ic_yt_play, Modifier.size(12.dp), ViColors.white)
            }
            Text(title, Modifier.padding(start = 13.dp).offset(y = (-3.5).dp), fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 21.7.sp, color = ViColors.ink)
        }
    }
    if (options.isEmpty()) item(key = "e$title", span = { GridItemSpan(maxLineSpan) }) {
        Text(stringResource(Res.string.downloads_not_available), color = ViColors.metaGrey, modifier = Modifier.padding(start = 22.dp, bottom = 8.dp))
    }
    items(options, key = { "$title${it.label}${it.container}${it.sizeBytes}" }) { o ->
        val sel = o == selected
        val grey = ViColors.metaGrey
        Box(Modifier.padding(start = 10.dp).height(42.dp).fillMaxWidth().clickable { onSelect(o) }) {
            Box(Modifier.offset(y = 1.7.dp).size(23.dp, 24.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.size(19.dp).clip(CircleShape).border(1.5.dp, if (sel) VRed else ViColors.batchRadioOff, CircleShape), contentAlignment = Alignment.Center) {
                    if (sel) Box(Modifier.size(12.dp).clip(CircleShape).background(VRed))
                }
            }
            Row(Modifier.padding(start = 25.dp, top = 3.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(o.label, fontWeight = FontWeight.Bold, fontSize = 12.9.sp, lineHeight = 17.7.sp, color = if (sel) VRed else ViColors.ink)
                Text(stringResource(Res.string.downloads_container, o.container), Modifier.padding(start = 2.dp), fontSize = 11.6.sp, lineHeight = 14.7.sp, color = if (sel) VRed else grey)
            }
            Text(if (o.sizeBytes > 0) fmtSize(o.sizeBytes).replace(" ", "").replace("KB", "kB") else "", Modifier.padding(start = 27.dp, top = 24.dp),
                fontSize = 11.6.sp, lineHeight = 14.7.sp, color = if (sel) VRed else grey)
        }
    }
}
