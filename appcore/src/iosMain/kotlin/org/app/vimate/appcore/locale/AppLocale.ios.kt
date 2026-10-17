package org.app.vimate.appcore.locale

import androidx.compose.runtime.Composable
import platform.Foundation.NSUserDefaults

internal actual fun applyAppLocale(tag: String) {
    NSUserDefaults.standardUserDefaults.setObject(listOf(tag), forKey = "AppleLanguages")
}

@Composable
internal actual fun LocaleOverride(tag: String, content: @Composable () -> Unit) = content()
