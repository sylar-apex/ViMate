package org.app.vimate.appcore.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.app.kmediaplayer.KMediaPlayer
import org.app.kmediaplayer.ui.settings.settingsVersion
import org.app.vimate.sharedlogic.settings.AppKeys

/** POC `VRed`. */
val VRed: Color = ViColors.vRed

private val DarkColors = darkColorScheme(
    primary = VRed,
    background = ViColors.pocDarkBackground,
    surface = ViColors.pocDarkBackground,
    surfaceVariant = ViColors.pocDarkSurfaceVariant,
    surfaceContainer = ViColors.pocDarkSurfaceContainer,
)

private val LightColors = lightColorScheme(
    primary = VRed,
    background = ViColors.pocLightBackground,
    surface = ViColors.pocLightBackground,
    surfaceVariant = ViColors.pocLightSurfaceVariant,
    surfaceContainer = ViColors.pocLightSurfaceContainer,
)

/** True while the app shows its dark theme: AppKeys.theme "dark" / "light" / "system". */
@Composable
fun isViDark(): Boolean {
    settingsVersion()
    return when (KMediaPlayer.settings[AppKeys.theme]) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
}

/** The app theme (POC VidTheme): VRed primary, light/dark/system from Settings › Theme. */
@Composable
fun ViTheme(content: @Composable () -> Unit) {
    val dark = isViDark()
    // Screens read ViColors directly; flip its neutrals before they compose.
    ViColors.isDark = dark
    MaterialTheme(colorScheme = if (dark) DarkColors else LightColors, content = content)
}
