package org.app.vimate.shorts.repository

import org.app.vimate.sharedlogic.yt.Page
import org.app.vimate.sharedlogic.yt.Video
import org.app.vimate.sharedlogic.yt.VideoInfo
import org.app.vimate.sharedlogic.yt.YouTube

interface ShortsRepository {
    suspend fun page(query: String, continuation: String?): Page<Video>
    suspend fun info(id: String): VideoInfo
}

class ShortsRepositoryImpl : ShortsRepository {
    override suspend fun page(query: String, continuation: String?) = YouTube.search(query, YouTube.Filter.Short, continuation)
    override suspend fun info(id: String) = YouTube.info(id)
}
