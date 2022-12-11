package com.github.btbrq.simpleeditorplugin.domain

import java.util.*

enum class HighlighterType {
    COLOR, HIGHLIGHT, UNDERLINE, ITALIC, BOLD;

    fun isOverridable(): Boolean {
        return EnumSet.of(COLOR, HIGHLIGHT).contains(this)
    }
}
