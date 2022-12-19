package com.github.btbrq.simpleeditorplugin.constants

import com.github.btbrq.simpleeditorplugin.domain.TypedRangeHighlighter
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.util.Key

class Constants {
    companion object {
        val STYLES: Key<MutableList<TypedRangeHighlighter>> = Key.create("SIMPLE_EDITOR_STYLES")
        const val HIGHLIGHTER_LAYER: Int = HighlighterLayer.SELECTION
    }

}
