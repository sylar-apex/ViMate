package org.app.vimate.sharedlogic.logic

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.app.vimate.sharedlogic.model.HistoryEntry
import org.app.vimate.sharedlogic.model.LocalFile
import org.app.vimate.sharedlogic.model.Playlist

class UserDataEditsTest {
    @Test
    fun historyMovesTheLatestWatchToTheFront() {
        val first = UserDataEdits.upsertHistory(emptyList(), "a", "u", 10, 1)
        val second = UserDataEdits.upsertHistory(first, "b", "u", 20, 2)
        val again = UserDataEdits.upsertHistory(second, "a", "u", 30, 3)
        assertEquals(listOf("a", "b"), again.map(HistoryEntry::mediaId))
        assertEquals(30, again.first().positionMs)
    }

    @Test
    fun playlistToggleAddsAndRemoves() {
        val playlist = Playlist("1", "Later", emptyList())
        val added = UserDataEdits.toggleInPlaylist(playlist, "open")
        val removed = UserDataEdits.toggleInPlaylist(added, "open")
        assertEquals(listOf("open"), added.mediaIds)
        assertTrue(removed.mediaIds.isEmpty())
    }

    @Test
    fun hiddenAndPrivateFilesStayOutUntilRequested() {
        val files = listOf(
            LocalFile("1", "Visible", "file://a", "video", 1),
            LocalFile("2", "Hidden", "file://b", "video", 1, hidden = true),
            LocalFile("3", "Private", "file://c", "video", 1, private = true),
        )
        assertEquals(listOf("1"), UserDataEdits.visibleFiles(files, showHidden = false, privateUnlocked = false).map { it.id })
        assertEquals(listOf("1", "2", "3"), UserDataEdits.visibleFiles(files, showHidden = true, privateUnlocked = true).map { it.id })
    }
}
