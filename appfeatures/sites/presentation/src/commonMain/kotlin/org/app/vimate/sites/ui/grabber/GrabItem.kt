package org.app.vimate.sites.ui.grabber

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.app.vimate.sites.logic.urlExt
import org.app.vimate.sites.logic.urlName
import org.app.vimate.sites.model.GrabCandidate
import org.app.vimate.sites.model.GrabType
import org.app.vimate.sites.model.ProbeResult

/** One grabber entry with the values probing fills in (POC `GrabItem`). */
@Stable
class GrabItem(val url: String, val type: GrabType, val variants: Int = 1) {
    var size by mutableLongStateOf(-1)
    var width by mutableIntStateOf(0)
    var height by mutableIntStateOf(0)
    var duration by mutableLongStateOf(0)
    /** Thumbnail bytes from the probe (fetched with the page's UA/Referer, so hotlink-protected images work). */
    var thumb by mutableStateOf<ByteArray?>(null)
    val ext get() = urlExt(url)
    val name get() = urlName(url)
    val resolution get() = if (width > 0 && height > 0) "${width}×$height" else ""

    fun apply(p: ProbeResult) {
        if (p.width > 0) { width = p.width; height = p.height }
        if (p.durationMs > 0) duration = p.durationMs
        if (p.size >= 0 && size < 0) size = p.size
        p.thumb?.let { thumb = it }
    }

    companion object {
        fun of(c: GrabCandidate) = GrabItem(c.url, c.type, c.variants)
    }
}
