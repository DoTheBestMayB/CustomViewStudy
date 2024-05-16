package com.dothebestmayb.customview.presentation.model

import java.util.UUID

data class DrawingShape(
    val size: Size,
    val point: Point,
    val color: DrawingColor,
    val transparent: Transparent,
    val type: DrawingType,
    val clicked: Boolean = false,
    val id: String = UUID.randomUUID().toString(),
    val name: String = id,
) {
    override fun toString(): String {
        return "${this.javaClass.simpleName} ($id), $point, $size, $color, $transparent"
    }

    fun isClicked(x: Int, y: Int): Boolean {
        return x in point.x..point.x + size.width && y in point.y..point.y + size.height
    }
}