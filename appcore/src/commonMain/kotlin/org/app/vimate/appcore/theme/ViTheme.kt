package org.app.vimate.appcore.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.app.kmediaplayer.KMediaPlayer
import org.app.kmediaplayer.ui.settings.settingsVersion
import org.app.vimate.sharedlogic.settings.AppKeys

private val LightColors = lightColorScheme(
    primary = ViColors.red,
    onPrimary = ViColors.white,
    background = ViColors.white,
    onBackground = ViColors.ink,
    surface = ViColors.white,
    onSurface = ViColors.ink,
    surfaceVariant = ViColors.canvas,
    onSurfaceVariant = ViColors.muted,
    outline = ViColors.line,
)

private val DarkColors = darkColorScheme(
    primary = ViColors.red,
    onPrimary = ViColors.white,
    background = ViColors.darkBackground,
    onBackground = ViColors.darkOnBackground,
    surface = ViColors.darkSurface,
    onSurface = ViColors.darkOnBackground,
    surfaceVariant = ViColors.darkSurfaceVariant,
    onSurfaceVariant = ViColors.darkOnSurfaceVariant,
    outline = ViColors.darkOutline,
)

private val Type = Typography(
    headlineMedium = TextStyle(fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = (-0.4).sp),
    titleLarge = TextStyle(fontWeight = FontWeight.Bold, fontSize = 22.sp),
    titleMedium = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 16.sp),
    bodyLarge = TextStyle(fontSize = 16.sp),
    bodyMedium = TextStyle(fontSize = 14.sp),
    labelLarge = TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    labelMedium = TextStyle(fontWeight = FontWeight.Medium, fontSize = 12.sp),
)

/** True while the app shows its dark theme. */
@Composable
fun isViDark(): Boolean {
    settingsVersion()
    return KMediaPlayer.settings[AppKeys.darkTheme]
}

/** ViMate's light or dark theme, following the Me page switch. */
@Composable
fun ViTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isViDark()) DarkColors else LightColors, typography = Type, content = content)
}

/** Grey canvas behind the four tab pages; sub-pages use the plain background. */
val tabCanvas: Color
    @Composable get() = if (isViDark()) MaterialTheme.colorScheme.background else ViColors.page

/** Text colour on the light grey tab pages. */
val pageInk: Color
    @Composable get() = if (isViDark()) MaterialTheme.colorScheme.onBackground else ViColors.ink

/** Secondary text colour that stays readable in both themes. */
val mutedInk: Color
    @Composable get() = if (isViDark()) MaterialTheme.colorScheme.onSurfaceVariant else ViColors.muted
