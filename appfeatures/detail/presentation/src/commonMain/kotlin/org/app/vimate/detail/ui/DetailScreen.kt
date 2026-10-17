package org.app.vimate.detail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.launch
import org.app.vimate.appcore.component.Loading
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.component.Thumb
import org.app.vimate.appcore.component.VideoCard
import org.app.vimate.appcore.component.rememberShareText
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.detail_download
import org.app.vimate.appres.resources.detail_play_audio
import org.app.vimate.appres.resources.detail_playback_error
import org.app.vimate.appres.resources.detail_popup
import org.app.vimate.appres.resources.detail_save
import org.app.vimate.appres.resources.detail_share
import org.app.vimate.appres.resources.ic_d_audio
import org.app.vimate.appres.resources.ic_d_download
import org.app.vimate.appres.resources.ic_d_popup
import org.app.vimate.appres.resources.ic_d_save
import org.app.vimate.appres.resources.ic_d_share
import org.app.vimate.detail.contract.DetailContract
import org.app.vimate.detail.model.Quality
import org.app.vimate.detail.repository.DetailRepository
import org.app.vimate.detail.ui.component.CommentsCard
import org.app.vimate.detail.ui.component.CommentsPanel
import org.app.vimate.detail.ui.component.DetailAction
import org.app.vimate.detail.ui.component.DetailActions
import org.app.vimate.detail.ui.component.DetailTitle
import org.app.vimate.detail.ui.component.PopupPermissionDialog
import org.app.vimate.detail.ui.component.SaveToPlaylistDialog
import org.app.vimate.detail.ui.constant.DetailLayoutSpec
import org.app.vimate.detail.ui.player.EmbedPlayer
import org.app.vimate.detail.ui.player.InlinePlayer
import org.app.vimate.detail.ui.player.rememberDetailPlayer
import org.app.vimate.detail.ui.player.rememberPopupController
import org.app.vimate.detail.ui.player.rememberWebFallback
import org.app.vimate.detail.usecase.BuildQualitiesUseCase
import org.app.vimate.detail.viewmodel.DetailViewModel
import org.app.vimate.sharedlogic.playback.PlaybackLauncher
import org.app.vimate.sharedlogic.yt.Video
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/** VidMate video page (POC ui/DetailScreen.kt): inline player, title, actions, comments, related videos. */
@Composable
fun DetailScreen(start: Video) {
    val nav = LocalAppNavigator.current
    val toast = LocalToast.current
    val share = rememberShareText()
    val vm = koinViewModel<DetailViewModel>()
    val repo = koinInject<DetailRepository>()
    val buildQualities = koinInject<BuildQualitiesUseCase>()
    val launcher = koinInject<PlaybackLauncher>()
    val popup = rememberPopupController()
    val webFallback = rememberWebFallback()
    val scope = rememberCoroutineScope()
    LaunchedEffect(start.id) { vm.setEvent(DetailContract.Event.Open(start)) }
    val state by vm.uiState.collectAsState()
    val current = state.current ?: start
    val player = rememberDetailPlayer()
    var autoNext by remember { mutableStateOf(repo.autoPlay) }
    var audioMode by remember { mutableStateOf(false) }
    var curQ by remember { mutableStateOf<Quality?>(null) }
    var showComments by remember { mutableStateOf(false) }
    var saveDialog by remember { mutableStateOf(false) }
    var popupAsk by remember { mutableStateOf(false) }
    // Auto Pop-up: leaving the page while a video plays moves it to the floating popup (where the platform has one).
    // Declared after the player, so it runs before the player's own dispose and can keep playback going.
    val popupVideo by rememberUpdatedState(state.video ?: current)
    val popupQuality by rememberUpdatedState(curQ)
    DisposableEffect(Unit) {
        onDispose {
            val q = popupQuality
            if (repo.autoPopup && popup.supported && popup.canDraw() && player.isPlaying && q != null) {
                popup.show(popupVideo, q.url, q.audioUrl, q.label, player.currentPosition)
                player.handOff()
            }
        }
    }
    val errorText = stringResource(Res.string.detail_playback_error, "%s")
    fun playbackError(code: String) = toast(errorText.replace("%s", code))

    val v = state.video ?: current
    val next = state.next
    fun playNext() = vm.setEvent(DetailContract.Event.PlayNext)

    val playerArea: @Composable (Boolean) -> Unit = { wide ->
            val data = state.info
            when {
                data != null && data.video.id == current.id -> {
                    var webQ by remember(current.id) { mutableStateOf<List<Quality>?>(null) }
                    var triedWeb by remember(current.id) { mutableStateOf(false) }
                    val qualities = webQ ?: remember(data) { buildQualities(data) }
                    val pref = remember { repo.onlineQuality }
                    val ordered = remember(qualities) { buildQualities.ordered(qualities, pref) }
                    InlinePlayer(player, ordered, data.captions.isNotEmpty(), autoNext, { autoNext = it }, ::playNext,
                        onAudioOnly = { audioMode = !audioMode }, wide = wide,
                        audioMode = audioMode, onPlayAsVideo = { audioMode = false }, onQuality = { curQ = it },
                        onFullscreen = { player.pause(); scope.launch { runCatching { launcher.video(v) }.onFailure { toast(it.message ?: "") } } },
                        onCollapse = { nav.back() },
                        onError = { code ->
                            if (!triedWeb && "BAD_HTTP_STATUS" in code && webFallback != null) {
                                // Direct links refused (403): let YouTube's own web player hand us working URLs.
                                triedWeb = true
                                scope.launch {
                                    val web = buildQualities.fromWeb(webFallback(current.id))
                                    if (web.isNotEmpty()) webQ = web else playbackError(code)
                                }
                            } else playbackError(code)
                        })
                }
                // Stream URLs unavailable (e.g. YouTube's bot check): fall back to YouTube's own embedded player.
                state.infoError != null -> EmbedPlayer(current.id, current.thumbnail)
                else -> Box(Modifier.fillMaxWidth().aspectRatio(DetailLayoutSpec.PLAYER_RATIO)) {
                    Thumb(current.thumbnail, Modifier.fillMaxSize(), corner = 0)
                    CircularProgressIndicator(Modifier.align(Alignment.Center), color = Color.White)
                }
            }
    }
    val ctoken = next?.commentsToken
    val actions: @Composable () -> Unit = {
                    DetailAction(Res.drawable.ic_d_save, stringResource(Res.string.detail_save)) { nav.go(Routes.YtSignIn) }
                    if (popup.supported) DetailAction(Res.drawable.ic_d_popup, stringResource(Res.string.detail_popup)) {
                        if (!popup.canDraw()) popupAsk = true
                        else curQ?.let { q -> popup.show(v, q.url, q.audioUrl, q.label, player.currentPosition); player.handOff(); nav.back() }
                    }
                    DetailAction(Res.drawable.ic_d_audio, stringResource(Res.string.detail_play_audio), if (audioMode) VRed else ViColors.ink) {
                        audioMode = !audioMode; if (!player.isPlaying) player.play()
                    }
                    DetailAction(Res.drawable.ic_d_share, stringResource(Res.string.detail_share)) { share("${v.title}\n${v.url}") }
                    DetailAction(Res.drawable.ic_d_download, stringResource(Res.string.detail_download), VRed) { nav.download(v) }
    }
    BoxWithConstraints(Modifier.fillMaxSize()) {
    if (maxWidth >= DetailLayoutSpec.WIDE_MIN) WideDetail(
        player = { playerArea(true) }, v = v, likes = next?.likes.orEmpty(),
        description = state.info?.takeIf { it.video.id == v.id }?.description.orEmpty(),
        commentCount = next?.commentCount.orEmpty(), commentsToken = ctoken,
        related = next?.related.orEmpty(), loading = state.nextLoading,
        audioMode = audioMode, autoNext = autoNext, onAutoNext = { autoNext = it },
        actions = WideActions(
            onAudio = { audioMode = !audioMode; if (!player.isPlaying) player.play() },
            onDownload = { nav.download(v) },
            onRelatedDownload = { nav.download(it) },
            onPopup = if (popup.supported) ({
                if (!popup.canDraw()) popupAsk = true
                else curQ?.let { q -> popup.show(v, q.url, q.audioUrl, q.label, player.currentPosition); player.handOff(); nav.back() }
            }) else null,
        ),
    ) else
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(Modifier.fillMaxWidth().background(ViColors.detailStatusBar).statusBarsPadding().background(Color.Black)) { playerArea(false) }
        // Below-player layout measured from VidMate (3px = 1dp).
        if (showComments && ctoken != null) CommentsPanel(ctoken, next.commentCount) { showComments = false }
        else LazyColumn(Modifier.fillMaxSize().background(ViColors.surface)) {
            item(key = "title") { DetailTitle(v, next?.likes.orEmpty()) }
            item(key = "actions") {
                DetailActions { actions() }
            }
            item(key = "comments") { CommentsCard(next?.commentCount.orEmpty(), state.firstComment, ctoken != null) { showComments = true } }
            items(next?.related.orEmpty(), key = { "r" + it.id }) { r -> VideoCard(r, onDownload = { nav.download(r) }, aspect = DetailLayoutSpec.RELATED_RATIO) }
            item(key = "loading") { if (state.nextLoading) Loading() }
        }
    }
    }
    if (saveDialog) SaveToPlaylistDialog(v) { saveDialog = false }
    if (popupAsk) PopupPermissionDialog(onAllow = { popupAsk = false; popup.requestPermission() }) { popupAsk = false }
}
