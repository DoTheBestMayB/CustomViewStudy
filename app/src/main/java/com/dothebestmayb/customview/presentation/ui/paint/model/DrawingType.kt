package com.dothebestmayb.customview.presentation.ui.paint.model

enum class DrawingType(val typeNum: Int) {
    UNKNOWN(-1), LINE(0), RECT(1);

    companion object {
        fun from(typeNum: Int): DrawingType =
            entries.firstOrNull { it.typeNum == typeNum } ?: UNKNOWN
    }
}