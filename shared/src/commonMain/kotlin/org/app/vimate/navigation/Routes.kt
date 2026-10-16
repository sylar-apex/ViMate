package org.app.vimate.navigation

import kotlinx.serialization.Serializable

/** Every destination of the main window. The four tabs live inside [Tabs]. */
object Routes {
    @Serializable data object Tabs
    /** [autoPlay]: start the player too, when the Auto play setting is on. */
    @Serializable data object Downloads
    @Serializable data object Batch
    @Serializable data class Detail(val mediaId: String, val autoPlay: Boolean = true)
}
