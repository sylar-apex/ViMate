package org.app.vimate.sharedlogic.logic

import org.app.vimate.sharedlogic.model.Media
import org.app.vimate.sharedlogic.model.Rendition

/** Picks which encoded file of a title to stream or download. */
object Renditions {
    private val qualityOrder = listOf("144p", "180p", "240p", "360p", "480p", "540p", "720p", "1080p")
    private const val FALLBACK_QUALITY = "360p"
    private const val UNKNOWN_RANK = 99

    private fun rank(quality: String) = qualityOrder.indexOf(quality.lowercase())

    /** The best rendition at or below [preferred], else the lowest one. */
    fun pick(media: Media, preferred: String): Rendition {
        if (media.renditions.size == 1) return media.renditions.first()
        val pref = rank(preferred).let { if (it < 0) rank(FALLBACK_QUALITY) else it }
        val ranked = media.renditions.map { it to rank(it.quality).let { i -> if (i < 0) UNKNOWN_RANK else i } }
        val below = ranked.filter { it.second <= pref }.maxByOrNull { it.second }
        return below?.first ?: ranked.minBy { it.second }.first
    }

    /** The rendition named [quality], or [pick] with it as the preference. */
    fun exactOrPick(media: Media, quality: String): Rendition =
        media.renditions.firstOrNull { it.quality == quality } ?: pick(media, quality)

    fun sorted(media: Media): List<Rendition> = media.renditions.sortedBy { rank(it.quality) }

    fun topQuality(media: Media): String = sorted(media).lastOrNull()?.quality?.uppercase().orEmpty()
}
