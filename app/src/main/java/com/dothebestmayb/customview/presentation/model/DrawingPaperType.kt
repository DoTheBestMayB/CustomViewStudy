package com.dothebestmayb.customview.presentation.model

enum class DrawingPaperType(val typeNum: Int) {
    LINE(0), RECT(1);

    companion object {
        fun from(typeNum: Int): DrawingPaperType = entries.getOrElse(typeNum) { LINE }
    }
}