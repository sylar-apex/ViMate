package org.app.vimate.appcore.image

import coil3.ComponentRegistry

internal actual fun ComponentRegistry.Builder.addPlatformDecoders() {
    // No video-frame decoder on this platform.
}
