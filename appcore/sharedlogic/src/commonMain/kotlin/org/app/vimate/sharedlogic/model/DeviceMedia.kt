package org.app.vimate.sharedlogic.model

/** A video or audio file found in the device media library. */
data class DeviceMedia(
    val uri: String,
    val title: String,
    val bytes: Long,
    val format: String,
    val dateMs: Long,
    val durationMs: Long,
    val kind: FileKind,
)

/** A file the user picked through the system document picker. */
data class ImportedMedia(
    val title: String,
    val uri: String,
    val kind: FileKind,
    val bytes: Long,
)

/** Free and total bytes of the download volume. Zeros when unknown. */
data class StorageInfo(val freeBytes: Long, val totalBytes: Long) {
    val known: Boolean get() = totalBytes > 0
    val usedBytes: Long get() = totalBytes - freeBytes

    companion object {
        val Unknown = StorageInfo(0, 0)
    }
}
