package org.app.vimate.sharedlogic.sites

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import org.app.vimate.sharedlogic.platform.WebCookies
import org.app.vimate.sharedlogic.yt.YouTube
import org.app.vimate.sharedlogic.yt.arr
import org.app.vimate.sharedlogic.yt.get
import org.app.vimate.sharedlogic.yt.str

/** One downloadable rendition offered in the site download sheet. */
data class MediaOption(val section: String, val label: String, val ext: String, val sizeBytes: Long, val url: String)

data class SiteMedia(val title: String, val thumbnail: String, val options: List<MediaOption>, val headers: Map<String, String>, val pageUrl: String)

/**
 * Instagram post resolver using the logged-in web session (cookies from the in-app browser).
 * Calls the same media-info endpoint instagram.com uses and lists every rendition:
 * progressive video_versions, DASH video/audio representations, and images.
 */
object Instagram {
    private const val APP_ID = "936619743392459"
    private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_"
    private val json = Json { ignoreUnknownKeys = true }

    /** Base-64 shortcode to the decimal media id (arbitrary precision, as BigInteger in the POC). */
    fun shortcodeToId(code: String): String {
        var digits = intArrayOf(0) // little-endian decimal digits
        for (c in code.take(11)) {
            var carry = ALPHABET.indexOf(c).toLong()
            val next = ArrayList<Int>(digits.size + 3)
            for (d in digits) {
                val v = d * 64L + carry
                next += (v % 10).toInt(); carry = v / 10
            }
            while (carry > 0) { next += (carry % 10).toInt(); carry /= 10 }
            digits = next.toIntArray()
        }
        return digits.reversed().joinToString("").trimStart('0').ifEmpty { "0" }
    }

    fun shortcode(url: String): String? = Regex("""instagram\.com/(?:[\w.]+/)?(?:p|reel|reels|tv)/([A-Za-z0-9_-]+)""").find(url)?.groupValues?.get(1)

    suspend fun resolve(shortcode: String, userAgent: String, index: Int = 0): SiteMedia {
        val cookies = WebCookies.get("https://www.instagram.com")
        val csrf = Regex("""csrftoken=([^;]+)""").find(cookies)?.groupValues?.get(1).orEmpty()
        val headers = mapOf("User-Agent" to userAgent, "Cookie" to cookies, "X-IG-App-ID" to APP_ID, "X-CSRFToken" to csrf,
            "X-Requested-With" to "XMLHttpRequest", "Referer" to "https://www.instagram.com/")
        val r = YouTube.http.get("https://www.instagram.com/api/v1/media/${shortcodeToId(shortcode)}/info/") {
            headers.forEach { (k, v) -> header(k, v) }
        }
        val code = r.status.value
        if (!r.status.isSuccess()) error(if (code == 401 || code == 403) "Log in to Instagram first" else "Instagram HTTP $code")
        val root = json.parseToJsonElement(r.bodyAsText())
        val item = root["items"][0] ?: error("Post not found")
        val media = item["carousel_media"].arr.getOrNull(index) ?: item
        val owner = item["user"]["username"].str.orEmpty()
        val pk = media["pk"].str ?: shortcode
        val options = mutableListOf<MediaOption>()

        // DASH manifest: separate video renditions + the audio track.
        media["video_dash_manifest"].str?.let { mpd ->
            Regex("""<AdaptationSet[^>]*contentType="(audio|video)"[^>]*>(.*?)</AdaptationSet>""", RegexOption.DOT_MATCHES_ALL).findAll(mpd).forEach { set ->
                val type = set.groupValues[1]
                Regex("""<Representation([^>]*)>.*?<BaseURL>([^<]+)</BaseURL>""", RegexOption.DOT_MATCHES_ALL).findAll(set.groupValues[2]).forEach { rep ->
                    val attrs = rep.groupValues[1]
                    val url = rep.groupValues[2].replace("&amp;", "&")
                    val size = Regex("""FBContentLength="(\d+)"""").find(attrs)?.groupValues?.get(1)?.toLongOrNull() ?: 0
                    if (type == "audio") options += MediaOption("Music", "AUDIO", "m4a", size, url)
                    else {
                        val w = Regex("""width="(\d+)"""").find(attrs)?.groupValues?.get(1)
                        val h = Regex("""height="(\d+)"""").find(attrs)?.groupValues?.get(1)
                        if (w != null && h != null) options += MediaOption("Video (no sound)", "${w}X$h", "mp4", size, url)
                    }
                }
            }
        }
        // Progressive versions carry sound; these are what VidMate lists under Video.
        media["video_versions"].arr.forEach { v ->
            val w = v["width"].str; val h = v["height"].str; val url = v["url"].str ?: return@forEach
            options += MediaOption("Video", "${w}X$h", "mp4", 0, url)
        }
        if (media["video_versions"].arr.isEmpty()) media["image_versions2"]["candidates"].arr.take(3).forEach { c ->
            options += MediaOption("Picture", "${c["width"].str}X${c["height"].str}", "jpg", 0, c["url"].str ?: return@forEach)
        }
        val video = options.filter { it.section == "Video" }.distinctBy { it.label }.sortedBy { it.label.substringBefore('X').toIntOrNull() ?: 0 }
        val ordered = options.filter { it.section == "Music" }.distinctBy { it.label } + video +
            options.filter { it.section == "Picture" }.distinctBy { it.label }
        return SiteMedia("instagram_$pk${if (owner.isNotEmpty()) "" else ""}", media["image_versions2"]["candidates"][0]["url"].str.orEmpty(),
            ordered.ifEmpty { options }, headers - "X-IG-App-ID" - "X-CSRFToken" - "X-Requested-With", "https://www.instagram.com/p/$shortcode/")
    }
}
