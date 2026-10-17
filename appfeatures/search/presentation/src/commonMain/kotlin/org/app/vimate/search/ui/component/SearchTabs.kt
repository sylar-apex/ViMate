package org.app.vimate.search.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.search.constant.SearchTab
import org.jetbrains.compose.resources.stringResource

private fun SearchTab.label() = when (this) {
    SearchTab.All -> Res.string.search_tab_all
    SearchTab.Music -> Res.string.search_tab_music
    SearchTab.Playlist -> Res.string.search_tab_playlist
    SearchTab.Instagram -> Res.string.search_tab_instagram
    SearchTab.Facebook -> Res.string.search_tab_facebook
    SearchTab.Picture -> Res.string.search_tab_picture
}

/** 39dp tab strip over a 1dp hairline; selected tab bold black with a 3dp underline. */
@Composable
internal fun SearchTabs(selected: SearchTab, onTab: (SearchTab) -> Unit) {
    Box(Modifier.fillMaxWidth().height(39.dp)) {
        Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().height(1.dp).background(ViColors.hairline))
        LazyRow(Modifier.fillMaxHeight()) {
            items(SearchTab.entries, key = { it.name }) { t ->
                val sel = selected == t
                Box(Modifier.fillMaxHeight().clickable { onTab(t) }) {
                    Box(Modifier.fillMaxHeight().padding(horizontal = 17.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(t.label()), fontSize = if (sel) 13.6.sp else 13.5.sp, lineHeight = 19.sp, fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
                            color = if (sel) Color.Black else ViColors.tabGrey)
                    }
                    if (sel) Box(Modifier.matchParentSize().padding(start = 10.dp, end = 10.dp, bottom = 1.dp).wrapContentHeight(Alignment.Bottom).height(3.dp).background(Color.Black))
                }
            }
        }
    }
}
