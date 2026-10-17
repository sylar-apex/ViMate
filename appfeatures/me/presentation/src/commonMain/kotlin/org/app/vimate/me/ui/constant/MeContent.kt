package org.app.vimate.me.ui.constant

import androidx.compose.ui.graphics.Color
import org.app.vimate.appcore.theme.ViColors
import org.app.vimate.appres.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

/** ME tool grid action ids (POC tools list order). */
enum class MeTool(val icon: DrawableResource, val label: StringResource, val iconSize: Float) {
    SHARE(Res.drawable.ic_t_share, Res.string.me_share, 24f),
    LIKE(Res.drawable.ic_t_like, Res.string.me_like_us, 26f),
    UPDATE(Res.drawable.ic_t_update, Res.string.me_update, 24f),
    FEEDBACK(Res.drawable.ic_t_feedback, Res.string.me_feedback, 27f),
    HELP(Res.drawable.ic_t_help, Res.string.me_help, 24f),
    SETTINGS(Res.drawable.ic_t_settings, Res.string.me_settings, 24f),
    JUNK(Res.drawable.ic_junk, Res.string.me_junk_clean, 26f),
    BOOST(Res.drawable.ic_t_boost, Res.string.me_boost, 24f),
}

/** Help Center tabs (POC `guides`). */
data class Guide(val name: String, val svg: String?, val glyph: DrawableResource?, val tint: Color, val steps: List<StringResource>, val url: String)

val guides = listOf(
    Guide("WhatsApp", "wa", null, Color.Unspecified, listOf(Res.string.me_g_wa_1, Res.string.me_g_wa_2), "https://web.whatsapp.com"),
    Guide("YouTube", null, Res.drawable.ic_site_youtube_square, ViColors.meYoutube, listOf(Res.string.me_g_yt_1, Res.string.me_g_yt_2), "https://m.youtube.com"),
    Guide("Facebook", null, Res.drawable.ic_site_facebook_square, ViColors.meFacebook, listOf(Res.string.me_g_fb_1, Res.string.me_g_fb_2), "https://m.facebook.com"),
    Guide("Instagram", "site_instagram", null, Color.Unspecified, listOf(Res.string.me_g_ig_1, Res.string.me_g_ig_2), "https://www.instagram.com"),
)

/** Help&Feedback hot questions (POC `hotQuestions`). */
val hotQuestions = listOf(
    Res.string.me_q1 to Res.string.me_a1, Res.string.me_q2 to Res.string.me_a2, Res.string.me_q3 to Res.string.me_a3,
    Res.string.me_q4 to Res.string.me_a4, Res.string.me_q5 to Res.string.me_a5, Res.string.me_q6 to Res.string.me_a6,
    Res.string.me_q7 to Res.string.me_a7, Res.string.me_q8 to Res.string.me_a8, Res.string.me_q9 to Res.string.me_a9,
    Res.string.me_q10 to Res.string.me_a10,
)

/** "Sign in with YouTube" feature list (POC SubscriptionsScreen). */
val signInFeatures = listOf(
    Triple(Res.drawable.ic_f_subs, ViColors.meRed, Res.string.me_f_subscriptions),
    Triple(Res.drawable.ic_f_later, ViColors.meFeatureBlue, Res.string.me_f_watch_later),
    Triple(Res.drawable.ic_f_history, ViColors.meFeatureCyan, Res.string.me_f_history),
    Triple(Res.drawable.ic_f_like, ViColors.meFeatureAmber, Res.string.me_f_like),
    Triple(Res.drawable.ic_f_person, ViColors.meFeatureTeal, Res.string.me_f_personal),
)
