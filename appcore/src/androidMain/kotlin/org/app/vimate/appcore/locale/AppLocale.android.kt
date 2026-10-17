package org.app.vimate.appcore.locale

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale

internal actual fun applyAppLocale(tag: String) { Locale.setDefault(Locale.forLanguageTag(tag)) }

/** Compose resources read the locale from LocalConfiguration on Android. */
@Composable
internal actual fun LocaleOverride(tag: String, content: @Composable () -> Unit) {
    val base = LocalConfiguration.current
    val config = remember(base, tag) { Configuration(base).apply { setLocale(Locale.forLanguageTag(tag)) } }
    CompositionLocalProvider(LocalConfiguration provides config, content = content)
}
