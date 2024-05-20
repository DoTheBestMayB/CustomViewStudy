package com.dothebestmayb.customview.presentation.ui.paint.model

import androidx.annotation.ColorInt
import kotlin.random.Random

data class DrawingColor(
    val r: UByte,
    val g: UByte,
    val b: UByte,
    @ColorInt val colorValue: Int = (r.toInt() shl 16) or (g.toInt() shl 8) or b.toInt() or (0xFF shl 24),
    @ColorInt val complementaryColor: Int = ((255 - r.toInt()) shl 16) or ((255 - g.toInt()) shl 8) or (255 - b.toInt()) or (0xFF shl 24),
) {
    override fun toString(): String {
        return "#%02X%02X%02X".format(r.toInt(), g.toInt(), b.toInt())
    }

    companion object {
        val DEFAULT = DrawingColor(0u, 0u, 0u)
        fun random(random: Random) = DrawingColor(
            r = random.nextInt(256).toUByte(),
            g = random.nextInt(256).toUByte(),
            b = random.nextInt(256).toUByte(),
        )
    }
}
