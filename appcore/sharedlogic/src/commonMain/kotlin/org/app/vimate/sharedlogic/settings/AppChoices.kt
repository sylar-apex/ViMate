package org.app.vimate.sharedlogic.settings

/** The option lists offered by settings and sheets. */
object AppChoices {
    const val DEFAULT_LANGUAGE = "English"
    const val DEFAULT_LOCATION = "India"
    const val DEFAULT_TASKS = 2
    const val DEFAULT_MUSIC_QUALITY = "Medium"
    const val DEFAULT_ONLINE_QUALITY = "360p"
    const val SUBTITLES_OFF = "Off"
    const val SUBTITLES_ENGLISH = "English"

    val languages = listOf(
        "हिंदी", "English", "தமிழ்", "తెలుగు", "ਪੰਜਾਬੀ", "मराठी",
        "ગુજરાતી", "മലയാളം", "বাংলা", "ଓଡ଼ିଆ", "ಕನ್ನಡ", "অসমীয়া",
        "भोजपुरी", "हरयाणवी", "राजस्थानी",
    )

    val locations = listOf(
        "India", "United States", "United Kingdom", "Indonesia",
        "Brazil", "Mexico", "Nigeria", "Philippines", "Pakistan", "Bangladesh",
    )

    val taskLimits = listOf(1, 2, 3, 5)
    val musicQualities = listOf("Low", "Medium", "High")
    val onlineQualities = listOf("144p", "240p", "360p", "480p", "720p", "1080p")
    val subtitleLanguages = listOf(SUBTITLES_OFF, SUBTITLES_ENGLISH)

    /** Quality presets of the batch download screen. */
    val batchQualities = onlineQualities

    /** The value after [current] in [options], wrapping to the first. */
    fun <T> next(options: List<T>, current: T): T = options[(options.indexOf(current) + 1) % options.size]
}

/** Chips of the Home feed that are not genres. */
object FeedChip {
    const val ALL = "All"
    const val SHORTS = "Shorts"
}

/** Music tab sections; [wire] matches the catalog's section names. */
enum class MusicSection(val wire: String) {
    ALL("Music"),
    NEW_RELEASES("New releases"),
    CHARTS("Charts"),
    MOODS("Moods & genres"),
}

/** Where finished downloads are saved: the Movies/Music folder for their kind, or Download. */
enum class DownloadFolder(val wire: String) {
    MEDIA("Movies"),
    DOWNLOAD("Download");

    companion object {
        fun fromWire(value: String): DownloadFolder = entries.firstOrNull { it.wire == value } ?: MEDIA
    }
}
