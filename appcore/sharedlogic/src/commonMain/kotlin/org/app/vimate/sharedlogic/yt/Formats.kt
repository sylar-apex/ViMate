package org.app.vimate.sharedlogic.yt

import kotlin.math.floor

/** "%d" zero-padded to two digits. */
internal fun pad2(n: Long): String = n.toString().padStart(2, '0')

/** "%.1f" (half up). */
internal fun oneDecimal(x: Double): String {
    val t = floor(x * 10 + 0.5).toLong()
    return "${t / 10}.${t % 10}"
}

/** "%.0f" (half up). */
internal fun noDecimal(x: Double): String = floor(x + 0.5).toLong().toString()
