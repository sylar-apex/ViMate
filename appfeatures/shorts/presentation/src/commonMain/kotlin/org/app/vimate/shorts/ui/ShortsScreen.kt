package org.app.vimate.shorts.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.kmediaplayer.KMediaPlayer
import org.app.kmediaplayer.engine.RepeatMode
import org.app.vimate.appcore.component.Ic
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.component.rememberShareText
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_back
import org.app.vimate.appres.resources.shorts_back
import org.app.vimate.appres.resources.shorts_error
import org.app.vimate.sharedlogic.library.Channel
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.shorts.presentation.ShortsContract.Effect
import org.app.vimate.shorts.presentation.ShortsContract.Event
import org.app.vimate.shorts.presentation.ShortsContract.State
import org.app.vimate.shorts.presentation.ShortsViewModel
import org.app.vimate.shorts.ui.component.ShortPage
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/** Vertical pager of YouTube Shorts, looping playback of the page on screen (POC ShortsScreen). */
@Composable
fun ShortsScreen(start: Video?) {
    val nav = LocalAppNavigator.current
    val toast = LocalToast.current
    val share = rememberShareText()
    val vm = koinViewModel<ShortsViewModel>()
    val state by vm.uiState.collectAsStateWithLifecycle()
    val player = KMediaPlayer.engine
    LaunchedEffect(Unit) { vm.onEvent(Event.Start(start)) }
    LaunchedEffect(vm) {
        vm.effects.collect { e ->
            when (e) {
                is Effect.Error -> toast(e.message ?: getString(Res.string.shorts_error))
                is Effect.Play -> { player.setStream(e.url); player.repeatMode = RepeatMode.One; player.prepare(); player.play() }
            }
        }
    }
    DisposableEffect(Unit) { onDispose { player.pause(); player.repeatMode = RepeatMode.Off } }
    ShortsContent(
        state = state,
        onPage = { vm.onEvent(Event.PageShown(it)) },
        onTogglePlay = { if (player.isPlaying) player.pause() else player.play() },
        onFollow = { vm.onEvent(Event.ToggleFollow(it)) },
        onShare = { share("https://youtube.com/shorts/${it.id}") },
        onDownload = { nav.download(it) },
        onBack = { nav.back() },
    )
}

@Composable
fun ShortsContent(
    state: State, onPage: (Int) -> Unit, onTogglePlay: () -> Unit, onFollow: (Channel) -> Unit,
    onShare: (Video) -> Unit, onDownload: (Video) -> Unit, onBack: () -> Unit,
) {
    val shorts = state.shorts
    val pager = rememberPagerState { shorts.size }
    LaunchedEffect(pager.currentPage, shorts.size) { if (shorts.isNotEmpty()) onPage(pager.currentPage) }
    Box(Modifier.fillMaxSize().background(ViColors.black)) {
        VerticalPager(pager, Modifier.fillMaxSize(), key = { shorts[it].id }) { page ->
            val v = shorts[page]
            ShortPage(v, state.info[v.id], page == pager.currentPage, state.subscribedIds, onTogglePlay, onFollow, { onShare(v) }, { onDownload(v) })
        }
        Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().height(2.dp).background(ViColors.shortsProgress))
        Box(Modifier.padding(start = 15.dp, top = 40.dp).size(30.dp).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
            Ic(Res.drawable.ic_back, Modifier.size(30.dp), ViColors.white, stringResource(Res.string.shorts_back))
        }
        if (shorts.isEmpty()) CircularProgressIndicator(Modifier.align(Alignment.Center), color = ViColors.white)
    }
}
