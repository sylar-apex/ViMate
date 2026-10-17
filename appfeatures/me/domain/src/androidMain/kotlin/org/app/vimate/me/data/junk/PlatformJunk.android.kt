package org.app.vimate.me.data.junk

import android.content.Context
import android.os.Environment
import java.io.File
import org.app.vimate.me.domain.model.JunkGroup
import org.app.vimate.me.domain.model.JunkKind
import org.app.vimate.me.domain.model.JunkPath
import org.app.vimate.me.domain.repository.JunkFileSystem
import org.koin.core.scope.Scope

actual fun Scope.platformJunkFileSystem(): JunkFileSystem = AndroidJunkFileSystem(get())

/** POC JunkCleanScreen folders, scanned with java.io.File. */
private class AndroidJunkFileSystem(private val ctx: Context) : JunkFileSystem {
    @Suppress("DEPRECATION")
    private val ext: File get() = Environment.getExternalStorageDirectory()
    override val rootPath: String get() = ext.path

    private fun p(f: File) = JunkPath(f.path, exists = f.exists(), isDownloadTemp = f.name == "dl")

    override fun groups() = listOf(
        JunkGroup(JunkKind.CACHE, listOfNotNull(ctx.externalCacheDir, ctx.cacheDir).map(::p), true),
        JunkGroup(JunkKind.TRASH, listOf(p(File(ext, ".Trash"))), false),
        JunkGroup(JunkKind.TEMP, listOf(p(File(ctx.cacheDir, "dl"))), false),
        JunkGroup(JunkKind.THUMBNAIL, listOf("Music", "Pictures", "Movies").map { p(File(ext, "$it/.thumbnails")) }, true),
        JunkGroup(JunkKind.AD, listOf(p(File(ctx.filesDir, "ads"))), true),
    )

    override fun exists(path: String) = File(path).exists()
    override fun sizeOf(path: String, onEnter: (String) -> Unit): Long =
        File(path).walkTopDown().onEnter { onEnter(it.path); true }.filter { it.isFile }.sumOf { it.length() }
    override fun walkRoot(onEnter: (String) -> Unit) {
        ext.walkTopDown().maxDepth(4).onEnter { onEnter(it.path); true }.take(400).count()
    }
    override fun clear(path: String) { File(path).listFiles()?.forEach { it.deleteRecursively() } }
    override fun clearCacheKeepingDownloads() { ctx.cacheDir.listFiles()?.filter { it.name != "dl" }?.forEach { it.deleteRecursively() } }
}
