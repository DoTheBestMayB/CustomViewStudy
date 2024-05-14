package com.dothebestmayb.customview.presentation.model

import androidx.annotation.IntRange

enum class Transparent(val value: Int, val indicatorValue: Int) {
    ONE(255 / 10, 1),
    TWO(255 / 9, 2),
    THREE(255 / 8, 3),
    FOUR(255 / 7, 4),
    FIVE(255 / 6, 5),
    SIX(255 / 5, 6),
    SEVEN(255 / 4, 7),
    EIGHT(255 / 3, 8),
    NINE(255 / 2, 9),
    TEN(255, 10);

    override fun toString(): String {
        return "Alpha: $value"
    }

    companion object {
        fun from(@IntRange(1, 10) indicatorValue: Int) =
            entries.first { it.indicatorValue == indicatorValue }
    }
}