package org.app.vimate.sharedlogic.library

import kotlinx.serialization.Serializable
import org.app.vimate.sharedlogic.yt.Video

@Serializable
data class Playlist(val id: Long, val name: String, val videos: List<Video> = emptyList())

@Serializable
data class Channel(val id: String, val name: String, val avatar: String = "")

@Serializable
data class SiteVisit(val url: String, val title: String, val time: Long)
