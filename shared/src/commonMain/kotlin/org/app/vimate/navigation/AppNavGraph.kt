package org.app.vimate.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.psbapp.navigation.utils.pop
import com.psbapp.navigation.utils.push
import org.app.vimate.detail.ui.DetailScreen
import org.app.vimate.downloads.ui.BatchScreen
import org.app.vimate.downloads.ui.DownloadsScreen
import org.app.vimate.home.ui.HomeNavigation
import org.app.vimate.home.ui.HomeScreen
import org.app.vimate.shell.MainTab
import org.app.vimate.shell.TabsScreen

fun NavGraphBuilder.appNavGraph(navController: NavController, tab: MainTab, onTab: (MainTab) -> Unit) {
    val back: () -> Unit = { navController.pop() }

    composable<Routes.Tabs> {
        TabsScreen(tab = tab, onTab = onTab, onBasket = { navController.push(Routes.Batch) }) { current ->
            when (current) {
                MainTab.HOME -> HomeScreen(
                    HomeNavigation(
                        onSearch = {},
                        onNotifications = {},
                        onSites = {},
                        onDownloads = { navController.push(Routes.Downloads) },
                        onShorts = {},
                        onDetail = { navController.push(Routes.Detail(it)) },
                    ),
                )
                MainTab.MUSIC, MainTab.FILES, MainTab.ME -> Unit
            }
        }
    }
    composable<Routes.Downloads> { DownloadsScreen(onBack = back) }
    composable<Routes.Batch> { BatchScreen(onBack = back) }
    composable<Routes.Detail> { entry ->
        val route = entry.toRoute<Routes.Detail>()
        DetailScreen(route.mediaId, route.autoPlay, onBack = back, onOpenDetail = { navController.push(Routes.Detail(it)) })
    }
}
