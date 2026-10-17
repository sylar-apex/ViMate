package org.app.vimate.appcore.locale

import androidx.compose.runtime.Composable

// The browser decides the web locale; the app language setting does not change it there.
internal actual fun applyAppLocale(tag: String) {}

@Composable
internal actual fun LocaleOverride(tag: String, content: @Composable () -> Unit) = content()
