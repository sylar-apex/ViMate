package org.app.vimate.sharedlogic.logic

/** The private folder's passcode check. Files are hidden inside the app, not encrypted. */
object PrivateLock {
    const val PIN_LENGTH = 4
    private const val FNV_OFFSET = -3750763034362895579L
    private const val FNV_PRIME = 1099511628211L

    /** FNV-1a hash of [text], stored instead of the passcode itself. */
    fun hash(text: String): String {
        var hash = FNV_OFFSET
        text.forEach { char ->
            hash = hash xor char.code.toLong()
            hash *= FNV_PRIME
        }
        return hash.toULong().toString(16)
    }
}
