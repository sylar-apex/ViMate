package org.app.vimate.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.app.vimate.music.ui.component.CollectMusicEffects
import org.app.vimate.music.ui.component.text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.app.vimate.appcore.adaptive.LocalWindowClass
import org.app.vimate.appcore.adaptive.WindowClass
import org.app.vimate.appcore.component.ErrorBox
import org.app.vimate.music.ui.wide.MusicWide
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.navigation.Tab
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_download
import org.app.vimate.appres.resources.ic_search
import org.app.vimate.appres.resources.music_downloads
import org.app.vimate.appres.resources.music_search_hint
import org.app.vimate.appres.resources.music_title
import org.app.vimate.music.ui.component.MusicTabs
import org.app.vimate.music.ui.component.ShelfView
import org.jetbrains.compose.resources.stringResource

// Geometry measured from VidMate 6.0302 Music tab (3px = 1dp).

/** The Music tab (POC ui/MusicScreen.kt MusicScreen). */
@Composable
fun MusicScreen(viewModel: MusicViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CollectMusicEffects(viewModel.effect)
    MusicContent(state, viewModel::setEvent)
}

@Composable
fun MusicContent(state: MusicContract.State, onEvent: (MusicContract.Event) -> Unit) {
    if (LocalWindowClass.current != WindowClass.Compact) return MusicWide(state, onEvent)
    val nav = LocalAppNavigator.current
    Column(Modifier.fillMaxSize().background(ViColors.page).statusBarsPadding()) {
        // Header 53dp: 24sp bold title at x=12, 32dp downloads icon at x=316.
        Row(Modifier.fillMaxWidth().height(53.dp).padding(start = 12.dp, end = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(Res.string.music_title), fontSize = 21.5.sp, lineHeight = 29.sp, fontWeight = FontWeight.Bold, color = ViColors.ink, modifier = Modifier.weight(1f).padding(top = 2.dp))
            Box(Modifier.size(32.dp).clickable { nav.home(Tab.FILES) }, contentAlignment = Alignment.Center) {
                Ic(Res.drawable.ic_download, Modifier.size(26.dp), ViColors.ink, stringResource(Res.string.music_downloads))
            }
        }
        // Search pill 336x42dp at x=12, content centred.
        Row(Modifier.padding(horizontal = 12.dp).fillMaxWidth().height(42.dp).clip(CircleShape).background(ViColors.white).clickable { nav.go(Routes.MusicSearch) },
            horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Ic(Res.drawable.ic_search, Modifier.size(22.dp), ViColors.ink)
            Text(stringResource(Res.string.music_search_hint), Modifier.padding(start = 6.dp), color = ViColors.hint, fontSize = 15.3.sp, lineHeight = 21.sp)
        }
        Spacer(Modifier.height(10.dp))
        MusicTabs(state.tabLabels, state.tab) { onEvent(MusicContract.Event.SelectTab(it)) }
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
            if (state.loading) item { Loading() }
            state.error?.let { e -> item { ErrorBox(e.text()) { onEvent(MusicContract.Event.Retry) } } }
            itemsIndexed(state.shelves, key = { i, s -> "$i${s.title}" }) { _, shelf ->
                ShelfView(shelf, { onEvent(MusicContract.Event.PlayAll(it)) }, { l, i -> onEvent(MusicContract.Event.Tap(l, i)) }, { onEvent(MusicContract.Event.PlaySingle(it)) })
                Spacer(Modifier.height(6.dp))
            }
        }
    }
}
