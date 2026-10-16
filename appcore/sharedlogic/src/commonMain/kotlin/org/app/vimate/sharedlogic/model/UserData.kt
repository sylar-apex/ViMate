package org.app.vimate.sharedlogic.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Profile(val name: String)

/** Where playback of [mediaId] stopped; [uri] is the file or stream the player opened. */
@Serializable
data class HistoryEntry(
    val mediaId: String,
    val positionMs: Long,
    val updatedAt: Long,
    val uri: String = "",
)

@Serializable
data class Playlist(
    val id: String,
    val name: String,
    val mediaIds: List<String>,
)

/** A finished download or an imported file, as listed under My Files. */
@Serializable
data class LocalFile(
    val id: String,
    val title: String,
    val uri: String,
    val kind: String,
    val bytes: Long,
    val hidden: Boolean = false,
    val private: Boolean = false,
    val mediaId: String? = null,
) {
    val fileKind: FileKind get() = FileKind.fromWire(kind)
}

/** An in-app notification shown on the Notifications screen. */
@Serializable
data class Note(
    val id: String,
    val title: NoteTitle,
    val body: String,
    val createdAt: Long,
)

/** The heading of a [Note]; the screen shows the matching text. */
@Serializable
enum class NoteTitle { DOWNLOAD_COMPLETE, SAVED, THANKS }

@Serializable
data class Report(
    val mediaId: String,
    val reason: ReportReason,
    val createdAt: Long,
)

@Serializable
enum class ReportReason { SEXUAL, VIOLENT, HATEFUL, HARMFUL, SPAM, CHILD_ABUSE }

/** Everything the user created on this device, stored as one json document. */
@Serializable
data class UserData(
    val profile: Profile? = null,
    val history: List<HistoryEntry> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val files: List<LocalFile> = emptyList(),
    val subscriptions: List<String> = emptyList(),
    val likes: List<String> = emptyList(),
    val feedback: List<String> = emptyList(),
    val notifications: List<Note> = emptyList(),
    val basket: List<String> = emptyList(),
    val watchLater: List<String> = emptyList(),
    val notInterested: List<String> = emptyList(),
    val reports: List<Report> = emptyList(),
) {
    fun encode(): String = json.encodeToString(serializer(), this)

    companion object {
        private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

        fun decode(raw: String?): UserData =
            raw?.let { runCatching { json.decodeFromString(serializer(), it) }.getOrNull() } ?: UserData()
    }
}
