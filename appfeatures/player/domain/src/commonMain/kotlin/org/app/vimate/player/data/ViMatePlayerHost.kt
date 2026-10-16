package org.app.vimate.player.data

import org.app.kmediaplayer.host.PlayerHost
import org.app.kmediaplayer.model.MediaFile
import org.app.kmediaplayer.model.PlaybackRecord
import org.app.kmediaplayer.model.SubtitleRef
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.kmediaplayer.settings.PlayerKeys
import org.app.kmediaplayer.util.nowMs
import org.app.vimate.sharedlogic.data.CatalogRepository
import org.app.vimate.sharedlogic.data.UserDataRepository
import org.app.vimate.sharedlogic.logic.UserDataEdits
import org.app.vimate.sharedlogic.playback.PlaybackLauncher

/** Connects the media player library to ViMate's watch history, catalog titles and subtitles. */
class ViMatePlayerHost(
    private val userData: UserDataRepository,
    private val catalog: CatalogRepository,
    private val launcher: PlaybackLauncher,
    private val settings: ObservableSettings,
) : PlayerHost {
    override val appName: String = "ViMate"

    /** Where [uri] stopped last time, so the player resumes there. */
    override fun playbackRecord(uri: String): PlaybackRecord? {
        val entry = userData.value.history.firstOrNull { it.uri == uri } ?: return null
        return PlaybackRecord(positionMs = entry.positionMs, lastPlayedMs = entry.updatedAt)
    }

    override fun recordPlayback(file: MediaFile, positionMs: Long, durationMs: Long) {
        if (settings[PlayerKeys.resume] == RESUME_OFF) return
        val mediaId = catalog.mediaForUri(file.uri)?.id ?: userData.value.files.firstOrNull { it.uri == file.uri }?.mediaId ?: return
        userData.update { it.copy(history = UserDataEdits.upsertHistory(it.history, mediaId, file.uri, positionMs, nowMs())) }
    }

    override fun subtitleFiles(file: MediaFile): List<SubtitleRef> =
        listOfNotNull(launcher.subtitleFor(file.uri)?.let { (uri, name) -> SubtitleRef(uri, name) })

    private companion object {
        /** [PlayerKeys.resume] value that turns resuming off. */
        const val RESUME_OFF = "start_over"
    }
}
