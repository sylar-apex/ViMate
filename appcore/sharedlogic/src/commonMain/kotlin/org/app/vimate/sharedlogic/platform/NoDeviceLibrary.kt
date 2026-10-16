package org.app.vimate.sharedlogic.platform

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.app.vimate.sharedlogic.model.DeviceMedia
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.model.ImportedMedia

/** For platforms without a shared media library (desktop, iOS, web). */
class NoDeviceLibrary : DeviceLibrary {
    override val available = false
    override val imports: SharedFlow<List<ImportedMedia>> = MutableSharedFlow()
    override fun hasAccess() = false
    override fun requestAccess() {}
    override suspend fun load(kind: FileKind): List<DeviceMedia> = emptyList()
}
