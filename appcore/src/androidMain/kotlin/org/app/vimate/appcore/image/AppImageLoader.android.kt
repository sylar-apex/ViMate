package org.app.vimate.appcore.image

import coil3.ComponentRegistry

internal actual fun ComponentRegistry.Builder.addPlatformDecoders() {
    add(coil3.video.VideoFrameDecoder.Factory())
}
