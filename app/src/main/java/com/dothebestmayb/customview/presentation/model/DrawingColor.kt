package com.dothebestmayb.customview.presentation.model

data class DrawingColor(
    val r: UByte,
    val g: UByte,
    val b: UByte,
) {
    override fun toString(): String {
        return "R:$r, G:$g, B:$b"
    }
}
