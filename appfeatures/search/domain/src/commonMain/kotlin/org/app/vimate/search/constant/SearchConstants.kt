package org.app.vimate.search.constant

/** POC `searchTabs`, in order; labels live in strings_search.xml. */
enum class SearchTab { All, Music, Playlist, Instagram, Facebook, Picture }

/** Hot search terms; the first [HOT_FIRE_COUNT] get the fire icon. */
val HOT_SEARCHES = listOf("bhojpuri song", "hindi song", "south movies", "ringtone", "sad song", "song")
const val HOT_FIRE_COUNT = 2
const val HISTORY_SHOWN = 12

/** Input treated as a link, not a query (POC submit regex). */
val URL_INPUT = Regex("""^(https?://|www\.)\S+$|^\S+\.(com|in|net|org|be|tv|me)(/\S*)?$""")
