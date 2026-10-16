package org.app.vimate.downloads.ui.component

import androidx.compose.runtime.Composable
import org.app.vimate.appres.resources.Res
import org.app.vimate.appres.resources.state_canceled
import org.app.vimate.appres.resources.state_completed
import org.app.vimate.appres.resources.state_failed
import org.app.vimate.appres.resources.state_paused
import org.app.vimate.appres.resources.state_queued
import org.app.vimate.appres.resources.state_running
import org.app.vimate.sharedlogic.download.model.DownloadState
import org.jetbrains.compose.resources.stringResource

/** "running", "paused" and so on, as the Downloads page lists them. */
@Composable
internal fun DownloadState.label(): String = stringResource(
    when (this) {
        DownloadState.QUEUED -> Res.string.state_queued
        DownloadState.RUNNING -> Res.string.state_running
        DownloadState.PAUSED -> Res.string.state_paused
        DownloadState.COMPLETED -> Res.string.state_completed
        DownloadState.FAILED -> Res.string.state_failed
        DownloadState.CANCELED -> Res.string.state_canceled
    },
)
