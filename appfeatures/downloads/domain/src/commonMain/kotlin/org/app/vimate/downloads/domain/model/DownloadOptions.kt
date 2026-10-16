package org.app.vimate.downloads.domain.model

import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.model.Media

/** One quality on the download sheet. [key] is the rendition quality, or [DownloadOptions.LINK_KEY] for a link. */
data class QualityChoice(val key: String, val label: String, val format: String, val bytes: Long)

/** What the download sheet offers for a title or a link. */
data class DownloadOptions(
    val media: Media?,
    val url: String?,
    val kind: FileKind,
    val defaultName: String,
    val choices: List<QualityChoice>,
    val defaultChoice: String,
) {
    companion object {
        const val LINK_KEY = "link"
    }
}

/** The sheet's input when DOWNLOAD is tapped. */
data class DownloadForm(val mediaId: String?, val url: String?, val choice: String, val name: String)
