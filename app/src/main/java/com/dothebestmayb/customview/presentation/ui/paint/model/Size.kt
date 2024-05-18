package com.dothebestmayb.customview.presentation.ui.paint.model

data class Size(
    val width: Int,
    val height: Int,
) {
    override fun toString(): String {
        return "W$width, H$height"
    }

    companion object {
        val Empty = Size(Int.MIN_VALUE, Int.MIN_VALUE)
    }
}
