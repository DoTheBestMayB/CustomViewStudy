package com.dothebestmayb.customview.presentation.model

import android.graphics.Rect

sealed interface DrawingInfo {

    val shape: DrawingShape

    data class DrawingRectInfo(
        override val shape: DrawingShape,
        val rect: Rect,
    ) : DrawingInfo
}