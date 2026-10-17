package org.app.vimate.sites.logic

/**
 * Settings › Sites Filter: hosts of well-known adult sites, blocked in the in-app browser while the filter is on,
 * and Google SafeSearch for searches typed into the app.
 */
object SiteFilter {
    private val adultHosts = setOf(
        "pornhub.com", "xvideos.com", "xnxx.com", "xhamster.com", "redtube.com", "youporn.com", "tube8.com", "spankbang.com",
        "eporner.com", "txxx.com", "hqporner.com", "porntrex.com", "tnaflix.com", "beeg.com", "motherless.com", "youjizz.com",
        "chaturbate.com", "stripchat.com", "bongacams.com", "livejasmin.com", "cam4.com", "onlyfans.com", "fansly.com",
        "brazzers.com", "realitykings.com", "xxx.com", "porn.com", "sex.com", "rule34.xxx", "nhentai.net", "hentaihaven.xxx",
    )

    /** True when [url]'s host is (a subdomain of) a listed adult site, or uses an adult-only top-level domain. */
    fun isAdult(url: String): Boolean {
        val host = url.substringAfter("://").substringBefore('/').substringBefore(':').lowercase().removePrefix("www.").removePrefix("m.")
        if (host.endsWith(".xxx") || host.endsWith(".porn") || host.endsWith(".sex") || host.endsWith(".adult")) return true
        return adultHosts.any { host == it || host.endsWith(".$it") }
    }

    /** Adds Google SafeSearch to a Google search URL. */
    fun safeSearch(url: String): String =
        if ("google." in url && "/search?" in url && "safe=" !in url) "$url&safe=active" else url
}
