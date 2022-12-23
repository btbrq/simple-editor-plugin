package com.github.btbrq.simpleeditorplugin.styling

import spock.lang.Unroll

import static com.github.btbrq.simpleeditorplugin.domain.HighlighterType.*

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
        performAction(type)

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
        addHighlighter(type, 1, 7, attributes)
        def highlighter = addHighlighter(type, 7, 10, attributes)

        and:
        caret(7, 8)

        when:
        performAction(type)

        then:
        userData().size() == 2
        1 * highlighter.highlighter.dispose()
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
        performAction(type)

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
        performAction(type)

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
        performAction(type)

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
        performAction(type)

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

    //todo create tests for not interfering types: e.g. underline + bold
}
