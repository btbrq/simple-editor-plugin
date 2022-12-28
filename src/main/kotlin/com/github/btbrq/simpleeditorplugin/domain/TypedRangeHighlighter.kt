package com.github.btbrq.simpleeditorplugin.domain

import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes

class TypedRangeHighlighter(
    val type: HighlighterType,
    val highlighter: RangeHighlighter,
    val attributes: TextAttributes
)
