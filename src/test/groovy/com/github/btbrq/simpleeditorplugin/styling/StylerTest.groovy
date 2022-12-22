package com.github.btbrq.simpleeditorplugin.styling

import com.github.btbrq.simpleeditorplugin.constants.Constants
import com.github.btbrq.simpleeditorplugin.domain.TypedRangeHighlighter
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.SelectionModel
import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.TextAttributes
import spock.lang.Specification
import spock.lang.Subject

import java.awt.*

import static com.github.btbrq.simpleeditorplugin.constants.Constants.STYLES
import static com.github.btbrq.simpleeditorplugin.domain.HighlighterType.BOLD

class StylerTest extends Specification {
    def editor = Mock(Editor) {
        getUserData(STYLES) >> []
    }
    @Subject
    def styler = new Styler(editor)

    def "should clear all"() {
        given:
        caret(7, 10)
        addHighlighter(BOLD, 1, 5)
        assert userData().size() == 1

        when:
        styler.clearAll()

        then:
        userData().size() == 0
    }

    def "should add sample highlighter when there is none interfering"() {
        given:
        addHighlighter(BOLD, 1, 7)

        and:
        def attributes = boldAttributes()
        caret(7, 10)

        when:
        styler.bold()

        then:
        1 * editor.getMarkupModel().addRangeHighlighter(7, 10, _, _, _) >> rangeHighlighter(7, 10, attributes)
        userData().size() == 2
    }

    def "should clear highlighting in the middle of two highlighters"() {
        //-1111111|111--
        //-1111111|-11--
        given:
        addHighlighter(BOLD, 1, 7)
        def highlighter = addHighlighter(BOLD, 7, 10)

        and:
        def attributes = boldAttributes()
        caret(7, 8)

        when:
        styler.bold()

        then:
        userData().size() == 2
        1 * highlighter.highlighter.dispose()
        0 * editor.getMarkupModel().addRangeHighlighter(7, 8, _, _, _)
        1 * editor.getMarkupModel().addRangeHighlighter(8, 10, _, _, _) >> rangeHighlighter(8, 10, attributes)
    }

    def "should extend existing highlighting the current one is starting before existing starts and ends within the existing one"() {
        //-------111----
        //---11111------
        //---1111111----
        given:
        def highlighter = addHighlighter(BOLD, 7, 10)

        and:
        def attributes = boldAttributes()
        caret(3, 8)

        when:
        styler.bold()

        then:
        userData().size() == 1
        1 * highlighter.highlighter.dispose()
        1 * editor.getMarkupModel().addRangeHighlighter(3, 10, _, _, _) >> rangeHighlighter(3, 10, attributes)
    }

    def "should extend existing highlighting as the current one is starting within the existing one and ends after the existing one"() {
        //---1111----
        //-----111---
        //---11111---
        given:
        def highlighter = addHighlighter(BOLD, 3, 7)

        and:
        def attributes = boldAttributes()
        caret(5, 8)

        when:
        styler.bold()

        then:
        userData().size() == 1
        1 * highlighter.highlighter.dispose()
        1 * editor.getMarkupModel().addRangeHighlighter(3, 8, _, _, _) >> rangeHighlighter(3, 8, attributes)
    }

    def "should remove existing highlighting as the range is the same"() {
        //---11111----
        //---11111----
        //------------
        given:
        def highlighter = addHighlighter(BOLD, 3, 7)

        and:
        caret(3, 7)

        when:
        styler.bold()

        then:
        userData().size() == 0
        1 * highlighter.highlighter.dispose()
        0 * editor.getMarkupModel().addRangeHighlighter(_, _, _, _, _)
    }

    def "should overtake existing highlighting as the range wider than the existing one"() {
        //---11111----
        //--1111111---
        //--1111111---
        given:
        def highlighter = addHighlighter(BOLD, 3, 7)

        and:
        def attributes = boldAttributes()
        caret(2, 8)

        when:
        styler.bold()

        then:
        userData().size() == 1
        1 * highlighter.highlighter.dispose()
        1 * editor.getMarkupModel().addRangeHighlighter(2, 8, _, _, _) >> rangeHighlighter(2, 8, attributes)
    }

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

    def userData() {
        editor.getUserData(Constants.STYLES)
    }

}
