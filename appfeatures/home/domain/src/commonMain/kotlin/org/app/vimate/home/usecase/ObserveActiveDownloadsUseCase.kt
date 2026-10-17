package org.app.vimate.home.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.app.vimate.sharedlogic.download.vid.TaskState
import org.app.vimate.sharedlogic.download.vid.VidDownloads

/** Count of downloads not Done/Failed (POC search-bar badge). */
class ObserveActiveDownloadsUseCase(private val downloads: VidDownloads) {
    operator fun invoke(): Flow<Int> = downloads.tasks.map { t -> t.count { it.state != TaskState.Done && it.state != TaskState.Failed } }
}
