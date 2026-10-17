package org.app.vimate.sharedlogic.platform

import org.app.vimate.sharedlogic.model.StorageInfo

/** Things only the host platform can do: share sheet, clipboard, system notifications, widgets, cache. */
interface PlatformActions {
    /** Whether the system document picker can add device files to My Files. */
    val supportsImport: Boolean

    fun share(text: String, uri: String?)
    fun clipboardText(): String?
    fun notify(title: String, body: String)
    fun pinWidget(widget: HomeWidget): WidgetPinResult
    fun requestImport()

    /** Deletes cached download parts and temporary files; returns the bytes freed. */
    fun clearCache(): Long
    fun storageInfo(): StorageInfo

    /** Lets the user choose a download folder; returns its absolute path, or null when unsupported or cancelled. */
    fun pickFolder(): String? = null

    /** Asks the OS to refresh the home-screen widgets, after playback state changes. */
    fun refreshWidgets() {}

    /** True when the app may show windows over other apps (the auto popup player); always false without one. */
    fun canDrawOverlays(): Boolean = false

    /** Opens the system screen that grants [canDrawOverlays]. */
    fun requestOverlayPermission() {}
}

enum class HomeWidget { QUICK_ACTIONS, MUSIC }

enum class WidgetPinResult { CONFIRM_ON_HOME_SCREEN, ADD_BY_LONG_PRESS, UNSUPPORTED }
