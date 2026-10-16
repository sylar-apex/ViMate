package org.app.vimate.sharedlogic.usecase

import org.app.kmediaplayer.settings.ObservableSettings
import org.app.vimate.sharedlogic.model.Media
import org.app.vimate.sharedlogic.playback.PlaybackLauncher
import org.app.vimate.sharedlogic.settings.AppKeys

/** Plays a catalog title, optionally inside a queue of other titles. */
class PlayMediaUseCase(private val launcher: PlaybackLauncher) {
    operator fun invoke(mediaId: String, queueIds: List<String> = listOf(mediaId)) = launcher.play(mediaId, queueIds)
}

/** Whether a detail page opened from a list starts its video right away (the Auto play setting). */
class ShouldAutoPlayUseCase(private val settings: ObservableSettings) {
    operator fun invoke(): Boolean = settings[AppKeys.autoPlay]
}

/** The files an inline player opens for a title: the downloaded copy, else the stream for the quality setting. */
class GetPlayerFilesUseCase(private val launcher: PlaybackLauncher) {
    operator fun invoke(media: Media) = launcher.filesFor(media)
}
