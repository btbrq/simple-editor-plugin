package com.github.btbrq.simpleeditorplugin.styling

import com.intellij.openapi.editor.markup.TextAttributes
import spock.lang.Unroll

import java.awt.*

import static com.github.btbrq.simpleeditorplugin.constants.Constants.HIGHLIGHTER_LAYER
import static com.github.btbrq.simpleeditorplugin.domain.HighlighterType.BOLD
import static com.github.btbrq.simpleeditorplugin.domain.HighlighterType.COLOR
import static com.github.btbrq.simpleeditorplugin.domain.HighlighterType.HIGHLIGHT
import static com.github.btbrq.simpleeditorplugin.domain.HighlighterType.UNDERLINE
import static com.intellij.openapi.editor.markup.HighlighterTargetArea.EXACT_RANGE

class StylerColoredActionTest extends StylerBaseSpec {
    @Unroll
    def "should add sample highlighter when there is none interfering: #type"() {
        given:
        addHighlighter(type, 1, 7, attributes)

        and:
        caret(7, 10)

        when:
        performColoredAction(type, color)

        then:
        1 * editor.getMarkupModel().addRangeHighlighter(7, 10, _, attributes, _) >> rangeHighlighter(7, 10, attributes)
        userData().size() == 2

        where:
        type      | color       | attributes
        COLOR     | Color.RED   | colorAttributes(color)
        HIGHLIGHT | Color.GREEN | backgroundAttributes(color)
    }

    @Unroll
    def "should change highlighting affecting one of existing highlighters: #type"() {
        //-BBBBBBB|GGG--
        //-BBBBBBB|RGG--
        given:
        def highlighter1 = addHighlighter(type, 1, 7, existingAttributes1)
        def highlighter2 = addHighlighter(type, 7, 10, existingAttributes2)

        and:
        caret(7, 8)

        when:
        performColoredAction(type, color)

        then:
        userData().size() == 3
        0 * highlighter1.highlighter.dispose()
        1 * highlighter2.highlighter.dispose()
        1 * editor.getMarkupModel().addRangeHighlighter(7, 8, _, attributes, _) >> rangeHighlighter(7, 8, attributes)
        1 * editor.getMarkupModel().addRangeHighlighter(8, 10, _, existingAttributes2, _) >> rangeHighlighter(8, 10, existingAttributes2)

        where:
        type      | existingAttributes1              | existingAttributes2               | color     | attributes
        COLOR     | colorAttributes(Color.BLUE)      | colorAttributes(Color.GREEN)      | Color.RED | colorAttributes(color)
        HIGHLIGHT | backgroundAttributes(Color.BLUE) | backgroundAttributes(Color.GREEN) | Color.RED | backgroundAttributes(color)
    }

    @Unroll
    def "should color part of the existing highlighter - as current starts before existing one: #type"() {
        //-------BBB----
        //---RRRRR------
        //---RRRRRBB----
        given:
        def highlighter = addHighlighter(type, 7, 10, existingAttributes)

        and:
        caret(3, 8)

        when:
        performColoredAction(type, color)

        then:
        userData().size() == 2
        1 * highlighter.highlighter.dispose()
        1 * editor.getMarkupModel().addRangeHighlighter(3, 8, _, attributes, _) >> rangeHighlighter(3, 8, attributes)
        1 * editor.getMarkupModel().addRangeHighlighter(8, 10, _, existingAttributes, _) >> rangeHighlighter(8, 10, existingAttributes)

        where:
        type      | existingAttributes               | color     | attributes
        COLOR     | colorAttributes(Color.BLUE)      | Color.RED | colorAttributes(color)
        HIGHLIGHT | backgroundAttributes(Color.BLUE) | Color.RED | backgroundAttributes(color)
    }

    @Unroll
    def "should color part of the existing highlighter as the current one is starting within the existing one and ends after the existing one: #type"() {
        //---BBBB----
        //------RRR---
        //---BBBRR---
        given:
        def highlighter = addHighlighter(type, 3, 7, existingAttributes)

        and:
        caret(6, 8)

        when:
        performColoredAction(type, color)

        then:
        userData().size() == 2
        1 * highlighter.highlighter.dispose()
        1 * editor.getMarkupModel().addRangeHighlighter(6, 8, _, attributes, _) >> rangeHighlighter(6, 8, attributes)
        1 * editor.getMarkupModel().addRangeHighlighter(3, 6, _, existingAttributes, _) >> rangeHighlighter(3, 6, existingAttributes)

        where:
        type      | existingAttributes               | color     | attributes
        COLOR     | colorAttributes(Color.BLUE)      | Color.RED | colorAttributes(color)
        HIGHLIGHT | backgroundAttributes(Color.BLUE) | Color.RED | backgroundAttributes(color)
    }

    @Unroll
    def "should remove existing highlighting as the range is the same: #type"() {
        //---BBBBB----
        //---RRRRR----
        //------------
        given:
        def highlighter = addHighlighter(type, 3, 7, existingAttributes)

        and:
        caret(3, end)

        when:
        performColoredAction(type, color)

        then:
        userData().size() == 1
        1 * highlighter.highlighter.dispose()
        1 * editor.getMarkupModel().addRangeHighlighter(3, end, _, attributes, _) >> rangeHighlighter(3, end, attributes)

        where:
        type      | existingAttributes               | color     | attributes                  | end
        COLOR     | colorAttributes(Color.BLUE)      | Color.RED | colorAttributes(color)      | 7
        HIGHLIGHT | backgroundAttributes(Color.BLUE) | Color.RED | backgroundAttributes(color) | 7
        COLOR     | colorAttributes(Color.BLUE)      | Color.RED | colorAttributes(color)      | 15
        HIGHLIGHT | backgroundAttributes(Color.BLUE) | Color.RED | backgroundAttributes(color) | 15
    }

    @Unroll
    def "should overtake existing highlighting as the range wider than the existing one: #type"() {
        //---BBBBB----
        //--RRRRRRR---
        //--RRRRRRR---
        given:
        def highlighter = addHighlighter(type, 3, 7, existingAttributes)

        and:
        caret(2, 8)

        when:
        performColoredAction(type, color)

        then:
        userData().size() == 1
        1 * highlighter.highlighter.dispose()
        1 * editor.getMarkupModel().addRangeHighlighter(2, 8, _, attributes, _) >> rangeHighlighter(2, 8, attributes)

        where:
        type      | existingAttributes               | color     | attributes
        COLOR     | colorAttributes(Color.BLUE)      | Color.RED | colorAttributes(color)
        HIGHLIGHT | backgroundAttributes(Color.BLUE) | Color.RED | backgroundAttributes(color)
    }

    def "should add highlighting affecting range of already existing, but the types are not interfering"() {
        given:
        def highlighter1 = addHighlighter(COLOR, 1, 7, colorAttributes(Color.RED))
        def highlighter2 = addHighlighter(HIGHLIGHT, 5, 10, backgroundAttributes(Color.RED))
        def attributes = colorAttributes(Color.GREEN)

        and:
        caret(7, 15)

        when:
        performColoredAction(COLOR, Color.GREEN)

        then:
        userData().size() == 3
        0 * highlighter1.highlighter.dispose()
        0 * highlighter2.highlighter.dispose()
        1 * editor.getMarkupModel().addRangeHighlighter(7, 15, _, attributes, _) >> rangeHighlighter(7, 15, boldAttributes())
    }

    def "should repeat background highlight operation"() {
        given:
        TextAttributes greenAttributes = backgroundAttributes(Color.GREEN)
        def redAttributes = backgroundAttributes(Color.RED)
        caret(7, 10)

        and:
        editor.getMarkupModel().addRangeHighlighter(7, 10, HIGHLIGHTER_LAYER, greenAttributes, EXACT_RANGE) >> rangeHighlighter(7, 10, greenAttributes)
        performColoredAction(HIGHLIGHT, Color.GREEN)
        assert userData().size() == 1

        when:
        performColoredAction(HIGHLIGHT, Color.RED)

        then:
        1 * editor.getMarkupModel().addRangeHighlighter(7, 10, _, redAttributes, _) >> rangeHighlighter(7, 10, redAttributes)
        userData().size() == 1
    }

    def "should do color operation after background highlighting"() {
        given:
        TextAttributes greenAttributes = backgroundAttributes(Color.GREEN)
        def redAttributes = colorAttributes(Color.RED)
        caret(7, 10)

        and:
        editor.getMarkupModel().addRangeHighlighter(7, 10, HIGHLIGHTER_LAYER, greenAttributes, EXACT_RANGE) >> rangeHighlighter(7, 10, greenAttributes)
        performColoredAction(HIGHLIGHT, Color.GREEN)
        assert userData().size() == 1

        when:
        performColoredAction(COLOR, Color.RED)

        then:
        1 * editor.getMarkupModel().addRangeHighlighter(7, 10, _, redAttributes, _) >> rangeHighlighter(7, 10, redAttributes)
        userData().size() == 2
    }

    def "should do basic operation and other types of operations after background highlighting and then clear"() {
        given:
        TextAttributes backgroundAttributes = backgroundAttributes(Color.GREEN)
        caret(7, 10)

        and:
        editor.getMarkupModel().addRangeHighlighter(7, 10, HIGHLIGHTER_LAYER, backgroundAttributes, EXACT_RANGE) >> rangeHighlighter(7, 10, backgroundAttributes)
        performColoredAction(HIGHLIGHT, Color.GREEN)
        assert userData().size() == 1

        when: "bold action"
        performBasicAction(BOLD)

        then: "bold styling should be added"
        1 * editor.getMarkupModel().addRangeHighlighter(7, 10, _, boldAttributes(), _) >> rangeHighlighter(7, 10, boldAttributes())
        userData().size() == 2

        when: "underline action"
        performBasicAction(UNDERLINE)

        then: "underline styling should be added"
        1 * editor.getMarkupModel().addRangeHighlighter(7, 10, _, underlineAttributes(), _) >> rangeHighlighter(7, 10, underlineAttributes())
        userData().size() == 3

        when: "color action"
        performColoredAction(COLOR, Color.RED)

        then: "color should be added"
        1 * editor.getMarkupModel().addRangeHighlighter(7, 10, _, colorAttributes(Color.RED), _) >> rangeHighlighter(7, 10, colorAttributes(Color.RED))
        userData().size() == 4

        when: "clear highlighting"
        styler.clear(HIGHLIGHT)

        then: "highlighting should be cleared"
        userData().size() == 3
    }
}
