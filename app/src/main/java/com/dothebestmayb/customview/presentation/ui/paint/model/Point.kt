package com.dothebestmayb.customview.presentation.ui.paint.model

data class Point(
    val x: Int,
    val y: Int,
) {
    override fun toString(): String {
        return "X:$x, Y:$y"
    }
}