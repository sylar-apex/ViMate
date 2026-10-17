package org.app.vimate.appcore.image

import androidx.compose.runtime.Composable
import coil3.ComponentRegistry
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.svg.SvgDecoder

/**
 * App-wide Coil image loader (POC App.kt): network via Ktor, SVG decoder, plus video-frame decoding where
 * the platform has it (Android). The shell calls this once at the root of the composition.
 */
@Composable
fun InstallImageLoader() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
                add(SvgDecoder.Factory())
                addPlatformDecoders()
            }
            .build()
    }
}

/** Adds decoders only one platform has (Android: VideoFrameDecoder). */
internal expect fun ComponentRegistry.Builder.addPlatformDecoders()
