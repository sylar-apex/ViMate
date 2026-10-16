package org.app.vimate.sharedlogic.logic

import org.app.kmediaplayer.util.formatDecimal

/** Text formats for durations, sizes and dates, matching the reference app. */
object Formats {
    private const val KB = 1024.0
    private const val GIB = 1_073_741_824.0
    private const val MIB = 1_048_576.0

    /** "m:ss" or "h:mm:ss"; empty for unknown durations. */
    fun duration(ms: Long): String {
        if (ms <= 0) return ""
        val total = ms / 1000
        val hours = total / 3600
        val minutes = (total % 3600) / 60
        val seconds = total % 60
        val ss = seconds.toString().padStart(2, '0')
        return if (hours > 0) "$hours:${minutes.toString().padStart(2, '0')}:$ss" else "$minutes:$ss"
    }

    /** Binary units with one truncated decimal: "4.1 MB". */
    fun bytes(bytes: Long): String {
        if (bytes < KB) return "$bytes B"
        val kb = bytes / KB
        if (kb < KB) return "${truncate1(kb)} KB"
        val mb = kb / KB
        if (mb < KB) return "${truncate1(mb)} MB"
        return "${truncate1(mb / KB)} GB"
    }

    /** Decimal units with two decimals, as the reference app lists file sizes: "4.37 MB", "2.96 GB". */
    fun decimalSize(bytes: Long): String = when {
        bytes >= 1_000_000_000L -> formatDecimal(bytes / 1e9, 2) + " GB"
        bytes >= 1_000_000L -> formatDecimal(bytes / 1e6, 2) + " MB"
        else -> formatDecimal(bytes / 1e3, 2) + " KB"
    }

    /** "4.17MB" as the download sheet lists rendition sizes; empty when the size is unknown. */
    fun sheetSize(bytes: Long): String {
        if (bytes <= 0) return ""
        val mb = bytes / MIB
        if (mb >= KB) return "${(mb / KB * 100).toLong() / 100.0}GB"
        val whole = (mb * 100).toLong()
        return "${whole / 100}.${(whole % 100).toString().padStart(2, '0')}MB"
    }

    /** "54.3GB" for volume sizes. */
    fun volume(bytes: Long): String = "${truncate1(bytes / GIB)}GB"

    /** "54.3 GB" for the storage row of the My Files menu. */
    fun gigabytes(bytes: Long): String {
        val tenths = bytes * 10 / (1L shl 30)
        return "${tenths / 10}.${tenths % 10} GB"
    }

    /** yyyy-MM-dd for an epoch time in UTC. */
    fun isoDate(ms: Long): String {
        val days = ms / 86_400_000L
        val z = days + 719_468
        val era = (if (z >= 0) z else z - 146_096) / 146_097
        val doe = z - era * 146_097
        val yoe = (doe - doe / 1460 + doe / 36_524 - doe / 146_096) / 365
        val doy = doe - (365 * yoe + yoe / 4 - yoe / 100)
        val mp = (5 * doy + 2) / 153
        val d = doy - (153 * mp + 2) / 5 + 1
        val m = if (mp < 10) mp + 3 else mp - 9
        val y = yoe + era * 400 + if (m <= 2) 1 else 0
        return "$y-${m.toString().padStart(2, '0')}-${d.toString().padStart(2, '0')}"
    }

    fun percent(fraction: Float): Int = (fraction * 100).toInt()

    private fun truncate1(value: Double): String = "${(value * 10).toLong() / 10.0}"
}
