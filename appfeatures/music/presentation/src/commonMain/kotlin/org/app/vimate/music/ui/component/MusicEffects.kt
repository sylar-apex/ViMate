package org.app.vimate.music.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow
import org.app.vimate.appcore.component.LocalToast
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.common_network_error
import org.app.vimate.music.ui.LoadError
import org.app.vimate.music.ui.MusicEffect
import org.jetbrains.compose.resources.stringResource

/** Shows [MusicEffect.Toast]s in the shell toast. */
@Composable
internal fun CollectMusicEffects(effects: Flow<MusicEffect>) {
    val toast = LocalToast.current
    LaunchedEffect(effects) { effects.collect { if (it is MusicEffect.Toast) toast(it.message) } }
}

/** Error text, "Network error" when the failure had no message (as rememberLoad). */
@Composable
internal fun LoadError.text(): String = message ?: stringResource(Res.string.common_network_error)
