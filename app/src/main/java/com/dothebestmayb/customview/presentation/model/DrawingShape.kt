package com.dothebestmayb.customview.presentation.model

import java.util.UUID

data class DrawingShape(
    val size: Size,
    val point: Point,
    val color: DrawingColor,
    val transparent: Transparent,
    val id: String = UUID.randomUUID().toString(),
) {
    override fun toString(): String {
        return "${this.javaClass.simpleName} ($id), $point, $size, $color, $transparent"
    }

    companion object {
        fun
    }
}
