package com.dothebestmayb.customview.presentation.model

enum class Transparent(val value: Int) {
    ONE(255 / 10), TWO(255 / 9), THREE(255 / 8), FOUR(255 / 7), FIVE(255 / 6),
    SIX(255 / 5), SEVEN(255 / 4), EIGHT(255 / 3), NINE(255 / 2), TEN(255);

    override fun toString(): String {
        return "Alpha: $value"
    }
}