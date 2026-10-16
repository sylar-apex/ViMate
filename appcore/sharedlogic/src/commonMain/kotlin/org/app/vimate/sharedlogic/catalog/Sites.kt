package org.app.vimate.sharedlogic.catalog

/** Home shortcut row; each tile opens the site in the browser. Names are brand names and stay untranslated. */
enum class BrandSite(val label: String, val url: String) {
    YOUTUBE("Youtube", "https://m.youtube.com/"),
    INSTAGRAM("Instagram", "https://www.instagram.com/"),
    FACEBOOK("Facebook", "https://m.facebook.com/"),
    GOOGLE("Google", "https://www.google.com/"),
}

/** Open-licence sources whose direct file links ViMate can download. */
enum class OpenSite(val label: String, val url: String) {
    ARCHIVE("Archive", "https://archive.org/details/movies"),
    COMMONS("Commons", "https://commons.wikimedia.org/wiki/Category:Videos"),
    PEXELS("Pexels", "https://www.pexels.com/videos/"),
    BLENDER("Blender", "https://studio.blender.org/films/");

    companion object {
        fun byName(name: String): OpenSite? = entries.firstOrNull { it.name == name }
    }
}

/** The url without its scheme and trailing slash, as shown under a site name. */
fun displayUrl(url: String): String = url.removePrefix("https://").trimEnd('/')
