package org.app.vimate.me.ui.constant

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import org.app.vimate.appcore.theme.ViColors

/** POC HomeScreen `Site`; brand names stay untranslated. */
data class HotSite(val name: String, val url: String, val icon: ImageVector, val color: Color, val adult: Boolean = false)

/** POC `allSites` (ME › Hot Sites shows the non-adult ones). */
val hotSites = listOf(
    HotSite("YouTube", "https://m.youtube.com", Icons.Filled.SmartDisplay, ViColors.meYoutube),
    HotSite("Youtube Music", "https://music.youtube.com", Icons.Filled.MusicNote, ViColors.siteMusic),
    HotSite("Instagram", "https://www.instagram.com", Icons.Filled.PhotoCamera, ViColors.siteInstagram),
    HotSite("Facebook", "https://m.facebook.com", Icons.Filled.Facebook, ViColors.siteFacebook),
    HotSite("Threads", "https://www.threads.net", Icons.Filled.AlternateEmail, ViColors.siteThreads),
    HotSite("Pinterest", "https://www.pinterest.com", Icons.Filled.PushPin, ViColors.sitePinterest),
    HotSite("Snapchat", "https://www.snapchat.com/spotlight", Icons.Filled.Face, ViColors.siteSnapchat),
    HotSite("Dailymotion", "https://www.dailymotion.com", Icons.Filled.PlayCircle, ViColors.siteDailymotion),
    HotSite("TED", "https://www.ted.com/talks", Icons.Filled.RecordVoiceOver, ViColors.siteTed),
    HotSite("snackvideo", "https://www.snackvideo.com", Icons.Filled.Videocam, ViColors.siteSnack),
    HotSite("X / Twitter", "https://x.com", Icons.Filled.Tag, ViColors.siteX),
    HotSite("Vimeo", "https://vimeo.com/watch", Icons.Filled.Movie, ViColors.siteVimeo),
    HotSite("Google", "https://www.google.com", Icons.Filled.Search, ViColors.siteGoogle),
)
