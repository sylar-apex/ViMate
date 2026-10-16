package org.app.vimate.shell

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.app.kmediaplayer.ui.music.MiniPlayer
import org.app.vimate.appcore.component.BasketPill
import org.app.vimate.appcore.component.LocalBottomInset
import org.app.vimate.appcore.theme.tabCanvas
import org.app.vimate.sharedlogic.data.UserDataRepository
import org.koin.compose.koinInject

/** One of the four tab pages, with the basket pill, mini player and floating navigation bar over its bottom edge. */
@Composable
internal fun TabsScreen(tab: MainTab, onTab: (MainTab) -> Unit, onBasket: () -> Unit, content: @Composable (MainTab) -> Unit) {
    val userData = koinInject<UserDataRepository>()
    val data by userData.data.collectAsStateWithLifecycle()
    val navInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(Modifier.fillMaxSize().background(tabCanvas)) {
        CompositionLocalProvider(LocalBottomInset provides ShellLayoutSpec.navHeight + navInset) {
            content(tab)
        }
        Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
            BasketPill(data.basket.size, onBasket, Modifier.padding(bottom = ShellLayoutSpec.basketGap))
            MiniPlayer()
            BottomNav(tab, onTab)
        }
    }
}
