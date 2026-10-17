package org.app.vimate.sites.usecase

import org.app.vimate.sharedlogic.download.vid.VidDownloads
import org.app.vimate.sharedlogic.library.Library
import org.app.vimate.sharedlogic.platform.WebCookies
import org.app.vimate.sharedlogic.sites.Instagram
import org.app.vimate.sharedlogic.sites.MediaOption
import org.app.vimate.sharedlogic.sites.SiteMedia
import org.app.vimate.sites.logic.buildDirectTask
import org.app.vimate.sites.logic.buildSiteTask
import org.app.vimate.sites.model.GrabType

/** POC `enqueueDirect`. */
class EnqueueDirectUseCase(private val downloads: VidDownloads) {
    operator fun invoke(mediaUrl: String, title: String, page: String, userAgent: String? = null, grabType: GrabType? = null) =
        downloads.enqueue(buildDirectTask(mediaUrl, title, page, userAgent, grabType, WebCookies.get(mediaUrl)))
}

/** SiteDownloadSheet DOWNLOAD button. */
class EnqueueSiteMediaUseCase(private val downloads: VidDownloads) {
    operator fun invoke(media: SiteMedia, option: MediaOption, title: String) = downloads.enqueue(buildSiteTask(media, option, title))
}

/** Instagram post → its renditions. */
class ResolveInstagramPostUseCase {
    suspend operator fun invoke(code: String, userAgent: String): SiteMedia = Instagram.resolve(code, userAgent)
}

/** Browser history entry on page finish (POC `lib.addSite`). */
class AddSiteVisitUseCase(private val library: Library) {
    operator fun invoke(url: String, title: String) { library.addSite(url, title) }
}
