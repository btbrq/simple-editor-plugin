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
import java.awt.Color
import java.awt.Font
import java.util.stream.Collectors

class Styler(private var editor: Editor) {
    private var currentRange: TypedRangeHighlighter? = null

    fun color(color: Color) {
        selectRange()
        currentRange = doHighlight(
            TextAttributes(color, null, null, null, 0),
            HighlighterType.COLOR
        )
    }

    fun background(color: Color) {
        selectRange()
        currentRange = doHighlight(
            TextAttributes(null, color, null, null, 0),
            HighlighterType.HIGHLIGHT
        )
        editor.selectionModel.removeSelection()
    }

    fun underline() {
        selectRange()
        currentRange = doHighlight(
            TextAttributes(null, null, Color.YELLOW, EffectType.LINE_UNDERSCORE, 0),
            HighlighterType.UNDERLINE
        )
    }

    fun bold() {
        selectRange()
        currentRange = doHighlight(
            TextAttributes(null, null, null, null, Font.BOLD),
            HighlighterType.BOLD
        )
    }

    fun italic() {
        selectRange()
        currentRange = doHighlight(
            TextAttributes(null, null, null, null, Font.ITALIC),
            HighlighterType.ITALIC
        )
    }

    fun clearAll() {
        val userData = getUserData()
        userData?.forEach {
            it.highlighter.dispose()
        }
        userData?.clear()
    }

    fun clear(type: HighlighterType) {
        selectRange()
        val primaryCaret: Caret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart
        val end: Int = primaryCaret.selectionEnd
        val markupModel = editor.markupModel

        val userData = getUserData()
        if (alreadyIsHighlightedHereWithSameAttribute(userData, start, end, type)) {
            splitAlreadyExistingHighlighters(userData, start, end, markupModel, type)
        }
    }

    private fun doHighlight(textAttributes: TextAttributes, type: HighlighterType): TypedRangeHighlighter? {
        val primaryCaret: Caret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart
        val end: Int = primaryCaret.selectionEnd
        val markupModel = editor.markupModel

        val userData = getUserData()
        return if (alreadyIsHighlightedHereWithSameAttribute(userData, start, end, type)) {
            splitAlreadyExistingHighlighters(userData, start, end, markupModel, type)

            if (type.isOverridable()) {
                addAttributedHighlighter(markupModel, start, end, textAttributes, userData, type)
            } else {
                null
            }
        } else {
            addAttributedHighlighter(markupModel, start, end, textAttributes, userData, type)
        }
    }

    private fun addAttributedHighlighter(
        markupModel: MarkupModel,
        start: Int,
        end: Int,
        textAttributes: TextAttributes,
        userData: MutableList<TypedRangeHighlighter>?,
        type: HighlighterType
    ): TypedRangeHighlighter {
        val highlighter = markupModel.addRangeHighlighter(
            start,
            end,
            Constants.HIGHLIGHTER_LAYER,
            textAttributes,
            HighlighterTargetArea.EXACT_RANGE
        )

        val range = TypedRangeHighlighter(type, highlighter)
        if (userData == null) {
            editor.putUserData(Constants.STYLES, mutableListOf(range))
        } else {
            getUserData()!!.add(range)
        }
        return range
    }

    private fun splitAlreadyExistingHighlighters(
        userData: MutableList<TypedRangeHighlighter>?,
        start: Int,
        end: Int,
        markupModel: MarkupModel,
        type: HighlighterType
    ) {
        findHighlightersInRange(userData, start, end, type)
            .forEach {
                val existingStart = it.highlighter.startOffset
                val existingEnd = it.highlighter.endOffset

                it.highlighter.dispose()
                userData!!.remove(it)

                if (isWithinExistingRange(start, end, existingStart, existingEnd) && !isExactRange(start, existingStart, end, existingEnd)) {
                    addHighlighterBasedOnExisting(markupModel, existingStart, start, it, userData, type)
                    addHighlighterBasedOnExisting(markupModel, end, existingEnd, it, userData, type)
                } else if (includesEntireExistingRange(start, end, existingStart, existingEnd) && !isExactRange(start, existingStart, end, existingEnd) && !type.isOverridable()) {
                    addHighlighterBasedOnExisting(markupModel, start, end, it, userData, type)
                }  else if (startsWithinExistingRange(start, end, existingStart, existingEnd) && !isExactRange(start, existingStart, end, existingEnd)) {
                    if (type.isOverridable()) {
                        addHighlighterBasedOnExisting(markupModel, existingStart, start, it, userData, type)
                    } else {
                        addHighlighterBasedOnExisting(markupModel, existingStart, end, it, userData, type)
                    }
                } else if (startsBeforeExistingRange(start, end, existingStart, existingEnd) && !isExactRange(start, existingStart, end, existingEnd)) {
                    if (type.isOverridable()) {
                        addHighlighterBasedOnExisting(markupModel, end, existingEnd, it, userData, type)
                    } else {
                        addHighlighterBasedOnExisting(markupModel, start, existingEnd, it, userData, type)
                    }
                }
            }
    }

    private fun addHighlighterBasedOnExisting(
        markupModel: MarkupModel,
        start: Int,
        end: Int,
        existingHighlighter: TypedRangeHighlighter,
        userData: MutableList<TypedRangeHighlighter>,
        type: HighlighterType
    ) {
        if (start < end) {
            val highlighter = markupModel.addRangeHighlighter(
                start,
                end,
                Constants.HIGHLIGHTER_LAYER,
                (existingHighlighter.highlighter as RangeHighlighterEx).forcedTextAttributes!!,
                HighlighterTargetArea.EXACT_RANGE
            )
            userData.add(TypedRangeHighlighter(type, highlighter))
        }
    }

    private fun isExactRange(start: Int, hStart: Int, end: Int, hEnd: Int) =
        start == hStart && end == hEnd

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
    ) = (start <= existingStart && end <= existingEnd && end > existingStart)

    private fun startsWithinExistingRange(
        start: Int,
        end: Int,
        existingStart: Int,
        existingEnd: Int
    ) = (start >= existingStart && end >= existingEnd && start < existingEnd)

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
    ) = existingStart <= start && end <= existingEnd

    private fun getUserData() = editor.getUserData(Constants.STYLES)

    private fun selectRange() {
        if (!editor.selectionModel.hasSelection() && currentRange != null) {
            editor.selectionModel.setSelection(
                currentRange!!.highlighter.startOffset,
                currentRange!!.highlighter.endOffset
            )
        }
    }
}
