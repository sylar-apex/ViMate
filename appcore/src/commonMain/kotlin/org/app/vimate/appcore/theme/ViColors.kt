package org.app.vimate.appcore.theme

import androidx.compose.ui.graphics.Color

/** Every fixed colour used by the app's screens. Theme-dependent colours come from MaterialTheme.colorScheme. */
object ViColors {
    // Brand
    val red = Color(0xFFEB2D33)
    val redButton = Color(0xFFF03F40)
    val dot = Color(0xFFEA3F40)
    val checkRed = Color(0xFFEB3F3E)
    val radioRed = Color(0xFFF00023)
    val privateRed = Color(0xFFF13031)
    val shortsRed = Color(0xFFFF0033)

    // Text and lines (light theme)
    val ink = Color(0xFF212121)
    val muted = Color(0xFF757575)
    val hint = Color(0xFFB4B4B4)
    val line = Color(0xFFE6E6EA)
    val tabGrey = Color(0xFF7A7A7A)
    val hairline = Color(0xFFC3C3C3)
    val separator = Color(0xFFD0D0D0)
    val searchIcon = Color(0xFF363636)
    val menuBorder = Color(0xFFBCBCBC)

    // Surfaces (light theme)
    val canvas = Color(0xFFF5F5F7)
    val page = Color(0xFFF4F4F4)
    val chipOn = Color(0xFF383838)
    val chipDark = Color(0xFF1C1C1E)
    val selected = Color(0xFFDBEBF5)
    val navBar = Color(0xFFFCFCFC)
    val sheet = Color(0xFFF6F7F9)
    val gap = Color(0xFFEDEDED)
    val hotSites = Color(0xFFF6F6F6)
    val keypad = Color(0xFFF2F2F2)
    val storageUsed = Color(0xFFCFCFCF)
    val progressTrack = Color(0xFFE3E3E3)
    val radioOff = Color(0xFFB0B0B0)
    val reportRadioOff = Color(0xFFD5D5D5)
    val disabledButton = Color(0xFF888888)
    val sheetHandle = Color(0xFFCACBD0)
    val emptyIcon = Color(0xFFCFCFCF)
    val fileThumb = Color(0xFF2B2B2B)
    val lockBadge = Color(0xFFDDF3EC)
    val lockIcon = Color(0xFF55B99A)

    // Links and accents
    val link = Color(0xFF1E88E5)
    val blue = Color(0xFF0099FF)
    val pillBlue = Color(0xFF0698F8)
    val playAllBlue = Color(0xFF258ADA)

    // Dark theme
    val darkBackground = Color(0xFF101114)
    val darkOnBackground = Color(0xFFF4F4F5)
    val darkSurface = Color(0xFF1A1C21)
    val darkSurfaceVariant = Color(0xFF26282E)
    val darkOnSurfaceVariant = Color(0xFFB0B0B6)
    val darkOutline = Color(0xFF33343A)

    // On-image overlays
    val white = Color(0xFFFFFFFF)
    val black = Color(0xFF000000)
    val transparent = Color(0x00000000)

    // Brand tiles of the site shortcuts
    val youtube = Color(0xFFE8353D)
    val facebook = Color(0xFF4574CB)
    val facebookMini = Color(0xFF3B6FD6)
    val google = Color(0xFFEAEAEA)
    val sitesTile = Color(0xFFE7E7E7)
    val twitter = Color(0xFF1DA1F2)
    val pinterest = Color(0xFFE60023)
    val youtubeMini = Color(0xFFF00000)
    val instagram = listOf(Color(0xFFFFD25E), Color(0xFFF77737), Color(0xFFE1306C), Color(0xFFC13584), Color(0xFF6440C4))

    // Open-source site tiles
    val archive = Color(0xFF4F5B66)
    val commons = Color(0xFF2F80ED)
    val pexels = Color(0xFF07A081)
    val blender = Color(0xFFF2994A)

    /** Gradient pairs used for posters and avatars, picked by a stable hash of the id. */
    val posterPalettes = listOf(
        listOf(Color(0xFF1D4ED8), Color(0xFF7C3AED)),
        listOf(Color(0xFFBE123C), Color(0xFFF97316)),
        listOf(Color(0xFF0F766E), Color(0xFF22C55E)),
        listOf(Color(0xFF1E293B), Color(0xFF64748B)),
        listOf(Color(0xFF9D174D), Color(0xFFFB7185)),
        listOf(Color(0xFF92400E), Color(0xFFFBBF24)),
    )
}
