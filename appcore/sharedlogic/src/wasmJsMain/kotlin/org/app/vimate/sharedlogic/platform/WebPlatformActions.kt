package org.app.vimate.sharedlogic.platform

import org.app.vimate.sharedlogic.model.StorageInfo

@JsFun("(text) => { if (navigator.share) { navigator.share({ text: text }).catch(() => {}); } else if (navigator.clipboard) { navigator.clipboard.writeText(text).catch(() => {}); } }")
private external fun webShare(text: String)

/** Browsers share through the Web Share API, falling back to copying the text. */
class WebPlatformActions : PlatformActions {
    override val supportsImport = false

    override fun share(text: String, uri: String?) = webShare(listOfNotNull(text, uri).joinToString("\n"))

    /** Reading the clipboard needs a user gesture and a permission prompt, so copied-link detection is off on the web. */
    override fun clipboardText(): String? = null

    override fun notify(title: String, body: String) {}

    override fun pinWidget(widget: HomeWidget) = WidgetPinResult.UNSUPPORTED

    override fun requestImport() {}

    override fun clearCache(): Long = 0

    override fun storageInfo(): StorageInfo = StorageInfo.Unknown
}
