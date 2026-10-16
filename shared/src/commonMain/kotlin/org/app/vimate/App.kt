package org.app.vimate

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.psbapp.navigation.utils.LocalNavHostController
import com.psbapp.navigation.utils.PSBNavHost
import org.app.kmediaplayer.KMediaPlayer
import org.app.kmediaplayer.ui.settings.settingsVersion
import org.app.vimate.appcore.theme.ViTheme
import org.app.vimate.navigation.Routes
import org.app.vimate.navigation.appNavGraph
import org.app.vimate.onboarding.ui.OnboardingScreen
import org.app.vimate.sharedlogic.event.AppEvents
import org.app.vimate.sharedlogic.settings.AppKeys
import org.app.vimate.shell.MainTab
import org.app.vimate.shell.MessageToast
import org.koin.compose.koinInject

/** The main window: onboarding on first launch, then the tabs and every sub-page. */
@Composable
fun App() {
    ViTheme {
        settingsVersion()
        val events = koinInject<AppEvents>()
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
            if (!KMediaPlayer.settings[AppKeys.onboardingDone]) {
                OnboardingScreen()
            } else {
                MainWindow(events)
            }
        }
    }
}

@Composable
private fun MainWindow(events: AppEvents) {
    val navController = rememberNavController()
    var tab by rememberSaveable { mutableStateOf(MainTab.HOME) }
    val entry by navController.currentBackStackEntryAsState()
    val onTabs = entry?.destination?.route?.endsWith(Routes.Tabs::class.simpleName.orEmpty()) != false
    Box(Modifier.fillMaxSize().statusBarsPadding()) {
        CompositionLocalProvider(LocalNavHostController provides navController) {
            PSBNavHost(navController, startDestination = Routes.Tabs, isAnimationEnabled = false) {
                appNavGraph(navController, tab) { tab = it }
            }
        }
        MessageToast(events, aboveNav = onTabs)
    }
}
