package org.app.vimate.sharedlogic.event

/** Places outside the current screen that app code can ask the shell to open. */
enum class AppRoute(val wire: String) {
    HOME("home"),
    MUSIC("music"),
    FILES("files"),
    FILES_PRIVATE("files_private"),
    ME("me"),
    DOWNLOADS("downloads"),
    VIDEO("video");

    companion object {
        fun fromWire(value: String?): AppRoute? = entries.firstOrNull { it.wire == value }
    }
}
