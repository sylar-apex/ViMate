package org.app.vimate.sharedlogic.platform

/** Cookies of the in-app browser (Android CookieManager); empty on platforms without one. */
expect object WebCookies {
    /** The Cookie header value for [url], or "" when there are none. */
    fun get(url: String): String
    fun removeAll()
}
