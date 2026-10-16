package org.app.vimate.sharedlogic.platform

import kotlinx.coroutines.flow.SharedFlow
import org.app.vimate.sharedlogic.model.DeviceMedia
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.model.ImportedMedia

/** The phone's own video and music library, and files picked through the system picker. */
interface DeviceLibrary {
    /** False on platforms without a shared media library. */
    val available: Boolean

    /** Files the user picked with [PlatformActions.requestImport]. */
    val imports: SharedFlow<List<ImportedMedia>>

    fun hasAccess(): Boolean
    fun requestAccess()

    /** Newest first. Empty without access. */
    suspend fun load(kind: FileKind): List<DeviceMedia>
}
