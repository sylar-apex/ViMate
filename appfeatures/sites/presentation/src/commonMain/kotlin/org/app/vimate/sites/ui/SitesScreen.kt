package org.app.vimate.sites.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_back
import org.app.vimate.appres.resources.sites_back
import org.app.vimate.appres.resources.sites_group_entertainment
import org.app.vimate.appres.resources.sites_group_favorite
import org.app.vimate.appres.resources.sites_title
import org.app.vimate.sites.ui.component.SiteLogo
import org.app.vimate.sites.ui.constant.SiteGroup
import org.app.vimate.sites.ui.constant.SiteTile
import org.app.vimate.sites.ui.constant.siteTiles
import org.jetbrains.compose.resources.stringResource

/** Measured: white page, 50dp header (back at x=16, 17sp title at x=64, 1dp rule); bold section titles at x=16;
 *  4 columns on a 90dp stride from x=19, 48dp logos, 13sp grey labels, 82dp cells + 16dp row gap. */
@Composable
fun SitesScreen() {
    val nav = LocalAppNavigator.current
    SitesContent(onBack = { nav.back() }, onOpen = { nav.go(Routes.Browser(it.url)) })
}

@Composable
fun SitesContent(onBack: () -> Unit, onOpen: (SiteTile) -> Unit) {
    Column(Modifier.fillMaxSize().background(ViColors.pocLightBackground).statusBarsPadding().background(ViColors.white)) {
        Row(Modifier.fillMaxWidth().height(50.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.padding(start = 16.dp).size(40.dp).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_back, Modifier.size(30.dp), ViColors.ink, stringResource(Res.string.sites_back))
            }
            Text(stringResource(Res.string.sites_title), Modifier.padding(start = 8.dp), fontSize = 14.3.sp, lineHeight = 20.sp, color = ViColors.ink)
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(ViColors.sitesRule))
        LazyVerticalGrid(GridCells.FixedSize(90.dp), Modifier.fillMaxSize(), contentPadding = PaddingValues(start = 19.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Start) {
            SiteGroup.entries.forEach { g ->
                item(key = "h_$g", span = { GridItemSpan(maxLineSpan) }) {
                    val label = stringResource(if (g == SiteGroup.Favorite) Res.string.sites_group_favorite else Res.string.sites_group_entertainment)
                    Text(label, Modifier.offset(x = (-3).dp).padding(top = if (g == SiteGroup.Favorite) 18.dp else 21.dp, bottom = 18.dp),
                        fontWeight = FontWeight.Bold, fontSize = 14.5.sp, lineHeight = 20.sp, color = ViColors.ink)
                }
                items(siteTiles.filter { it.group == g }, key = { it.url }) { t ->
                    Column(Modifier.padding(bottom = 16.dp).width(90.dp).height(82.dp).clickable { onOpen(t) }, horizontalAlignment = Alignment.Start) {
                        Column(Modifier.width(52.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.padding(top = 2.dp)) { SiteLogo(t, 48.dp) }
                            Text(t.name, Modifier.padding(top = 4.dp).wrapContentWidth(unbounded = true).widthIn(max = 58.dp), fontSize = 11.sp, lineHeight = 13.sp,
                                color = ViColors.metaGrey, maxLines = 2, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
