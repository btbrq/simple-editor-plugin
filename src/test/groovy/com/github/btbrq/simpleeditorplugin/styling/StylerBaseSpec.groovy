package com.github.btbrq.simpleeditorplugin.styling

import com.github.btbrq.simpleeditorplugin.domain.TypedRangeHighlighter
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.TextAttributes
import spock.lang.Specification
import spock.lang.Subject

import java.awt.*

import static com.github.btbrq.simpleeditorplugin.constants.Constants.STYLES
import static com.github.btbrq.simpleeditorplugin.domain.HighlighterType.*

class StylerBaseSpec extends Specification {
    def editor = Mock(Editor) {
        getUserData(STYLES) >> []
    }
    @Subject
    def styler = new Styler(editor)

    def caret(def start, def end) {
        editor.getMarkupModel() >> Mock(MarkupModel)
        editor.getSelectionModel() >> Mock(SelectionModel)
        editor.getCaretModel() >> Mock(CaretModel) {
            getPrimaryCaret() >> Mock(Caret) {
                getSelectionStart() >> start
                getSelectionEnd() >> end
            }
        }
    }

    def performBasicAction(def type) {
        if (type == BOLD) {
            styler.bold()
        } else if (type == UNDERLINE) {
            styler.underline()
        } else if (type == ITALIC) {
            styler.italic()
        }
    }

    def performColoredAction(def type, Color color) {
        if (type == COLOR) {
            styler.color(color)
        } else if (type == HIGHLIGHT) {
            styler.background(color)
        }
    }

    def addHighlighter(def type, def start, def end, def attributes = boldAttributes()) {
        def highlighter = highlighter(type, start, end, attributes)
        userData().add(highlighter)
        highlighter
    }

    def highlighter(def type, def start, def end, def attributes) {
        new TypedRangeHighlighter(type, rangeHighlighter(start, end, attributes))
    }

    def rangeHighlighter(def start, def end, def attributes) {
        Mock(RangeHighlighterEx) {
            getStartOffset() >> start
            getEndOffset() >> end
            getForcedTextAttributes() >> attributes
        }
    }

    def boldAttributes() {
        new TextAttributes(null, null, null, null, Font.BOLD)
    }

    def underlineAttributes() {
        new TextAttributes(null, null, Color.YELLOW, EffectType.LINE_UNDERSCORE, 0)
    }

    def italicAttributes() {
        new TextAttributes(null, null, null, null, Font.ITALIC)
    }

    def colorAttributes(Color color) {
        new TextAttributes(color, null, null, null, 0)
    }

    def backgroundAttributes(Color color) {
        new TextAttributes(null, color, null, null, 0)
    }

    def userData() {
        editor.getUserData(STYLES)
    }

}
