package org.app.vimate.sharedlogic.settings

import org.app.kmediaplayer.settings.BoolSetting
import org.app.kmediaplayer.settings.ChoiceSetting
import org.app.kmediaplayer.settings.IntSetting
import org.app.kmediaplayer.settings.PlayerKeys
import org.app.kmediaplayer.settings.SettingRegistry
import org.app.kmediaplayer.settings.TextSetting

/**
 * ViMate preferences outside the player. Player preferences (speed, gestures, background play, resume)
 * live in [PlayerKeys] so the media player library reads them directly.
 */
object AppKeys : SettingRegistry() {
    // ---- App ----
    val onboardingDone = r(BoolSetting("onboarding_done", "Onboarding done", false))
    val darkTheme = r(BoolSetting("dark_theme", "Dark theme", false))
    val shortsSeen = r(BoolSetting("shorts_seen", "Shorts seen", false))
    val language = r(ChoiceSetting("language", "Language", AppChoices.DEFAULT_LANGUAGE, AppChoices.languages.map { it to it }))
    val location = r(ChoiceSetting("location", "Location", AppChoices.DEFAULT_LOCATION, AppChoices.locations.map { it to it }))

    // ---- Content filters ----
    val restrictedRecommendations = r(BoolSetting("restricted_recommendations", "Recommendations filter", false))
    val restrictedBrowsing = r(BoolSetting("restricted_browsing", "Browsing filter", false))
    val restrictedSearch = r(BoolSetting("restricted_search", "Search filter", false))
    val restrictedSites = r(BoolSetting("restricted_sites", "Sites filter", false))

    // ---- Downloads ----
    val wifiOnly = r(BoolSetting("download_wifi_only", "Download only on Wi-Fi", false))
    val maxTasks = r(IntSetting("download_max_tasks", "Max download tasks", AppChoices.DEFAULT_TASKS, AppChoices.taskLimits.first()..AppChoices.taskLimits.last()))
    val fastDownload = r(BoolSetting("download_fast", "Fast download", true))
    val autoResume = r(BoolSetting("download_auto_resume", "Auto resume", true))
    val subtitleLanguage = r(ChoiceSetting("download_subtitle", "Download subtitle", AppChoices.SUBTITLES_OFF, AppChoices.subtitleLanguages.map { it to it }))
    val musicQuality = r(ChoiceSetting("music_quality", "Music quality", AppChoices.DEFAULT_MUSIC_QUALITY, AppChoices.musicQualities.map { it to it }))
    val clipboardDetect = r(BoolSetting("clipboard_detect", "Copied link detection", true))
    val showHidden = r(BoolSetting("show_hidden", "Show hidden files", false))
    val downloadNotify = r(BoolSetting("download_notify", "Download complete notification", true))
    val downloadFolder = r(ChoiceSetting("download_folder", "Download folder", DownloadFolder.MEDIA.wire, DownloadFolder.entries.map { it.wire to it.wire }))

    // ---- Notifications ----
    val dailyPush = r(BoolSetting("notify_daily", "Daily push", false))
    val likeNotify = r(BoolSetting("notify_likes", "Likes", true))
    val commentNotify = r(BoolSetting("notify_comments", "Comments", false))
    val followNotify = r(BoolSetting("notify_follows", "Follows", true))
    val messageNotify = r(BoolSetting("notify_messages", "Messages", true))

    // ---- Playback ----
    val onlineQuality = r(ChoiceSetting("online_quality", "Online video quality", AppChoices.DEFAULT_ONLINE_QUALITY, AppChoices.onlineQualities.map { it to it }))
    val autoPlay = r(BoolSetting("auto_play", "Auto play", true))
    val autoPopup = r(BoolSetting("auto_popup", "Pop-up when leaving the player", true))

    // ---- Private folder ----
    val privatePasswordHash = r(TextSetting("private_password_hash", "Private password", ""))
}

/** Every registry stored in the app's settings file. */
val appSettingRegistries = listOf(PlayerKeys, AppKeys)
