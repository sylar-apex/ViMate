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

/** What a site offers, shown under its name on wide cards ("Videos, Reels"). */
enum class SiteTag { Videos, Shorts, Playlists, Reels, Stories, Pins, Search, Songs, Albums, Podcasts, Talks, Spotlight, Posts }

/**
 * A site tile: either a multicolour SVG logo, or a mono glyph tinted on a coloured rounded tile. [brand] tints the
 * wide card's background; [tags] list what can be downloaded there.
 */
data class SiteTile(
    val name: String, val url: String, val svg: String? = null, val glyph: DrawableResource? = null, val glyphTint: Color = ViColors.white,
    val tile: Color = ViColors.transparent, val circle: Boolean = false, val group: SiteGroup = SiteGroup.Favorite,
    val kind: SiteKind = SiteKind.Other, val brand: Color = Color.Gray, val tags: List<SiteTag> = emptyList(),
)

/** POC `siteTiles` (brand names are not translated). */
val siteTiles = listOf(
    SiteTile("YouTube", "https://m.youtube.com", glyph = Res.drawable.ic_site_youtube_square, glyphTint = ViColors.siteYoutubeGlyph, kind = SiteKind.Video, brand = Color(0xFFFF0000), tags = listOf(SiteTag.Videos, SiteTag.Shorts, SiteTag.Playlists)),
    SiteTile("Facebook", "https://m.facebook.com", glyph = Res.drawable.ic_site_facebook_square, glyphTint = ViColors.siteFacebookGlyph, kind = SiteKind.Social, brand = Color(0xFF1877F2), tags = listOf(SiteTag.Videos, SiteTag.Reels)),
    SiteTile("Instagram", "https://www.instagram.com", svg = "site_instagram", kind = SiteKind.Social, brand = Color(0xFFE1306C), tags = listOf(SiteTag.Videos, SiteTag.Reels, SiteTag.Stories)),
    SiteTile("Google", "https://www.google.com", svg = "site_google", tile = ViColors.siteGoogleTile, brand = Color(0xFF4285F4), tags = listOf(SiteTag.Search, SiteTag.Videos)),
    SiteTile("YouTube\nMusic", "https://music.youtube.com", glyph = Res.drawable.ic_b_ytmusic, glyphTint = ViColors.white, tile = ViColors.siteYtMusicTile, circle = true, kind = SiteKind.Music, brand = Color(0xFFFF0000), tags = listOf(SiteTag.Songs, SiteTag.Playlists)),
    SiteTile("Pinterest", "https://www.pinterest.com", svg = "site_pinterest", kind = SiteKind.Social, brand = Color(0xFFE60023), tags = listOf(SiteTag.Videos, SiteTag.Pins)),
    SiteTile("Dailymotion", "https://www.dailymotion.com", glyph = Res.drawable.ic_b_dailymotion, tile = ViColors.siteDailymotionTile, kind = SiteKind.Video, brand = Color(0xFF0066DC), tags = listOf(SiteTag.Videos)),
    SiteTile("X", "https://x.com", glyph = Res.drawable.ic_b_x, tile = ViColors.black, kind = SiteKind.Social, brand = Color(0xFF000000), tags = listOf(SiteTag.Videos)),
    SiteTile("TED", "https://www.ted.com/talks", glyph = Res.drawable.ic_b_ted, glyphTint = ViColors.siteTedGlyph, tile = ViColors.siteTedTile, group = SiteGroup.Entertainment, kind = SiteKind.Entertainment, brand = Color(0xFFE62B1E), tags = listOf(SiteTag.Talks)),
    SiteTile("Snapchat", "https://www.snapchat.com/spotlight", glyph = Res.drawable.ic_b_snapchat, glyphTint = ViColors.black, tile = ViColors.siteSnapchatTile, group = SiteGroup.Entertainment, kind = SiteKind.Social, brand = Color(0xFFF5C400), tags = listOf(SiteTag.Spotlight)),
    SiteTile("SnackVideo", "https://www.snackvideo.com", glyph = Res.drawable.ic_b_play, glyphTint = ViColors.siteSnackGlyph, tile = ViColors.black, circle = true, group = SiteGroup.Entertainment, kind = SiteKind.Video, brand = Color(0xFFFFA000), tags = listOf(SiteTag.Videos)),
    SiteTile("Threads", "https://www.threads.net", glyph = Res.drawable.ic_b_threads, tile = ViColors.black, group = SiteGroup.Entertainment, kind = SiteKind.Social, brand = Color(0xFF000000), tags = listOf(SiteTag.Videos, SiteTag.Posts)),
    SiteTile("Spotify", "https://open.spotify.com", glyph = Res.drawable.ic_site_spotify, glyphTint = Color.Unspecified, group = SiteGroup.Music, kind = SiteKind.Music, brand = Color(0xFF1DB954), tags = listOf(SiteTag.Songs, SiteTag.Podcasts)),
    SiteTile("SoundCloud", "https://soundcloud.com", glyph = Res.drawable.ic_site_soundcloud, glyphTint = Color.Unspecified, group = SiteGroup.Music, kind = SiteKind.Music, brand = Color(0xFFFF5500), tags = listOf(SiteTag.Songs)),
    SiteTile("Apple Music", "https://music.apple.com", glyph = Res.drawable.ic_site_apple_music, glyphTint = Color.Unspecified, group = SiteGroup.Music, kind = SiteKind.Music, brand = Color(0xFFFA243C), tags = listOf(SiteTag.Songs, SiteTag.Albums)),
    SiteTile("Deezer", "https://www.deezer.com", glyph = Res.drawable.ic_site_deezer, glyphTint = Color.Unspecified, group = SiteGroup.Music, kind = SiteKind.Music, brand = Color(0xFFA238FF), tags = listOf(SiteTag.Songs)),
    SiteTile("Tidal", "https://tidal.com", glyph = Res.drawable.ic_site_tidal, glyphTint = Color.Unspecified, group = SiteGroup.Music, kind = SiteKind.Music, brand = Color(0xFF000000), tags = listOf(SiteTag.Songs)),
    SiteTile("Amazon Music", "https://music.amazon.com", glyph = Res.drawable.ic_site_amazon_music, glyphTint = Color.Unspecified, group = SiteGroup.Music, kind = SiteKind.Music, brand = Color(0xFF25D1DA), tags = listOf(SiteTag.Songs)),
)
