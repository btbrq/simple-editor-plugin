package com.github.btbrq.simpleeditorplugin.domain

import java.util.EnumSet

enum class HighlighterType {
    COLOR, HIGHLIGHT, UNDERLINE, ITALIC, BOLD;

    fun isOverridable(): Boolean {
        return EnumSet.of(COLOR, HIGHLIGHT).contains(this)
    }
}
