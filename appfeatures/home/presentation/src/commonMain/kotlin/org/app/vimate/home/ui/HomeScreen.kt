package org.app.vimate.home.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.app.vimate.home.viewmodel.HomeContract.Event
import org.app.vimate.home.viewmodel.HomeViewModel
import org.koin.compose.viewmodel.koinViewModel

/** Home tab (POC ui/HomeScreen.kt `HomeScreen`). */
@Composable
fun HomeScreen(viewModel: HomeViewModel = koinViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.setEvent(Event.Start) }
    HomeContent(state, onEvent = viewModel::setEvent)
}
