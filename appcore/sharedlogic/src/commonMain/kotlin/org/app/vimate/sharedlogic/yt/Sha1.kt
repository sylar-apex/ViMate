package org.app.vimate.sharedlogic.yt

/** SHA-1 hex digest (used for the SAPISIDHASH auth header). */
internal fun sha1Hex(input: ByteArray): String {
    var h0 = 0x67452301; var h1 = 0xEFCDAB89.toInt(); var h2 = 0x98BADCFE.toInt(); var h3 = 0x10325476; var h4 = 0xC3D2E1F0.toInt()
    val ml = input.size.toLong() * 8
    val padLen = ((56 - (input.size + 1) % 64) + 64) % 64
    val msg = ByteArray(input.size + 1 + padLen + 8)
    input.copyInto(msg)
    msg[input.size] = 0x80.toByte()
    for (i in 0 until 8) msg[msg.size - 1 - i] = (ml ushr (8 * i)).toByte()
    val w = IntArray(80)
    for (chunk in msg.indices step 64) {
        for (i in 0 until 16) {
            val j = chunk + i * 4
            w[i] = ((msg[j].toInt() and 0xff) shl 24) or ((msg[j + 1].toInt() and 0xff) shl 16) or
                ((msg[j + 2].toInt() and 0xff) shl 8) or (msg[j + 3].toInt() and 0xff)
        }
        for (i in 16 until 80) w[i] = (w[i - 3] xor w[i - 8] xor w[i - 14] xor w[i - 16]).rotateLeft(1)
        var a = h0; var b = h1; var c = h2; var d = h3; var e = h4
        for (i in 0 until 80) {
            val (f, k) = when (i) {
                in 0..19 -> ((b and c) or (b.inv() and d)) to 0x5A827999
                in 20..39 -> (b xor c xor d) to 0x6ED9EBA1
                in 40..59 -> ((b and c) or (b and d) or (c and d)) to 0x8F1BBCDC.toInt()
                else -> (b xor c xor d) to 0xCA62C1D6.toInt()
            }
            val t = a.rotateLeft(5) + f + e + k + w[i]
            e = d; d = c; c = b.rotateLeft(30); b = a; a = t
        }
        h0 += a; h1 += b; h2 += c; h3 += d; h4 += e
    }
    return intArrayOf(h0, h1, h2, h3, h4).joinToString("") { it.toUInt().toString(16).padStart(8, '0') }
}
