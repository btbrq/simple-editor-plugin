package com.github.btbrq.simpleeditorplugin.styling

import spock.lang.Unroll

import static com.github.btbrq.simpleeditorplugin.domain.HighlighterType.BOLD
import static com.github.btbrq.simpleeditorplugin.domain.HighlighterType.ITALIC
import static com.github.btbrq.simpleeditorplugin.domain.HighlighterType.UNDERLINE

class StylerBasicActionTest extends StylerBaseSpec {
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

    @Unroll
    def "should add sample highlighter when there is none interfering: #type"() {
        given:
        addHighlighter(type, 1, 7, attributes)

        and:
        caret(7, 10)

        when:
        performBasicAction(type)

        then:
        1 * editor.getMarkupModel().addRangeHighlighter(7, 10, _, _, _) >> rangeHighlighter(7, 10, attributes)
        userData().size() == 2

        where:
        type      | attributes
        BOLD      | boldAttributes()
        UNDERLINE | underlineAttributes()
        ITALIC    | italicAttributes()
    }

    @Unroll
    def "should clear highlighting in the middle of two highlighters, affecting one: #type"() {
        //-1111111|111--
        //-1111111|-11--
        given:
        def highlighter1 = addHighlighter(type, 1, 7, attributes)
        def highlighter2 = addHighlighter(type, 7, 10, attributes)

        and:
        caret(7, 8)

        when:
        performBasicAction(type)

        then:
        userData().size() == 2
        0 * highlighter1.highlighter.dispose()
        1 * highlighter2.highlighter.dispose()
        0 * editor.getMarkupModel().addRangeHighlighter(7, 8, _, _, _)
        1 * editor.getMarkupModel().addRangeHighlighter(8, 10, _, _, _) >> rangeHighlighter(8, 10, attributes)

        where:
        type      | attributes
        BOLD      | boldAttributes()
        UNDERLINE | underlineAttributes()
        ITALIC    | italicAttributes()
    }

    @Unroll
    def "should extend existing highlighting the current one is starting before existing starts and ends within the existing one: #type"() {
        //-------111----
        //---11111------
        //---1111111----
        given:
        def highlighter = addHighlighter(type, 7, 10, attributes)

        and:
        caret(3, 8)

        when:
        performBasicAction(type)

        then:
        userData().size() == 1
        1 * highlighter.highlighter.dispose()
        1 * editor.getMarkupModel().addRangeHighlighter(3, 10, _, _, _) >> rangeHighlighter(3, 10, attributes)

        where:
        type      | attributes
        BOLD      | boldAttributes()
        UNDERLINE | underlineAttributes()
        ITALIC    | italicAttributes()
    }

    @Unroll
    def "should extend existing highlighting as the current one is starting within the existing one and ends after the existing one: #type"() {
        //---1111----
        //-----111---
        //---11111---
        given:
        def highlighter = addHighlighter(type, 3, 7, attributes)

        and:
        caret(5, 8)

        when:
        performBasicAction(type)

        then:
        userData().size() == 1
        1 * highlighter.highlighter.dispose()
        1 * editor.getMarkupModel().addRangeHighlighter(3, 8, _, _, _) >> rangeHighlighter(3, 8, attributes)

        where:
        type      | attributes
        BOLD      | boldAttributes()
        UNDERLINE | underlineAttributes()
        ITALIC    | italicAttributes()
    }

    @Unroll
    def "should remove existing highlighting as the range is the same: #type"() {
        //---11111----
        //---11111----
        //------------
        given:
        def highlighter = addHighlighter(type, 3, 7, attributes)

        and:
        caret(3, 7)

        when:
        performBasicAction(type)

        then:
        userData().size() == 0
        1 * highlighter.highlighter.dispose()
        0 * editor.getMarkupModel().addRangeHighlighter(_, _, _, _, _)

        where:
        type      | attributes
        BOLD      | boldAttributes()
        UNDERLINE | underlineAttributes()
        ITALIC    | italicAttributes()
    }

    @Unroll
    def "should overtake existing highlighting as the range wider than the existing one: #type"() {
        //---11111----
        //--1111111---
        //--1111111---
        given:
        def highlighter = addHighlighter(type, 3, 7, attributes)

        and:
        caret(2, 8)

        when:
        performBasicAction(type)

        then:
        userData().size() == 1
        1 * highlighter.highlighter.dispose()
        1 * editor.getMarkupModel().addRangeHighlighter(2, 8, _, _, _) >> rangeHighlighter(2, 8, attributes)

        where:
        type      | attributes
        BOLD      | boldAttributes()
        UNDERLINE | underlineAttributes()
        ITALIC    | italicAttributes()
    }

    def "should add highlighting affecting range of two already existing, but the types are not interfering"() {
        given:
        def highlighter1 = addHighlighter(UNDERLINE, 1, 7, boldAttributes())
        def highlighter2 = addHighlighter(ITALIC, 5, 10, italicAttributes())

        and:
        caret(7, 15)

        when:
        performBasicAction(BOLD)

        then:
        userData().size() == 3
        0 * highlighter1.highlighter.dispose()
        0 * highlighter2.highlighter.dispose()
        1 * editor.getMarkupModel().addRangeHighlighter(7, 15, _, _, _) >> rangeHighlighter(7, 15, boldAttributes())
    }
}
