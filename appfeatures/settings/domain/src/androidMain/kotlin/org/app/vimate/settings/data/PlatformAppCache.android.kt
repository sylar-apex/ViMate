package org.app.vimate.settings.data

import android.content.Context
import org.app.vimate.settings.domain.repository.AppCache
import org.koin.core.scope.Scope

actual fun Scope.platformAppCache(): AppCache {
    val ctx = get<Context>()
    return object : AppCache {
        override fun size() = ctx.cacheDir.walkTopDown().filter { it.isFile }.sumOf { it.length() }
        override fun clear() { ctx.cacheDir.listFiles()?.filter { it.name != "dl" }?.forEach { it.deleteRecursively() } }
    }
}
