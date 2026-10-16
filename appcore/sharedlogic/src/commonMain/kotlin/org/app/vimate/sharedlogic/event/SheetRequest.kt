package org.app.vimate.sharedlogic.event

/** Bottom sheets that any screen can open over the app. */
sealed interface SheetRequest {
    /** Choose quality and name, then download a catalog title ([mediaId]) or a direct link ([url]). */
    data class Download(val mediaId: String?, val url: String?) : SheetRequest
    data class Report(val mediaId: String) : SheetRequest
    data class ItemMenu(val mediaId: String) : SheetRequest
    data class AddToPlaylist(val mediaId: String) : SheetRequest
    data object CreatePlaylist : SheetRequest
}
