package org.app.vimate.home.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.home_downloads
import org.app.vimate.appres.resources.home_search_hint
import org.app.vimate.appres.resources.ic_download
import org.app.vimate.appres.resources.ic_search
import org.jetbrains.compose.resources.stringResource

/** Row y=40..90dp (measured 2026-10-16): search pill 309x36dp at x=12, downloads 32dp at x=322. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchBarRow(activeDownloads: Int) {
    val nav = LocalAppNavigator.current
    Row(Modifier.fillMaxWidth().height(50.dp), verticalAlignment = Alignment.CenterVertically) {
        Row(Modifier.padding(start = 12.dp).width(309.dp).height(36.dp).clip(CircleShape).background(Color.White)
            .clickable { nav.go(Routes.Search()) }.padding(start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Ic(Res.drawable.ic_search, Modifier.size(24.dp), ViColors.searchIcon)
            Text(stringResource(Res.string.home_search_hint), Modifier.padding(start = 6.dp).graphicsLayer(scaleX = 0.987f, transformOrigin = TransformOrigin(0f, .5f)),
                color = ViColors.hint, fontSize = 15.sp, lineHeight = 21.sp, maxLines = 1)
        }
        Box(Modifier.padding(start = 0.dp).offset(x = (-1).dp).size(35.dp, 32.dp).clickable { nav.home(Tab.FILES) }, contentAlignment = Alignment.Center) {
            BadgedBox(badge = { if (activeDownloads > 0) Badge(containerColor = VRed) { Text("$activeDownloads") } }) {
                Ic(Res.drawable.ic_download, Modifier.size(26.dp), ViColors.ink, stringResource(Res.string.home_downloads))
            }
        }
    }
}
