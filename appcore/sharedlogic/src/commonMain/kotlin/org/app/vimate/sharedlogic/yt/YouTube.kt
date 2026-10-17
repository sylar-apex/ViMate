package org.app.vimate.sharedlogic.yt

import com.psbapp.networking.client.getHttpClientEngine
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.decodeURLQueryComponent
import io.ktor.http.encodeURLParameter
import io.ktor.http.isSuccess
import kotlin.concurrent.Volatile
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
import org.app.kmediaplayer.util.nowMs
import org.app.vimate.sharedlogic.platform.WebCookies

/**
 * Client for YouTube's InnerTube JSON API (the endpoints the official apps call).
 * Browsing uses the WEB client; stream URLs come from the ANDROID_VR client, whose
 * responses carry direct (unciphered) googlevideo URLs.
 */
object YouTube {
    var hl = "en"
    var gl = "IN"
    /** Settings › Restricted Mode: YouTube's own safety filter on recommendations, search and pages. */
    var restricted = false

    val http: HttpClient = HttpClient(getHttpClientEngine()) {
        expectSuccess = false
        install(HttpTimeout) {
            connectTimeoutMillis = 20_000
            socketTimeoutMillis = 30_000
        }
    }
    private val json = Json { ignoreUnknownKeys = true }

    /** Deciphering resolver tried before the InnerTube clients (NewPipe on Android/desktop, none elsewhere). */
    var resolver: StreamResolver? = platformStreamResolver()

    private class Client(val name: String, val version: String, val id: Int, val ua: String, val host: String, val extra: Map<String, String> = emptyMap())

    private val WEB = Client("WEB", "2.20250925.01.00", 1,
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36",
        "www.youtube.com")
    private val REMIX = Client("WEB_REMIX", "1.20250922.03.00", 67, WEB.ua, "music.youtube.com")
    private val VR = Client("ANDROID_VR", "1.62.27", 28,
        "com.google.android.apps.youtube.vr.oculus/1.62.27 (Linux; U; Android 12L; eureka-user Build/SQ3A.220605.009.A1) gzip",
        "www.youtube.com",
        mapOf("deviceMake" to "Oculus", "deviceModel" to "Quest 3", "osName" to "Android", "osVersion" to "12L", "androidSdkVersion" to "32"))
    private val IOS = Client("IOS", "20.10.4", 5,
        "com.google.ios.youtube/20.10.4 (iPhone16,2; U; CPU iOS 18_3_2 like Mac OS X;)", "www.youtube.com",
        mapOf("deviceMake" to "Apple", "deviceModel" to "iPhone16,2", "osName" to "iPhone", "osVersion" to "18.3.2.22D82"))
    private val VR_OLD = Client("ANDROID_VR", "1.57.29", 28,
        "com.google.android.apps.youtube.vr.oculus/1.57.29 (Linux; U; Android 12L; eureka-user Build/SQ3A.220605.009.A1) gzip", "www.youtube.com",
        mapOf("deviceMake" to "Oculus", "deviceModel" to "Quest 3", "osName" to "Android", "osVersion" to "12L", "androidSdkVersion" to "32"))
    private val TV_EMBED = Client("TVHTML5_SIMPLY_EMBEDDED_PLAYER", "2.0", 85,
        "Mozilla/5.0 (PlayStation; PlayStation 4/12.00) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.4 Safari/605.1.15", "www.youtube.com")

    /** Player clients tried in order; YouTube's bot check differs per client and network. */
    private val playerClients = listOf(VR, IOS, VR_OLD, TV_EMBED)

    @Volatile private var visitorData: String? = null
    @Volatile var lastClient: String = ""
    /** User-Agent googlevideo expects for URLs from the client that answered last. */
    @Volatile var streamUA: String = VR.ua

    private suspend fun visitor(): String? = visitorData ?: runCatching {
        call("visitor_id", WEB) { emptyMap() }.path("responseContext", "visitorData").str
    }.getOrNull()?.also { visitorData = it }

    private suspend fun call(endpoint: String, client: Client, body: JsonObject.() -> Map<String, JsonElement>): JsonElement {
        val payload = buildJsonObject {
            putJsonObject("context") {
                putJsonObject("client") {
                    put("clientName", client.name)
                    put("clientVersion", client.version)
                    put("hl", hl)
                    put("gl", gl)
                    if (client !== WEB && client !== REMIX) visitorData?.let { put("visitorData", it) }
                    client.extra.forEach { (k, v) -> put(k, v) }
                }
                putJsonObject("user") { put("enableSafetyMode", restricted) }
            }
            JsonObject(emptyMap()).body().forEach { (k, v) -> put(k, v) }
        }
        val resp = http.post("https://${client.host}/youtubei/v1/$endpoint?prettyPrint=false") {
            header("User-Agent", client.ua)
            header("X-YouTube-Client-Name", client.id.toString())
            header("X-YouTube-Client-Version", client.version)
            header("Origin", "https://${client.host}")
            if (client === WEB) authHeaders().forEach { (k, v) -> header(k, v) }
            if (client !== WEB && client !== REMIX) visitorData?.let { header("X-Goog-Visitor-Id", it) }
            contentType(ContentType.Application.Json)
            setBody(payload.toString())
        }
        val text = resp.bodyAsText()
        if (!resp.status.isSuccess()) error("YouTube $endpoint HTTP ${resp.status.value}")
        return json.parseToJsonElement(text)
    }

    private fun args(vararg pairs: Pair<String, String>): JsonObject.() -> Map<String, JsonElement> =
        { pairs.associate { (k, v) -> k to JsonPrimitive(v) } }

    // ---- account (cookies from the in-app Google sign-in) ----

    fun cookies(): String = runCatching { WebCookies.get("https://www.youtube.com") }.getOrDefault("")
    val signedIn: Boolean get() = "SAPISID=" in cookies()

    /** Same scheme youtube.com uses: SAPISIDHASH <ts>_<sha1(ts SAPISID origin)>. */
    private fun authHeaders(): Map<String, String> {
        val c = cookies()
        val sapisid = Regex("(?:__Secure-3PAPISID|SAPISID)=([^;]+)").find(c)?.groupValues?.get(1) ?: return emptyMap()
        val ts = nowMs() / 1000
        val origin = "https://www.youtube.com"
        val hash = sha1Hex("$ts $sapisid $origin".encodeToByteArray())
        return mapOf("Cookie" to c, "Authorization" to "SAPISIDHASH ${ts}_$hash", "X-Origin" to origin, "X-Goog-AuthUser" to "0")
    }

    /** FEsubscriptions, FEhistory, VLWL (Watch later), FElibrary… for the signed-in account. */
    suspend fun accountFeed(browseId: String): List<Video> = parseVideos(call("browse", WEB, args("browseId" to browseId)))

    fun signOut() { WebCookies.removeAll() }

    // ---- search / feeds ----

    /** Search filter params (protobuf, base64) as used by the YouTube web client. */
    enum class Filter(val params: String?) { All(null), Videos("EgIQAQ%3D%3D"), Playlists("EgIQAw%3D%3D"), Short("EgIYAQ%3D%3D") }

    suspend fun search(query: String, filter: Filter = Filter.All, continuation: String? = null): Page<Video> {
        val r = if (continuation != null) call("search", WEB, args("continuation" to continuation))
        else call("search", WEB, args(*listOfNotNull("query" to query, filter.params?.let { "params" to it.decodeURLQueryComponent(plusIsSpace = true) }).toTypedArray()))
        return Page(parseVideos(r), r.findAll("continuationCommand").firstOrNull()?.get("token").str)
    }

    /** Home feed. Logged-out InnerTube home is empty, so feeds are driven by topic queries per chip. */
    suspend fun feed(topic: String, continuation: String? = null): Page<Video> = search(topic, Filter.Videos, continuation)

    suspend fun suggestions(query: String): List<String> {
        val url = "https://suggestqueries-clients6.youtube.com/complete/search?client=youtube&ds=yt&hl=$hl&gl=$gl&q=" +
            query.encodeURLParameter(spaceToPlus = true)
        return runCatching {
            val body = http.get(url).bodyAsText()
            val arr = json.parseToJsonElement(body.substringAfter('(').substringBeforeLast(')'))
            arr[1].arr.mapNotNull { it[0].str }
        }.getOrDefault(emptyList())
    }

    fun parseVideos(root: JsonElement): List<Video> {
        val out = LinkedHashMap<String, Video>()
        for (v in root.findAll("videoRenderer") + root.findAll("compactVideoRenderer") + root.findAll("gridVideoRenderer")) {
            val id = v["videoId"].str ?: continue
            out[id] = Video(
                id = id,
                title = v["title"].text.orEmpty(),
                channel = (v["ownerText"] ?: v["longBylineText"] ?: v["shortBylineText"]).text.orEmpty(),
                channelId = (v["ownerText"] ?: v["longBylineText"]).path("runs", 0, "navigationEndpoint", "browseEndpoint", "browseId").str.orEmpty(),
                channelAvatar = v.findFirst("channelThumbnailWithLinkRenderer")["thumbnail"].thumb
                    ?: v["channelThumbnail"].thumb.orEmpty(),
                duration = v["lengthText"].text.orEmpty(),
                views = (v["shortViewCountText"] ?: v["viewCountText"]).text.orEmpty(),
                published = v["publishedTimeText"].text.orEmpty(),
                thumbnail = v["thumbnail"].thumb ?: "https://i.ytimg.com/vi/$id/hqdefault.jpg",
            )
        }
        // Newer layout used for related videos and some feeds.
        for (l in root.findAll("lockupViewModel")) {
            if (l["contentType"].str != "LOCKUP_CONTENT_TYPE_VIDEO") continue
            val id = l["contentId"].str ?: continue
            val meta = l.path("metadata", "lockupMetadataViewModel")
            val rows = meta.path("metadata", "contentMetadataViewModel", "metadataRows").arr
            val parts = rows.flatMap { it["metadataParts"].arr }.mapNotNull { it.path("text", "content").str }
            if (id !in out) out[id] = Video(
                id = id,
                title = meta.path("title", "content").str.orEmpty(),
                channel = parts.getOrNull(0).orEmpty(),
                views = parts.getOrNull(1).orEmpty(),
                published = parts.getOrNull(2).orEmpty(),
                channelAvatar = meta.findFirst("avatarViewModel")["image"].thumb.orEmpty(),
                duration = l.findAll("thumbnailBadgeViewModel").firstOrNull()?.get("text").str.orEmpty(),
            )
        }
        // Shorts shelves
        for (s in root.findAll("shortsLockupViewModel")) {
            val id = s.path("onTap", "innertubeCommand", "reelWatchEndpoint", "videoId").str ?: continue
            if (id !in out) out[id] = Video(
                id = id,
                title = s.path("overlayMetadata", "primaryText", "content").str.orEmpty(),
                views = s.path("overlayMetadata", "secondaryText", "content").str.orEmpty(),
                thumbnail = "https://i.ytimg.com/vi/$id/oardefault.jpg",
            )
        }
        return out.values.toList()
    }

    // ---- watch page ----

    /** Stream info: NewPipe (deciphered, playable URLs) first, InnerTube clients as fallback. */
    suspend fun info(id: String): VideoInfo {
        val r = resolver ?: return innerTubeInfo(id)
        return runCatching { r.resolve(id) }.getOrElse { innerTubeInfo(id) }
    }

    private suspend fun innerTubeInfo(id: String): VideoInfo {
        visitor()
        var lastError = "Video unavailable"
        for ((n, c) in playerClients.withIndex()) {
            val p = runCatching { call("player", c, args("videoId" to id, "contentCheckOk" to "true", "racyCheckOk" to "true")) }
                .getOrElse { lastError = it.message ?: lastError; continue }
            val status = p.path("playabilityStatus", "status").str
            val hasUrls = p["streamingData"].let { sd -> (sd["formats"].arr + sd["adaptiveFormats"].arr).any { it["url"].str != null } || sd["hlsManifestUrl"].str != null }
            if (status == "OK" && hasUrls) { lastClient = "${c.name} ${c.version}"; streamUA = c.ua; return parseInfo(id, p) }
            lastError = p.path("playabilityStatus", "reason").str ?: "Video unavailable ($status)"
            // A fresh visitor id sometimes clears the bot check for the next client.
            if (n == 0) { visitorData = null; visitor() }
        }
        error(lastError)
    }

    private fun parseInfo(id: String, p: JsonElement): VideoInfo {
        val d = p["videoDetails"]
        val sd = p["streamingData"]
        fun stream(f: JsonElement): Stream? {
            val url = f["url"].str ?: return null
            val mime = f["mimeType"].str.orEmpty()
            val hasAudio = f["audioQuality"].str != null || mime.startsWith("audio")
            return Stream(
                itag = f["itag"].str?.toIntOrNull() ?: 0,
                url = url,
                mime = mime,
                bitrate = f["bitrate"].str?.toIntOrNull() ?: 0,
                sizeBytes = f["contentLength"].str?.toLongOrNull()
                    ?: ((f["approxDurationMs"].str?.toLongOrNull() ?: 0) * (f["bitrate"].str?.toLongOrNull() ?: 0) / 8000),
                height = f["height"].str?.toIntOrNull() ?: 0,
                qualityLabel = f["qualityLabel"].str.orEmpty(),
                audioSampleRate = f["audioSampleRate"].str?.toIntOrNull() ?: 0,
                hasVideo = mime.startsWith("video"),
                hasAudio = hasAudio,
            )
        }
        val muxed = sd["formats"].arr.mapNotNull(::stream)
        val adaptive = sd["adaptiveFormats"].arr.mapNotNull(::stream)
        val caps = p.path("captions", "playerCaptionsTracklistRenderer", "captionTracks").arr.mapNotNull {
            Caption(it["baseUrl"].str ?: return@mapNotNull null, it["languageCode"].str.orEmpty(), it["name"].text.orEmpty())
        }
        val vid = d["videoId"].str ?: id
        return VideoInfo(
            video = Video(
                id = vid,
                title = d["title"].str.orEmpty(),
                channel = d["author"].str.orEmpty(),
                channelId = d["channelId"].str.orEmpty(),
                views = d["viewCount"].str?.let { formatCount(it.toLong()) + " views" }.orEmpty(),
                duration = formatDuration(d["lengthSeconds"].str?.toLongOrNull() ?: 0),
                thumbnail = d["thumbnail"].thumb ?: "https://i.ytimg.com/vi/$vid/hqdefault.jpg",
            ),
            description = d["shortDescription"].str.orEmpty(),
            lengthSec = d["lengthSeconds"].str?.toLongOrNull() ?: 0,
            likes = "",
            muxed = muxed,
            videoOnly = adaptive.filter { it.hasVideo },
            audioOnly = adaptive.filter { !it.hasVideo },
            captions = caps,
            hlsUrl = sd["hlsManifestUrl"].str,
        )
    }

    data class Next(val related: List<Video>, val likes: String, val channelAvatar: String, val published: String, val commentsToken: String?, val commentCount: String)

    suspend fun next(id: String): Next {
        val r = call("next", WEB, args("videoId" to id))
        val likes = r.findAll("likeButtonViewModel").firstOrNull()
            .findFirst("toggleButtonViewModel").findFirst("buttonViewModel")?.get("title").str.orEmpty()
        val avatar = r.findFirst("videoOwnerRenderer")["thumbnail"].thumb.orEmpty()
        val published = r.findFirst("videoPrimaryInfoRenderer")["relativeDateText"].text.orEmpty()
        val section = r.findAll("itemSectionRenderer").firstOrNull { it["sectionIdentifier"].str == "comment-item-section" }
        val token = section.findFirst("continuationCommand")["token"].str
        val count = r.findAll("commentsEntryPointHeaderRenderer").firstOrNull()?.get("commentCount").text.orEmpty()
            .ifEmpty { r.findAll("engagementPanelTitleHeaderRenderer").firstOrNull { it["panelIdentifier"].str == "comment-item-section" || it["title"].text == "Comments" }?.get("contextualInfo").text.orEmpty() }
            .ifEmpty { r.findAll("engagementPanelTitleHeaderRenderer").mapNotNull { it["contextualInfo"].text }.firstOrNull().orEmpty() }
        return Next(parseVideos(r["contents"].path("twoColumnWatchNextResults", "secondaryResults") ?: r).filter { it.id != id },
            likes, avatar, published, token, count)
    }

    suspend fun comments(token: String): Page<Comment> {
        val r = call("next", WEB, args("continuation" to token))
        val list = r.findAll("commentEntityPayload").map { c ->
            val p = c["properties"]
            Comment(
                author = c.path("author", "displayName").str.orEmpty(),
                avatar = c.path("author", "avatarThumbnailUrl").str.orEmpty(),
                text = p.path("content", "content").str.orEmpty(),
                likes = c.path("toolbar", "likeCountNotliked").str.orEmpty(),
                published = p["publishedTime"].str.orEmpty(),
                replies = c.path("toolbar", "replyCount").str.orEmpty(),
            )
        }
        val next = r.findAll("continuationCommand").lastOrNull()?.get("token").str
        return Page(list, next?.takeIf { it != token })
    }

    data class PlaylistPage(val title: String, val owner: String, val videos: List<Video>)

    /** A YouTube playlist (browse id "VL" + list id). */
    private const val MAX_PLAYLIST = 5000

    /** First "load more" token; it sits directly in continuationCommand or one level down (innertubeCommand). */
    private fun nextToken(json: JsonElement): String? =
        json.findAll("continuationCommand").firstNotNullOfOrNull { it["token"].str ?: it.findFirst("token").str }

    suspend fun playlist(listId: String): PlaylistPage {
        val r = call("browse", WEB, args("browseId" to "VL$listId"))
        val header = r.findFirst("pageHeaderViewModel") ?: r.findFirst("playlistHeaderRenderer")
        val title = header?.get("title").text ?: header?.path("title", "dynamicTextViewModel", "text", "content").str ?: r.findFirst("microformatDataRenderer")?.get("title").str.orEmpty()
        val owner = r.findFirst("ownerText").text ?: r.findFirst("metadataRows")?.findFirst("content").str.orEmpty()
        // Older pages use playlistVideoRenderer, current ones lockup view models (parseVideos reads those).
        fun page(json: JsonElement): List<Video> = json.findAll("playlistVideoRenderer").mapNotNull { v ->
            val id = v["videoId"].str ?: return@mapNotNull null
            Video(id, v["title"].text.orEmpty(), channel = v["shortBylineText"].text.orEmpty(), duration = v["lengthText"].text.orEmpty(),
                thumbnail = v["thumbnail"].thumb ?: "https://i.ytimg.com/vi/$id/hqdefault.jpg")
        }.ifEmpty { parseVideos(json) }
        val vids = page(r).toMutableList()
        // YouTube returns ~100 videos per page; the first continuation token is the list's "load more".
        var token = nextToken(r)
        while (token != null && vids.size < MAX_PLAYLIST) {
            val more = runCatching { call("browse", WEB, args("continuation" to token!!)) }.getOrNull() ?: break
                val got = page(more).filter { v -> vids.none { it.id == v.id } }
            if (got.isEmpty()) break
            vids += got
            token = nextToken(more)
        }
        return PlaylistPage(title, owner, vids)
    }

    /** Uploads of a channel (used by Subscriptions). */
    suspend fun channelVideos(channelId: String): List<Video> {
        val r = call("browse", WEB, args("browseId" to channelId, "params" to "EgZ2aWRlb3PyBgQKAjoA"))
        return parseVideos(r)
    }

    // ---- YouTube Music ----

    enum class MusicTab(val label: String, val browseId: String) {
        Home("Music", "FEmusic_home"),
        NewReleases("New releases", "FEmusic_new_releases"),
        Charts("Charts", "FEmusic_charts"),
        Moods("Moods & genres", "FEmusic_moods_and_genres"),
    }

    suspend fun music(tab: MusicTab): List<Shelf> = parseMusicShelves(call("browse", REMIX, args("browseId" to tab.browseId)))

    suspend fun musicCollection(browseId: String, params: String? = null): Pair<String, List<Video>> {
        val r = call("browse", REMIX, args(*listOfNotNull("browseId" to browseId, params?.let { "params" to it }).toTypedArray()))
        val title = (r.findFirst("musicResponsiveHeaderRenderer") ?: r.findFirst("musicDetailHeaderRenderer"))["title"].text.orEmpty()
        val songs = parseMusicSongs(r)
        return title to songs
    }

    suspend fun searchMusic(query: String): List<Video> =
        parseMusicSongs(call("search", REMIX, args("query" to query, "params" to "EgWKAQIIAWoKEAkQBRAKEAMQBA==")))

    private fun parseMusicSongs(r: JsonElement): List<Video> = r.findAll("musicResponsiveListItemRenderer").mapNotNull { s ->
        val id = s.path("playlistItemData", "videoId").str
            ?: s.findFirst("watchEndpoint")?.get("videoId").str ?: return@mapNotNull null
        val cols = s["flexColumns"].arr.map { it.findFirst("text").text.orEmpty() }
        Video(id = id, title = cols.getOrNull(0).orEmpty(), channel = cols.getOrNull(1).orEmpty(),
            views = cols.getOrNull(2).orEmpty(), thumbnail = s["thumbnail"].findFirst("thumbnails")?.let { JsonObject(mapOf("thumbnails" to it)).thumb } ?: "https://i.ytimg.com/vi/$id/hqdefault.jpg")
    }.distinctBy { it.id }

    private fun parseMusicShelves(r: JsonElement): List<Shelf> {
        val shelves = mutableListOf<Shelf>()
        for (c in r.findAll("musicCarouselShelfRenderer") + r.findAll("gridRenderer")) {
            val title = (c.findFirst("musicCarouselShelfBasicHeaderRenderer") ?: c.findFirst("gridHeaderRenderer"))["title"].text.orEmpty()
            val songs = parseMusicSongs(c)
            val cols = c.findAll("musicTwoRowItemRenderer").mapNotNull { t ->
                val nav = t["navigationEndpoint"]
                val browseId = nav.path("browseEndpoint", "browseId").str
                val vid = nav.path("watchEndpoint", "videoId").str
                Collection(
                    browseId = browseId ?: vid?.let { "video:$it" } ?: return@mapNotNull null,
                    title = t["title"].text.orEmpty(),
                    subtitle = t["subtitle"].text.orEmpty(),
                    thumbnail = t.findFirst("thumbnails")?.let { JsonObject(mapOf("thumbnails" to it)).thumb }.orEmpty(),
                )
            } + c.findAll("musicNavigationButtonRenderer").mapNotNull { b ->
                val be = b.path("clickCommand", "browseEndpoint")
                Collection(be["browseId"].str ?: return@mapNotNull null, b["buttonText"].text.orEmpty(), playlistId = be["params"].str.orEmpty())
            }
            if (songs.isNotEmpty() || cols.isNotEmpty()) shelves += Shelf(title, songs, cols)
        }
        return shelves
    }

    // ---- helpers ----

    fun idFromUrl(url: String): String? {
        val m = Regex("""(?:v=|youtu\.be/|/shorts/|/embed/|/live/)([A-Za-z0-9_-]{11})""").find(url)
        return m?.groupValues?.get(1)
    }

    fun formatDuration(sec: Long): String {
        val h = sec / 3600; val m = (sec % 3600) / 60; val s = sec % 60
        return if (h > 0) "$h:${pad2(m)}:${pad2(s)}" else "$m:${pad2(s)}"
    }

    fun formatCount(n: Long): String = when {
        n >= 1_000_000_000 -> oneDecimal(n / 1e9) + "B"
        n >= 1_000_000 -> oneDecimal(n / 1e6) + "M"
        n >= 1_000 -> noDecimal(n / 1e3) + "K"
        else -> n.toString()
    }.replace(".0", "")
}
