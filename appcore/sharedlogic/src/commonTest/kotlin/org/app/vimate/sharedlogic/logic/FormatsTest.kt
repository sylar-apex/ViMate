package org.app.vimate.sharedlogic.logic

import kotlin.test.Test
import kotlin.test.assertEquals

class FormatsTest {
    @Test
    fun durationsUseMinutesUnderAnHour() {
        assertEquals("4:12", Formats.duration(252_000))
        assertEquals("1:02:03", Formats.duration(3_723_000))
        assertEquals("", Formats.duration(0))
    }

    @Test
    fun sizesMatchTheReferenceFormats() {
        assertEquals("4.1 MB", Formats.bytes(4_372_373))
        assertEquals("4.37 MB", Formats.decimalSize(4_372_373))
        assertEquals("4.16MB", Formats.sheetSize(4_372_373))
        assertEquals("", Formats.sheetSize(0))
        assertEquals("1970-01-02", Formats.isoDate(86_400_000))
    }

    @Test
    fun fileNamesAreSafeAndKeepTheExtension() {
        assertEquals("sintel-official-trailer.mp4", FileNames.forDownload("Sintel | Official trailer", "https://x/y.mp4?z=1"))
        assertEquals("media.mp3", FileNames.forDownload("!!!", "https://x/y.mp3"))
    }

    @Test
    fun passcodeHashIsStable() {
        assertEquals(PrivateLock.hash("1234"), PrivateLock.hash("1234"))
        assertEquals(false, PrivateLock.hash("1234") == PrivateLock.hash("4321"))
    }
}
