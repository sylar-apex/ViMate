package org.app.vimate.appcore.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.app.vimate.appcore.theme.VRed
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.common_network_error
import org.app.vimate.appres.resources.common_retry
import org.jetbrains.compose.resources.stringResource

@Composable
/** Loading placeholder used across the app: a shimmering list skeleton. */
fun Loading(modifier: Modifier = Modifier) = ShimmerList(4, modifier)

@Composable
fun ErrorBox(msg: String, retry: () -> Unit) = Column(Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
    Text(msg, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .7f))
    TextButton(onClick = retry) { Text(stringResource(Res.string.common_retry)) }
}

@Composable
fun Empty(msg: String) = Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
    Text(msg, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .5f))
}

/** Loads a value once per key with loading / error state. */
class Loadable<T> {
    var data by mutableStateOf<T?>(null)
    var error by mutableStateOf<String?>(null)
    var loading by mutableStateOf(false)
}

/** Runs [block] once per [keys]; the returned lambda retries. Error text falls back to "Network error". */
@Composable
fun <T> rememberLoad(vararg keys: Any?, block: suspend () -> T): Pair<Loadable<T>, () -> Unit> {
    val state = remember(*keys) { Loadable<T>() }
    var attempt by remember(*keys) { mutableIntStateOf(0) }
    val networkError = stringResource(Res.string.common_network_error)
    LaunchedEffect(*keys, attempt) {
        state.loading = true; state.error = null
        runCatching { block() }.onSuccess { state.data = it }.onFailure { state.error = it.message ?: networkError }
        state.loading = false
    }
    return state to { attempt++ }
}
