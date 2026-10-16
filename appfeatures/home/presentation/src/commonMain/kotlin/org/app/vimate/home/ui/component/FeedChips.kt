package org.app.vimate.home.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.home_chip_all
import org.app.vimate.appres.resources.home_shorts
import org.app.vimate.appres.resources.ic_brand_shorts
import org.app.vimate.home.ui.constant.HomeLayoutSpec
import org.app.vimate.sharedlogic.settings.FeedChip
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

/** The Shorts pill; a red dot marks it until it was opened once. */
@Composable
internal fun ShortsChip(seen: Boolean, onClick: () -> Unit) {
    val ink = MaterialTheme.colorScheme.onSurface
    Box(Modifier.padding(start = 16.dp, top = 1.7.dp)) {
        Row(
            Modifier.height(HomeLayoutSpec.chipHeight).clip(CircleShape).background(MaterialTheme.colorScheme.surface)
                .clickable(onClick = onClick).padding(start = 10.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(painterResource(Res.drawable.ic_brand_shorts), null, tint = ViColors.shortsRed, modifier = Modifier.size(HomeLayoutSpec.shortsIcon))
            Spacer(Modifier.width(3.dp))
            Text(stringResource(Res.string.home_shorts), color = ink, fontSize = HomeLayoutSpec.chipText, maxLines = 1)
        }
        if (!seen) Box(Modifier.align(Alignment.TopEnd).size(HomeLayoutSpec.dot).background(ViColors.dot, CircleShape))
    }
}

/** "All" and the genre chips, scrolling sideways. */
@Composable
internal fun FeedChips(chips: List<String>, selected: String, onSelect: (String) -> Unit, modifier: Modifier = Modifier) {
    val surface = MaterialTheme.colorScheme.surface
    val ink = MaterialTheme.colorScheme.onSurface
    LazyRow(
        modifier.padding(start = 3.dp, top = 2.4.dp),
        contentPadding = PaddingValues(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(HomeLayoutSpec.chipGap),
    ) {
        items(chips, key = { it }) { chip ->
            val on = chip == selected
            Box(
                Modifier.height(HomeLayoutSpec.chipHeight).defaultMinSize(minWidth = HomeLayoutSpec.chipMinWidth).clip(CircleShape)
                    .background(if (on) ViColors.chipOn else surface).clickable { onSelect(chip) }.padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (chip == FeedChip.ALL) stringResource(Res.string.home_chip_all) else chip,
                    color = if (on) ViColors.white else ink, fontSize = HomeLayoutSpec.chipText, maxLines = 1,
                )
            }
        }
    }
}
