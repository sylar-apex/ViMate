package org.app.vimate.sharedlogic.logic

import kotlin.test.Test
import kotlin.test.assertEquals
import org.app.vimate.sharedlogic.model.FileKind
import org.app.vimate.sharedlogic.model.LinkKind

class LinkClassifierTest {
    @Test
    fun classifiesDirectMediaAndPages() {
        assertEquals(LinkKind.DIRECT_MEDIA, LinkClassifier.classify("https://cdn.example.com/clip.mp4"))
        assertEquals(LinkKind.DIRECT_MEDIA, LinkClassifier.classify("https://cdn.example.com/song.mp3?token=1"))
        assertEquals(LinkKind.PAGE, LinkClassifier.classify("https://example.com/watch?v=1"))
        assertEquals(LinkKind.NOT_A_URL, LinkClassifier.classify("sintel"))
    }

    @Test
    fun audioLinksAreMusic() {
        assertEquals(FileKind.MUSIC, LinkClassifier.kindForUrl("https://cdn.example.com/c.mp3"))
        assertEquals(FileKind.VIDEO, LinkClassifier.kindForUrl("https://cdn.example.com/c.webm"))
        assertEquals("MOV", LinkClassifier.container("https://cdn.example.com/hi.mov?x=1"))
        assertEquals("clip.mp4", LinkClassifier.lastSegment(" https://cdn.example.com/a/clip.mp4?t=1 "))
    }
}
