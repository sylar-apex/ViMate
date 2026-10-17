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
import org.app.vimate.appres.resources.ic_site_facebook_square
import org.app.vimate.appres.resources.ic_site_youtube_square
import org.jetbrains.compose.resources.DrawableResource

enum class SiteGroup { Favorite, Entertainment }

/** A site tile: either a multicolour SVG logo, or a mono glyph tinted on a coloured rounded tile. */
data class SiteTile(
    val name: String, val url: String, val svg: String? = null, val glyph: DrawableResource? = null, val glyphTint: Color = ViColors.white,
    val tile: Color = ViColors.transparent, val circle: Boolean = false, val group: SiteGroup = SiteGroup.Favorite,
)

/** POC `siteTiles` (brand names are not translated). */
val siteTiles = listOf(
    SiteTile("YouTube", "https://m.youtube.com", glyph = Res.drawable.ic_site_youtube_square, glyphTint = ViColors.siteYoutubeGlyph),
    SiteTile("Facebook", "https://m.facebook.com", glyph = Res.drawable.ic_site_facebook_square, glyphTint = ViColors.siteFacebookGlyph),
    SiteTile("Instagram", "https://www.instagram.com", svg = "site_instagram"),
    SiteTile("Google", "https://www.google.com", svg = "site_google", tile = ViColors.siteGoogleTile),
    SiteTile("Youtube Music", "https://music.youtube.com", glyph = Res.drawable.ic_b_ytmusic, glyphTint = ViColors.white, tile = ViColors.siteYtMusicTile, circle = true),
    SiteTile("Pinterest", "https://www.pinterest.com", svg = "site_pinterest"),
    SiteTile("Dailymotion", "https://www.dailymotion.com", glyph = Res.drawable.ic_b_dailymotion, tile = ViColors.siteDailymotionTile),
    SiteTile("X", "https://x.com", glyph = Res.drawable.ic_b_x, tile = ViColors.black),
    SiteTile("TED", "https://www.ted.com/talks", glyph = Res.drawable.ic_b_ted, glyphTint = ViColors.siteTedGlyph, tile = ViColors.siteTedTile, group = SiteGroup.Entertainment),
    SiteTile("Snapchat", "https://www.snapchat.com/spotlight", glyph = Res.drawable.ic_b_snapchat, glyphTint = ViColors.black, tile = ViColors.siteSnapchatTile, group = SiteGroup.Entertainment),
    SiteTile("snackvideo", "https://www.snackvideo.com", glyph = Res.drawable.ic_b_play, glyphTint = ViColors.siteSnackGlyph, tile = ViColors.black, circle = true, group = SiteGroup.Entertainment),
    SiteTile("Threads", "https://www.threads.net", glyph = Res.drawable.ic_b_threads, tile = ViColors.black, group = SiteGroup.Entertainment),
)
