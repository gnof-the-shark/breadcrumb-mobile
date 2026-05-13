package com.paul.composables

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

actual fun byteArrayToImageBitmap(data: ByteArray?): ImageBitmap? {
    return data?.let {
        try {
            Image.makeFromEncoded(it).toComposeImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}
