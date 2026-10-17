package org.app.vimate.settings.domain.constant

/** Routes.SettingsGroup ids, as in the POC (the English group titles). */
object SettingsGroups {
    const val CONTENT = "Content"
    const val DOWNLOAD = "Download"
    const val PUSH = "Push Notifications"
    const val WIDGETS = "Widgets"
    const val OTHERS = "Others"
    const val ABOUT = "About Us"

    val MAX_TASKS = (1..5).map { "$it" }
    val CONNECTIONS = listOf("1", "2", "3", "4", "6", "8")
    val MUSIC_QUALITY = listOf("Low", "Medium", "High")
    val ONLINE_QUALITY = listOf("144p", "240p", "360p", "480p", "720p")
    val THEMES = listOf("system", "light", "dark")
    val LANGUAGES = listOf("English", "हिन्दी", "Bahasa Indonesia", "Português", "Español")
    val LOCATIONS = listOf("IN", "US", "GB", "ID", "BR", "PK")

    fun hlOf(language: String) = when (language) { "हिन्दी" -> "hi"; "Bahasa Indonesia" -> "id"; "Português" -> "pt"; "Español" -> "es"; else -> "en" }
}
