package com.github.btbrq.simpleeditorplugin.styling

import com.github.btbrq.simpleeditorplugin.constants.Constants
import com.github.btbrq.simpleeditorplugin.domain.HighlighterType
import com.github.btbrq.simpleeditorplugin.domain.TypedRangeHighlighter
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.MarkupModel
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.refactoring.suggested.range
import java.awt.Color
import java.awt.Font
import java.util.stream.Collectors

class Styler(private var editor: Editor) {
    fun color(color: Color) {
        doHighlight(
            TextAttributes(color, null, null, null, 0),
            HighlighterType.COLOR
        )
    }

    fun background(color: Color) {
        doHighlight(
            TextAttributes(null, color, null, null, 0),
            HighlighterType.HIGHLIGHT
        )
    }

    fun underline() {
        doHighlight(
            TextAttributes(null, null, Color.YELLOW, EffectType.LINE_UNDERSCORE, 0),
            HighlighterType.UNDERLINE
        )
    }

    fun bold() {
        doHighlight(
            TextAttributes(null, null, null, null, Font.BOLD),
            HighlighterType.BOLD
        )
    }

    fun italic() {
        doHighlight(
            TextAttributes(null, null, null, null, Font.ITALIC),
            HighlighterType.ITALIC
        )
    }

    fun clear(type: HighlighterType) {
        val primaryCaret: Caret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart
        val end: Int = primaryCaret.selectionEnd
        val markupModel = editor.markupModel

        val userData = getUserData()
        if (alreadyIsHighlightedHereWithSameAttribute(userData, start, end, type)) {
            splitAlreadyExistingHighlighting(userData, start, end, markupModel, type)
        }
    }

    private fun doHighlight(textAttributes: TextAttributes, type: HighlighterType) {
        val primaryCaret: Caret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart
        val end: Int = primaryCaret.selectionEnd
        val markupModel = editor.markupModel

        val userData = getUserData()
        if (alreadyIsHighlightedHereWithSameAttribute(userData, start, end, type)) {
            splitAlreadyExistingHighlighting(userData, start, end, markupModel, type)

            if (type.isOverridable()) {
                addHighlighting(markupModel, start, end, textAttributes, userData, type)
            }
        } else {
            addHighlighting(markupModel, start, end, textAttributes, userData, type)
        }
    }

    private fun addHighlighting(
        markupModel: MarkupModel,
        start: Int,
        end: Int,
        textAttributes: TextAttributes,
        userData: MutableList<TypedRangeHighlighter>?,
        type: HighlighterType
    ) {
        val highlighter = markupModel.addRangeHighlighter(
            start,
            end,
            Constants.HIGHLIGHTER_LAYER,
            textAttributes,
            HighlighterTargetArea.EXACT_RANGE
        )

        if (userData == null) {
            editor.putUserData(Constants.STYLES, mutableListOf(TypedRangeHighlighter(type, highlighter)))
        } else {
            val userData1 = getUserData()!!
            userData1.add(TypedRangeHighlighter(type, highlighter))
        }
    }

    private fun splitAlreadyExistingHighlighting(
        userData: MutableList<TypedRangeHighlighter>?,
        start: Int,
        end: Int,
        markupModel: MarkupModel,
        type: HighlighterType
    ) {
        findHighlightersInRange(userData, start, end, type)
            .forEach {
                val hStart = it.highlighter.startOffset
                val hEnd = it.highlighter.endOffset

                it.highlighter.dispose()
                userData!!.remove(it)
                println("${start}-${end} highlight: ${hStart}-${hEnd}")

                if (isWithinExistingRange(start, end, hStart, hEnd)) {
                    println("within")

                    val before = markupModel.addRangeHighlighter(
                        hStart,
                        start,
                        Constants.HIGHLIGHTER_LAYER,
                        (it.highlighter as RangeHighlighterEx).forcedTextAttributes!!,
                        HighlighterTargetArea.EXACT_RANGE
                    )

                    val after = markupModel.addRangeHighlighter(
                        end,
                        hEnd,
                        Constants.HIGHLIGHTER_LAYER,
                        it.highlighter.forcedTextAttributes!!,
                        HighlighterTargetArea.EXACT_RANGE
                    )

                    userData.add(TypedRangeHighlighter(type, before))
                    userData.add(TypedRangeHighlighter(type, after))
                } else if (includesEntireExistingRange(start, end, hStart, hEnd)) {
                    println("includes")

                    //just delete
                } else if (startsWithinExistingRange(start, end, hStart, hEnd)) {
                    println("within")
                    val before = markupModel.addRangeHighlighter(
                        hStart,
                        start,
                        Constants.HIGHLIGHTER_LAYER,
                        (it.highlighter as RangeHighlighterEx).forcedTextAttributes!!,
                        HighlighterTargetArea.EXACT_RANGE
                    )
                    userData.add(TypedRangeHighlighter(type, before))
                } else if (startsBeforeExistingRange(start, end, hStart, hEnd)) {
                    println("after")
                    val after = markupModel.addRangeHighlighter(
                        end,
                        hEnd,
                        Constants.HIGHLIGHTER_LAYER,
                        (it.highlighter as RangeHighlighterEx).forcedTextAttributes!!,
                        HighlighterTargetArea.EXACT_RANGE
                    )
                    userData.add(TypedRangeHighlighter(type, after))
                }
            }
    }


    private fun alreadyIsHighlightedHereWithSameAttribute(
        userData: MutableList<TypedRangeHighlighter>?,
        start: Int,
        end: Int,
        type: HighlighterType
    ): Boolean {
        if (userData == null) {
            return false
        }

        return findHighlightersInRange(userData, start, end, type).isNotEmpty()
    }

    private fun findHighlightersInRange(
        userData: MutableList<TypedRangeHighlighter>?,
        start: Int,
        end: Int,
        type: HighlighterType
    ) = userData!!.stream()
        .filter { it.type == type }
        .filter { it.highlighter.range != null }
        .filter {
            isWithinExistingRange(start, end, it.highlighter.startOffset, it.highlighter.endOffset)
                    || includesEntireExistingRange(start, end, it.highlighter.startOffset, it.highlighter.endOffset)
                    || startsWithinExistingRange(start, end, it.highlighter.startOffset, it.highlighter.endOffset)
                    || startsBeforeExistingRange(start, end, it.highlighter.startOffset, it.highlighter.endOffset)
        }
        .collect(Collectors.toList())

    private fun startsBeforeExistingRange(
        start: Int,
        end: Int,
        existingStart: Int,
        existingEnd: Int
    ) = (start <= existingStart && end <= existingEnd && end >= existingStart)

    private fun startsWithinExistingRange(
        start: Int,
        end: Int,
        existingStart: Int,
        existingEnd: Int
    ) = (start >= existingStart && end >= existingEnd && start <= existingEnd)

    private fun includesEntireExistingRange(
        start: Int,
        end: Int,
        existingStart: Int,
        existingEnd: Int
    ) = (start <= existingStart && end >= existingEnd)

    private fun isWithinExistingRange(
        start: Int,
        end: Int,
        existingStart: Int,
        existingEnd: Int
    ) = start >= existingStart && end <= existingEnd

    private fun getUserData() = editor.getUserData(Constants.STYLES)
}
