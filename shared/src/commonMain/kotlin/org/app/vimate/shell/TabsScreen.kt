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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.app.vimate.appcore.component.LocalBottomInset
import org.app.vimate.appcore.theme.tabCanvas

/** One of the four tab pages with the floating navigation bar over its bottom edge. */
@Composable
internal fun TabsScreen(tab: MainTab, onTab: (MainTab) -> Unit) {
    val navInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    Box(Modifier.fillMaxSize().background(tabCanvas)) {
        CompositionLocalProvider(LocalBottomInset provides ShellLayoutSpec.navHeight + navInset) {
            TabContent(tab)
        }
        Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
            BottomNav(tab, onTab)
        }
    }
}

@Composable
private fun TabContent(tab: MainTab) {
    when (tab) {
        MainTab.HOME, MainTab.MUSIC, MainTab.FILES, MainTab.ME -> Box(Modifier.fillMaxSize())
    }
}
