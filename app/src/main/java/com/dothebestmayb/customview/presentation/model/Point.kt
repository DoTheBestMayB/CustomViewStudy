package com.dothebestmayb.customview.presentation.model

data class Point(
    val x: Int,
    val y: Int,
) {
    override fun toString(): String {
        return "X:$x, Y:$y"
    }
}