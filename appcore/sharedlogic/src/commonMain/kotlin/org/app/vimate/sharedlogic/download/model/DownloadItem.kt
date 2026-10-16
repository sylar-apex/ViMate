package org.app.vimate.sharedlogic.download.model

import org.app.vimate.sharedlogic.model.FileKind

/** Where a download stands. */
enum class DownloadState {
    QUEUED, RUNNING, PAUSED, COMPLETED, FAILED, CANCELED;

    val isActive: Boolean get() = this == QUEUED || this == RUNNING || this == PAUSED
    val canPause: Boolean get() = this == QUEUED || this == RUNNING
    val canResume: Boolean get() = this == PAUSED || this == FAILED
}

/** One row of the download list. [mediaId] is null for pasted links. */
data class DownloadItem(
    val id: String,
    val mediaId: String?,
    val title: String,
    val url: String,
    val kind: FileKind,
    val state: DownloadState,
    val progress: Float,
    val bytes: Long,
    val totalBytes: Long,
    val error: String?,
    val localUri: String?,
    val speedBytesPerSec: Long,
)
