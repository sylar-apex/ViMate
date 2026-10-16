package org.app.vimate.sharedlogic.model

/** Whether a file is shown under Videos or Music. Stored by [wire] value. */
enum class FileKind(val wire: String) {
    VIDEO("video"),
    MUSIC("music");

    companion object {
        fun fromWire(value: String?): FileKind = entries.firstOrNull { it.wire == value } ?: VIDEO
    }
}
