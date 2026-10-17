package org.app.vimate.appcore.locale

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import org.app.kmediaplayer.KMediaPlayer
import org.app.kmediaplayer.ui.settings.settingsVersion
import org.app.vimate.sharedlogic.settings.AppKeys

/** App languages offered in Settings › Content (label shown → locale tag). */
val AppLanguages = linkedMapOf("English" to "en", "हिन्दी" to "hi")

/** Locale tag for the language saved in Settings; English when unknown. */
fun appLanguageTag(label: String): String = AppLanguages[label] ?: "en"

/** Applies [tag] as the locale compose resources read strings from (per platform). */
internal expect fun applyAppLocale(tag: String)

/** Shows [content] in the language picked in Settings › Content; a change rebuilds the UI in the new language. */
@Composable
fun ProvideAppLanguage(content: @Composable () -> Unit) {
    settingsVersion()
    val tag = appLanguageTag(KMediaPlayer.settings[AppKeys.language])
    applyAppLocale(tag)
    LocaleOverride(tag) { key(tag) { content() } }
}

/** Platforms whose resource lookup follows a composition local (Android's Configuration) override it here. */
@Composable
internal expect fun LocaleOverride(tag: String, content: @Composable () -> Unit)
