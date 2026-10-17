package org.app.vimate.sites.ui.web

import javafx.application.Platform

/**
 * Starts JavaFX (used by the in-app browser) once, before the window opens. Starting it lazily from
 * inside Compose pumps the AWT event queue mid-frame and crashes ("Reentry into ignoringRedrawRequests").
 */
object DesktopWebRuntime {
    fun start() {
        runCatching { Platform.startup {} }
        Platform.setImplicitExit(false)
    }
}
