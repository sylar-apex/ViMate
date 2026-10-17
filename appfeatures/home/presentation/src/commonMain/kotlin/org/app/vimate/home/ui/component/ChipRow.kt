package org.app.vimate.home.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.jetbrains.compose.resources.stringResource

/** Chip labels in [org.app.vimate.home.constant.HOME_CHIP_TOPICS] order. */
internal val chipLabels = listOf(
    Res.string.home_chip_all, Res.string.home_chip_music, Res.string.home_chip_jukebox, Res.string.home_chip_playlists,
    Res.string.home_chip_1990s, Res.string.home_chip_movies, Res.string.home_chip_comedy, Res.string.home_chip_news,
    Res.string.home_chip_gaming, Res.string.home_chip_bhojpuri, Res.string.home_chip_punjabi,
)

/** Row y=90..128dp: Shorts chip (87x32dp at x=16, red dot) then scrollable 32dp pills, 12dp apart. */
@Composable
internal fun ChipRow(selected: Int, onChip: (Int) -> Unit) {
    val nav = LocalAppNavigator.current
    Row(Modifier.fillMaxWidth().height(42.dp).padding(top = 2.dp), verticalAlignment = Alignment.Top) {
        Box(Modifier.padding(start = 16.dp).width(87.dp).height(32.dp)) {
            Row(Modifier.fillMaxSize().clip(CircleShape).background(Color.White).clickable { nav.go(Routes.Shorts()) }.padding(start = 11.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Ic(Res.drawable.ic_shorts, Modifier.size(19.dp), ViColors.shortsChipRed)
                Text(stringResource(Res.string.home_shorts), Modifier.padding(start = 7.dp).graphicsLayer(scaleX = 0.97f, transformOrigin = TransformOrigin(0f, .5f)),
                    color = ViColors.ink, fontSize = 13.sp, lineHeight = 20.sp, maxLines = 1)
            }
        }
        LazyRow(Modifier.padding(start = 3.dp), contentPadding = PaddingValues(start = 10.dp, end = 12.dp), horizontalArrangement = Arrangement.spacedBy(11.7.dp)) {
            itemsIndexed(chipLabels, key = { i, _ -> i }) { i, label ->
                val sel = i == selected
                Box(Modifier.height(32.dp).clip(CircleShape).background(if (sel) ViColors.chipOn else Color.White).clickable { onChip(i) }
                    .padding(horizontal = 10.dp), contentAlignment = Alignment.Center) {
                    Text(stringResource(label), color = if (sel) Color.White else ViColors.ink, fontWeight = if (sel) FontWeight.Medium else FontWeight.Normal,
                        fontSize = 13.4.sp, modifier = Modifier.offset(y = 1.dp), lineHeight = 20.sp, maxLines = 1)
                }
            }
        }
    }
}
