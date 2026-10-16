package org.app.vimate.sharedlogic.platform

import android.app.Activity
import java.lang.ref.WeakReference
import org.app.vimate.sharedlogic.model.ImportedMedia

/**
 * Callbacks the activity registers so shared code can reach activity-only APIs: the document picker,
 * the permission prompt and widget pinning.
 */
object AndroidHost {
    var activity: WeakReference<Activity>? = null
    var importLauncher: (() -> Unit)? = null
    var mediaAccessLauncher: (() -> Unit)? = null
    var widgetPinner: ((HomeWidget) -> WidgetPinResult)? = null
    var widgetRefresher: (() -> Unit)? = null

    /** Set by [AndroidDeviceLibrary]; the activity calls it with the picked files. */
    var onImported: ((List<ImportedMedia>) -> Unit)? = null
}
