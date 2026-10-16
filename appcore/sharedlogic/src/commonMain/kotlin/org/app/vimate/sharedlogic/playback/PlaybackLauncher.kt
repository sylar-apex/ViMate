package org.app.vimate.sharedlogic.playback

import org.app.kmediaplayer.KMediaPlayer
import org.app.kmediaplayer.model.MediaFile
import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.sharedlogic.data.CatalogRepository
import org.app.vimate.sharedlogic.data.UserDataRepository
import org.app.vimate.sharedlogic.logic.LinkClassifier
import org.app.vimate.sharedlogic.logic.Renditions
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.model.Media
import org.app.vimate.sharedlogic.settings.AppChoices
import org.app.vimate.sharedlogic.settings.AppKeys

/**
 * Starts titles in the media player library: video in its full player, music in the background with
 * the mini player. Prefers a downloaded copy over streaming and picks the quality from settings.
 */
class PlaybackLauncher(
    private val catalog: CatalogRepository,
    private val userData: UserDataRepository,
    private val settings: ObservableSettings,
) {
    /** Plays [media]; with a [queue], next and previous move through it. */
    fun play(media: Media, queue: List<Media> = listOf(media)) {
        val list = queue.ifEmpty { listOf(media) }
        val files = list.map(::toMediaFile)
        val index = list.indexOfFirst { it.id == media.id }.coerceAtLeast(0)
        if (media.isMusic) KMediaPlayer.playMusic(files, index) else KMediaPlayer.playVideo(files, index)
    }

    fun play(mediaId: String, queueIds: List<String> = listOf(mediaId)) {
        val media = catalog.media(mediaId) ?: return
        play(media, queueIds.mapNotNull(catalog::media))
    }

    /** Plays a file by uri (download, import or device library) that may not be in the catalog. */
    fun playLocal(uri: String, title: String, kind: FileKind, durationMs: Long = 0, bytes: Long = 0) {
        play(catalog.registerLocal(uri, title, kind, durationMs, bytes))
    }

    /** The url the player opens for [media]: its downloaded copy, else the rendition for the quality setting. */
    fun sourceOf(media: Media): String {
        userData.value.files.firstOrNull { it.mediaId == media.id && !it.private }?.let { return it.uri }
        val preferred = if (media.isMusic) settings[AppKeys.musicQuality] else settings[AppKeys.onlineQuality]
        return Renditions.pick(media, preferred).url
    }

    /** The subtitle file of the title playing [uri], when it has one and subtitles are on in settings. */
    fun subtitleFor(uri: String): Pair<String, String>? {
        if (settings[AppKeys.subtitleLanguage] == AppChoices.SUBTITLES_OFF) return null
        val media = catalog.mediaForUri(uri) ?: userData.value.files.firstOrNull { it.uri == uri }?.mediaId?.let(catalog::media) ?: return null
        val vtt = media.subtitleVtt ?: return null
        val name = "${media.id}.vtt"
        return SubtitleCache.write(name, vtt)?.let { it to name }
    }

    /** The player files for [media], for an inline player. */
    fun filesFor(media: Media): List<MediaFile> = listOf(toMediaFile(media))

    private fun toMediaFile(media: Media): MediaFile {
        val uri = sourceOf(media)
        return MediaFile(
            id = uri.hashCode().toLong(),
            uri = uri,
            path = "",
            name = media.title + "." + LinkClassifier.container(uri).lowercase(),
            sizeBytes = Renditions.sorted(media).firstOrNull { it.url == uri }?.bytes ?: 0,
            durationMs = media.durationMs,
            dateModifiedMs = 0,
            isAudio = media.isMusic,
            artist = media.creator,
        )
    }
}
