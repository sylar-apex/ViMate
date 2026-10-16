package org.app.vimate.sharedlogic.logic

import org.app.vimate.sharedlogic.model.HistoryEntry
import org.app.vimate.sharedlogic.model.LocalFile
import org.app.vimate.sharedlogic.model.Playlist

/** Pure edits of the user's records. */
object UserDataEdits {
    /** Moves [mediaId] to the top of the history with its new position. */
    fun upsertHistory(list: List<HistoryEntry>, mediaId: String, uri: String, positionMs: Long, now: Long): List<HistoryEntry> =
        listOf(HistoryEntry(mediaId, positionMs, now, uri)) + list.filter { it.mediaId != mediaId }

    fun toggleInPlaylist(playlist: Playlist, mediaId: String): Playlist {
        val ids = if (mediaId in playlist.mediaIds) playlist.mediaIds - mediaId else playlist.mediaIds + mediaId
        return playlist.copy(mediaIds = ids)
    }

    fun visibleFiles(files: List<LocalFile>, showHidden: Boolean, privateUnlocked: Boolean): List<LocalFile> =
        files.filter { (showHidden || !it.hidden) && (privateUnlocked || !it.private) }

    /** Adds [item] when absent, removes it when present. */
    fun <T> List<T>.toggle(item: T): List<T> = if (item in this) this - item else this + item
}
