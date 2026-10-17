package org.app.vimate.search.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appcore.navigation.LocalAppNavigator
import org.app.vimate.appcore.navigation.Routes
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.common_error
import org.app.vimate.search.viewmodel.SearchContract.Effect
import org.app.vimate.search.viewmodel.SearchContract.Event
import org.app.vimate.search.viewmodel.SearchViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

/** Search page (POC ui/HomeScreen.kt `SearchScreen(initial)`). */
@Composable
fun SearchScreen(initial: String, viewModel: SearchViewModel = koinViewModel()) {
    val nav = LocalAppNavigator.current
    val toast = LocalToast.current
    val errorText = stringResource(Res.string.common_error)
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.setEvent(Event.Start(initial)) }
    LaunchedEffect(viewModel) {
        viewModel.effect.collect { e ->
            when (e) {
                is Effect.OpenLink -> { nav.back(); nav.open(e.url) }
                is Effect.OpenBrowser -> nav.go(Routes.Browser(e.url))
                is Effect.Toast -> toast(e.message ?: errorText)
            }
        }
    }
    SearchContent(state, autoFocus = initial.isEmpty(), onEvent = viewModel::setEvent)
}
