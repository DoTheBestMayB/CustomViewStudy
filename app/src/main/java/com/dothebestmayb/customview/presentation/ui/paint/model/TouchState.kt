package com.dothebestmayb.customview.presentation.ui.paint.model

import kotlin.math.abs

data class TouchState(
    val start: Point,
    val end: Point,
) {
    val width: Int
        get() = abs(end.x - start.x)

    val height: Int
        get() = abs(end.y - start.y)

    fun getLeftX(): Int {
        val startX = start.x
        val endX = end.x
        return if (startX <= endX) startX else endX
    }

    fun getLeftY(): Int {
        val startY = start.y
        val endY = end.y
        return if (startY <= endY) startY else endY
    }

    companion object {
        val Empty = TouchState(Point.Empty, Point.Empty)
    }
}
