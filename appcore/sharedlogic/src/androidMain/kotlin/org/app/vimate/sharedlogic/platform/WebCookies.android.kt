package org.app.vimate.sharedlogic.platform

import android.webkit.CookieManager

/** Cookies of the in-app WebView (Google / Instagram sign-in). */
actual object WebCookies {
    actual fun get(url: String): String = runCatching { CookieManager.getInstance().getCookie(url).orEmpty() }.getOrDefault("")
    actual fun removeAll() { CookieManager.getInstance().removeAllCookies(null) }
}
