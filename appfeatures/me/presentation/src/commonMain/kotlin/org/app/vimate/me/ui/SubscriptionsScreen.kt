package org.app.vimate.me.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.vimate.appcore.component.*
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.app.vimate.me.ui.component.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.app.vimate.me.domain.constant.MeUrls
import org.app.vimate.me.ui.constant.signInFeatures

/** Subscription: VidMate's "Sign in with YouTube" feature page; followed channels (local) appear once you follow any. */
@Composable
fun SubscriptionsScreen(forceSignIn: Boolean = false, viewModel: LibraryViewModel = koinViewModel()) {
    val nav = LocalAppNavigator.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val subs = state.subscriptions
    var channel by remember(subs.isEmpty()) { mutableStateOf(subs.firstOrNull()?.id) }
    val watchLater = stringResource(Res.string.me_watch_later)
    Column(Modifier.fillMaxSize().background(ViColors.white).statusBarsPadding()) {
        PageHeader(stringResource(if (subs.isEmpty() || forceSignIn) Res.string.me_sign_in_with_youtube else Res.string.me_subscription), bold = false) { nav.back() }
        if (viewModel.signedIn && !forceSignIn) { AccountFeedList(MeUrls.FEED_SUBSCRIPTIONS, viewModel); return@Column }
        if (viewModel.signedIn && forceSignIn) { LaunchedEffect(Unit) { nav.back(); nav.go(Routes.AccountFeed(watchLater, MeUrls.FEED_WATCH_LATER)) }; return@Column }
        if (subs.isEmpty() || forceSignIn) {
            LazyColumn(Modifier.padding(start = 33.dp, top = 30.dp), userScrollEnabled = false) {
                item(key = "head") {
                    LinkLogos(65, Res.drawable.ic_arrows_lr)
                    Text(stringResource(Res.string.me_sign_in_with_youtube), Modifier.padding(top = 38.dp), fontWeight = FontWeight.Bold, fontSize = 24.5.sp, lineHeight = 32.sp, color = ViColors.ink)
                    Text(stringResource(Res.string.me_unlock_features), Modifier.padding(top = 10.dp), fontSize = 16.5.sp, lineHeight = 21.7.sp, color = ViColors.meIconGrey)
                    Spacer(Modifier.height(20.dp))
                }
                items(signInFeatures, key = { it.third.key }) { (ic, tint, label) ->
                    Row(Modifier.height(29.dp), verticalAlignment = Alignment.CenterVertically) {
                        Ic(ic, Modifier.size(12.dp), tint)
                        Text(stringResource(label), Modifier.padding(start = 7.dp), fontSize = 13.sp, lineHeight = 19.sp, color = ViColors.ink)
                    }
                }
            }
            RedSignInBar(Modifier.padding(start = 30.dp, end = 30.dp, top = 42.dp), corner = 4, bold = true, fontSize = 16f) { nav.go(Routes.GoogleLogin) }
            return@Column
        }
        val (load, retry) = rememberLoad(channel) { channel?.takeIf { !it.startsWith("name:") }?.let { viewModel.channelVideos(it) } ?: emptyList() }
        LazyRow(contentPadding = PaddingValues(8.dp)) {
            items(subs, key = { it.id }) { c ->
                Column(Modifier.width(80.dp).clickable { channel = c.id }.padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Avatar(c.avatar, 52)
                    Text(c.name, fontSize = 11.sp, maxLines = 1, fontWeight = if (c.id == channel) FontWeight.Bold else FontWeight.Normal,
                        color = if (c.id == channel) VRed else androidx.compose.ui.graphics.Color.Unspecified)
                }
            }
        }
        LazyColumn {
            if (load.loading) item(key = "loading") { Loading() }
            load.error?.let { item(key = "error") { ErrorBox(it, retry) } }
            items(load.data.orEmpty(), key = { it.id }) { v -> VideoCard(v, onDownload = { nav.downloadFor = v }) }
        }
    }
}

@Composable
fun AccountFeedScreen(title: String, browseId: String, viewModel: LibraryViewModel = koinViewModel()) {
    val nav = LocalAppNavigator.current
    Column(Modifier.fillMaxSize().background(ViColors.white).statusBarsPadding()) {
        PageHeader(title) { nav.back() }
        AccountFeedList(browseId, viewModel)
    }
}

@Composable
private fun AccountFeedList(browseId: String, viewModel: LibraryViewModel) {
    val nav = LocalAppNavigator.current
    val (state, retry) = rememberLoad(browseId) { viewModel.accountFeed(browseId) }
    LazyColumn {
        if (state.loading) item(key = "loading") { Loading() }
        state.error?.let { item(key = "error") { ErrorBox(it, retry) } }
        if (!state.loading && state.data.orEmpty().isEmpty() && state.error == null) item(key = "empty") { Empty(stringResource(Res.string.me_nothing_here)) }
        items(state.data.orEmpty(), key = { it.id }) { v -> VideoCard(v, onDownload = { nav.downloadFor = v }) }
    }
}
