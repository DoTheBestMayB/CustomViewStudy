package com.dothebestmayb.customview.presentation.model

import android.graphics.Rect

sealed interface DrawingInfo {
    data class DrawingRectInfo(
        val shape: DrawingShape,
        val rect: Rect,
    ) : DrawingInfo
}