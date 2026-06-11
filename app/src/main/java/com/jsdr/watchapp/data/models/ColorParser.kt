package com.jsdr.watchapp.data.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.toColorInt
import com.jsdr.watchapp.BrandPurple

fun Color.toHex(): String {
    return String.format("#%08X", 0xFFFFFFFF and this.toArgb().toLong())
}

fun String.toColor(): Color {
    return try {
        Color(this.toColorInt())
    } catch (_: IllegalArgumentException) {
        BrandPurple
    }
}