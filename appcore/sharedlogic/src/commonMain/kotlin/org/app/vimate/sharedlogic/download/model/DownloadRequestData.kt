package org.app.vimate.sharedlogic.download.model

import org.app.vimate.sharedlogic.model.FileKind

/** What to download: a catalog rendition ([mediaId] set) or a pasted link. */
data class DownloadRequestData(
    val mediaId: String?,
    val title: String,
    val url: String,
    val kind: FileKind,
    val totalBytes: Long,
)

/** The answer of the queue. */
enum class EnqueueOutcome { ADDED, DUPLICATE }
