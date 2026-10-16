package org.app.vimate.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import org.app.vimate.shell.MainTab
import org.app.vimate.shell.TabsScreen

fun NavGraphBuilder.appNavGraph(navController: NavController, tab: MainTab, onTab: (MainTab) -> Unit) {
    composable<Routes.Tabs> {
        TabsScreen(tab = tab, onTab = onTab)
    }
}
