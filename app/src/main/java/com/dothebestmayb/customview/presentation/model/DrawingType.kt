package com.dothebestmayb.customview.presentation.model

enum class DrawingType(val typeNum: Int) {
    LINE(0), RECT(1);

    companion object {
        fun from(typeNum: Int): DrawingType = entries.getOrElse(typeNum) { LINE }
    }
}