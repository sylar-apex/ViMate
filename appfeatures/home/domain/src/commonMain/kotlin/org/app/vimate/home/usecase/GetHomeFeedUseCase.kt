package org.app.vimate.home.usecase

import org.app.vimate.sharedlogic.yt.Page
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.YouTube

/** One page of the feed for a chip topic (POC `YouTube.feed`). */
class GetHomeFeedUseCase(private val youTube: YouTube) {
    suspend operator fun invoke(topic: String, continuation: String?): Page<Video> = youTube.feed(topic, continuation)
}
