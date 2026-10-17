package org.app.vimate.sharedlogic.settings

import org.app.kmediaplayer.settings.BoolSetting
import org.app.kmediaplayer.settings.IntSetting
import org.app.kmediaplayer.settings.PlayerKeys
import org.app.kmediaplayer.settings.SettingRegistry
import org.app.kmediaplayer.settings.TextSetting

/**
 * Settings shown under ME › Settings, mirroring VidMate's groups (POC data/Stores.kt Settings + Settings.K):
 * same keys and defaults. Read with `settings[AppKeys.x]`, write with `settings[AppKeys.x] = v`.
 */
object AppKeys : SettingRegistry() {
    // Download
    val wifiOnly = r(BoolSetting("wifi_only", "Download only on Wi-Fi", false))
    val maxTasks = r(IntSetting("max_tasks", "Max download tasks", 2, 1..5))
    val connections = r(IntSetting("connections", "Fast Download Option", 3, 1..8))
    val autoResume = r(BoolSetting("auto_resume", "Auto resume", true))
    val completeNotify = r(BoolSetting("complete_notify", "Download complete notification", true))
    val downloadSubtitle = r(BoolSetting("dl_subtitle", "Download subtitle", false))
    val musicQuality = r(TextSetting("music_quality", "Music quality", "Medium"))
    val clipboardDetect = r(BoolSetting("clipboard", "Copied Link Detection", true))
    val showHidden = r(BoolSetting("show_hidden", "Show hidden files", false))
    val smartMuxer = r(BoolSetting("smart_muxer", "Smart muxer", true))
    val downloadFolder = r(TextSetting("folder", "Download folder", "VidMate"))
    // Content
    val language = r(TextSetting("language", "Language", "English"))
    val location = r(TextSetting("location", "Location", "IN"))
    val restricted = r(BoolSetting("restricted", "Restricted mode", false))
    val hideAdultSites = r(BoolSetting("hide_adult", "Hide adult sites", true))
    // Others
    val autoPlay = r(BoolSetting("autoplay", "Auto play", true))
    val autoPopup = r(BoolSetting("auto_popup", "Auto pop-up", false))
    val onlineQuality = r(TextSetting("online_quality", "Online video quality", "360p"))
    val quickSearch = r(BoolSetting("quick_search", "Quick search", false))
    /** "system", "light" or "dark" (POC Settings.dark). */
    val theme = r(TextSetting("theme", "Theme", "system"))
    // Push
    val push = r(BoolSetting("push", "Push", true))
    val dailyPush = r(BoolSetting("daily_push", "Daily push", true))
    // Private folder
    val privatePin = r(TextSetting("private_pin", "Private password", ""))
}

/** Every registry stored in the app's settings file. */
val appSettingRegistries = listOf(PlayerKeys, AppKeys)
