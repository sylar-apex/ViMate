package org.app.vimate.sharedlogic.platform

/** No in-app browser cookie jar on this platform. */
actual object WebCookies {
    actual fun get(url: String): String = ""
    actual fun removeAll() {}
}
