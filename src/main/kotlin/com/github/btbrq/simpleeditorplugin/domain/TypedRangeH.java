package com.github.btbrq.simpleeditorplugin.domain;

import com.intellij.openapi.editor.markup.RangeHighlighter;

public class TypedRangeH {
    private HighlighterType type;
    private RangeHighlighter highlighter;

    public TypedRangeH(HighlighterType type, RangeHighlighter highlighter) {
        this.type = type;
        this.highlighter = highlighter;
    }

    enum HighlighterType {
        COLOR, BACKGROUND, UNDERLINE, ITALIC, BOLD
    }
}
