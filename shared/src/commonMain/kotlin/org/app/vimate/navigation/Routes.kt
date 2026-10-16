package org.app.vimate.navigation

import kotlinx.serialization.Serializable

/** Every destination of the main window. The four tabs live inside [Tabs]. */
object Routes {
    @Serializable data object Tabs
}
