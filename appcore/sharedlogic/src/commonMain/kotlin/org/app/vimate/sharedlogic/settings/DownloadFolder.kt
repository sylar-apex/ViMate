package org.app.vimate.sharedlogic.settings

/** Public folder downloads are saved under (download engine destination). */
enum class DownloadFolder(val wire: String) {
    MEDIA("Movies"),
    DOWNLOAD("Download");

    companion object {
        fun fromWire(value: String): DownloadFolder = entries.firstOrNull { it.wire == value } ?: MEDIA
    }
}
