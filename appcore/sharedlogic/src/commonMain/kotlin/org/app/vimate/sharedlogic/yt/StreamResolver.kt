package org.app.vimate.sharedlogic.yt

/**
 * Deciphering stream resolver (NewPipeExtractor on Android and desktop). [YouTube.info] tries it first and
 * falls back to the InnerTube player clients when it fails or when the platform has none (iOS, web).
 */
interface StreamResolver {
    suspend fun resolve(videoId: String): VideoInfo
}

/** The platform's resolver, or null where NewPipeExtractor cannot run. */
internal expect fun platformStreamResolver(): StreamResolver?
