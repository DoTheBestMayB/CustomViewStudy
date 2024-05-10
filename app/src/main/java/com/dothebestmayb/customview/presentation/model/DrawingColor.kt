package com.dothebestmayb.customview.presentation.model

import androidx.annotation.ColorInt

data class DrawingColor(
    val r: UByte,
    val g: UByte,
    val b: UByte,
    @ColorInt val colorValue: Int = (r.toInt() shl 16) or (g.toInt() shl 8) or b.toInt() or (0xFF shl 24),
) {
    override fun toString(): String {
        return "R:$r, G:$g, B:$b"
    }
}
