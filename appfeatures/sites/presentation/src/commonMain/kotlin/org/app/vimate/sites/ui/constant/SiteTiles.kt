package org.app.vimate.sites.ui.constant

import androidx.compose.ui.graphics.Color
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.ic_b_dailymotion
import org.app.vimate.appres.resources.ic_b_play
import org.app.vimate.appres.resources.ic_b_snapchat
import org.app.vimate.appres.resources.ic_b_ted
import org.app.vimate.appres.resources.ic_b_threads
import org.app.vimate.appres.resources.ic_b_x
import org.app.vimate.appres.resources.ic_b_ytmusic
import org.app.vimate.appres.resources.ic_site_amazon_music
import org.app.vimate.appres.resources.ic_site_apple_music
import org.app.vimate.appres.resources.ic_site_deezer
import org.app.vimate.appres.resources.ic_site_soundcloud
import org.app.vimate.appres.resources.ic_site_spotify
import org.app.vimate.appres.resources.ic_site_tidal
import org.app.vimate.appres.resources.ic_site_facebook_square
import org.app.vimate.appres.resources.ic_site_youtube_square
import org.jetbrains.compose.resources.DrawableResource

enum class SiteGroup { Favorite, Entertainment, Music }

/** Chip on the Sites page. [All] shows every section. */
enum class SiteKind { Social, Video, Music, Entertainment, News, Shopping, Other }

/** A site tile: either a multicolour SVG logo, or a mono glyph tinted on a coloured rounded tile. */
data class SiteTile(
    val name: String, val url: String, val svg: String? = null, val glyph: DrawableResource? = null, val glyphTint: Color = ViColors.white,
    val tile: Color = ViColors.transparent, val circle: Boolean = false, val group: SiteGroup = SiteGroup.Favorite,
    val kind: SiteKind = SiteKind.Other,
)

/** POC `siteTiles` (brand names are not translated). */
val siteTiles = listOf(
    SiteTile("YouTube", "https://m.youtube.com", glyph = Res.drawable.ic_site_youtube_square, glyphTint = ViColors.siteYoutubeGlyph, kind = SiteKind.Video),
    SiteTile("Facebook", "https://m.facebook.com", glyph = Res.drawable.ic_site_facebook_square, glyphTint = ViColors.siteFacebookGlyph, kind = SiteKind.Social),
    SiteTile("Instagram", "https://www.instagram.com", svg = "site_instagram", kind = SiteKind.Social),
    SiteTile("Google", "https://www.google.com", svg = "site_google", tile = ViColors.siteGoogleTile),
    SiteTile("YouTube\nMusic", "https://music.youtube.com", glyph = Res.drawable.ic_b_ytmusic, glyphTint = ViColors.white, tile = ViColors.siteYtMusicTile, circle = true, kind = SiteKind.Music),
    SiteTile("Pinterest", "https://www.pinterest.com", svg = "site_pinterest", kind = SiteKind.Social),
    SiteTile("Dailymotion", "https://www.dailymotion.com", glyph = Res.drawable.ic_b_dailymotion, tile = ViColors.siteDailymotionTile, kind = SiteKind.Video),
    SiteTile("X", "https://x.com", glyph = Res.drawable.ic_b_x, tile = ViColors.black, kind = SiteKind.Social),
    SiteTile("TED", "https://www.ted.com/talks", glyph = Res.drawable.ic_b_ted, glyphTint = ViColors.siteTedGlyph, tile = ViColors.siteTedTile, group = SiteGroup.Entertainment, kind = SiteKind.Entertainment),
    SiteTile("Snapchat", "https://www.snapchat.com/spotlight", glyph = Res.drawable.ic_b_snapchat, glyphTint = ViColors.black, tile = ViColors.siteSnapchatTile, group = SiteGroup.Entertainment, kind = SiteKind.Social),
    SiteTile("SnackVideo", "https://www.snackvideo.com", glyph = Res.drawable.ic_b_play, glyphTint = ViColors.siteSnackGlyph, tile = ViColors.black, circle = true, group = SiteGroup.Entertainment, kind = SiteKind.Video),
    SiteTile("Threads", "https://www.threads.net", glyph = Res.drawable.ic_b_threads, tile = ViColors.black, group = SiteGroup.Entertainment, kind = SiteKind.Social),
    SiteTile("Spotify", "https://open.spotify.com", glyph = Res.drawable.ic_site_spotify, glyphTint = Color.Unspecified, group = SiteGroup.Music, kind = SiteKind.Music),
    SiteTile("SoundCloud", "https://soundcloud.com", glyph = Res.drawable.ic_site_soundcloud, glyphTint = Color.Unspecified, group = SiteGroup.Music, kind = SiteKind.Music),
    SiteTile("Apple Music", "https://music.apple.com", glyph = Res.drawable.ic_site_apple_music, glyphTint = Color.Unspecified, group = SiteGroup.Music, kind = SiteKind.Music),
    SiteTile("Deezer", "https://www.deezer.com", glyph = Res.drawable.ic_site_deezer, glyphTint = Color.Unspecified, group = SiteGroup.Music, kind = SiteKind.Music),
    SiteTile("Tidal", "https://tidal.com", glyph = Res.drawable.ic_site_tidal, glyphTint = Color.Unspecified, group = SiteGroup.Music, kind = SiteKind.Music),
    SiteTile("Amazon Music", "https://music.amazon.com", glyph = Res.drawable.ic_site_amazon_music, glyphTint = Color.Unspecified, group = SiteGroup.Music, kind = SiteKind.Music),
)
