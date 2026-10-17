package org.app.vimate.appcore.locale

import androidx.compose.runtime.Composable
import java.util.Locale

internal actual fun applyAppLocale(tag: String) { Locale.setDefault(Locale.forLanguageTag(tag)) }

@Composable
internal actual fun LocaleOverride(tag: String, content: @Composable () -> Unit) = content()
