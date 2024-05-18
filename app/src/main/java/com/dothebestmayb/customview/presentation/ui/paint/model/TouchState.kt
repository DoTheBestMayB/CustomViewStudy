package com.dothebestmayb.customview.presentation.ui.paint.model

data class TouchState(
    val start: Point,
    val end: Point,
) {
    val width: Int
        get() = end.x - start.x

    val height: Int
        get() = end.y - start.y

    companion object {
        val Empty = TouchState(Point.Empty, Point.Empty)
    }
}
