package com.github.btbrq.simpleeditorplugin.popup

import com.github.btbrq.simpleeditorplugin.constants.Constants
import com.github.btbrq.simpleeditorplugin.domain.HighlighterType
import com.github.btbrq.simpleeditorplugin.domain.TypedRangeHighlighter
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.RangeHighlighterEx
import com.intellij.openapi.editor.markup.*
import com.intellij.refactoring.suggested.range
import com.intellij.ui.JBColor
import java.awt.Font
import java.util.stream.Collectors.toList
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel

class SimpleEditorPopup(editor: Editor) : JPanel() {

    init {
        val selectionModel = editor.selectionModel
        if (!selectionModel.hasSelection()) {
            selectionModel.selectWordAtCaret(false)
        }

        val dialogPanel = JPanel()
        dialogPanel.layout = BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS)
        val jButton = JButton("green")
        dialogPanel.add(jButton)
        jButton.addActionListener({ highlight(editor, JBColor.GREEN) })

        val jButtonb = JButton("blue")
        dialogPanel.add(jButtonb)
        jButtonb.addActionListener({ highlight(editor, JBColor.BLUE) })


        val jButton1 = JButton("underline")
        jButton1.addActionListener({ underline(editor) })
        dialogPanel.add(jButton1)

        val jButton1hh = JButton("highlighters")
        jButton1hh.addActionListener({
            getUserData(editor)!!.forEach {
                println("${it.highlighter.startOffset} - ${it.highlighter.endOffset}")
            }
        })
        dialogPanel.add(jButton1hh)

        add(dialogPanel)
    }

    fun highlight(editor: Editor, color: JBColor) {
        doHighlight(
            editor,
            TextAttributes(color, null, null, null, 0),
            HighlighterType.COLOR
        )
    }

    fun underline(editor: Editor) {
        doHighlight(
            editor,
            TextAttributes(null, null, JBColor.YELLOW, EffectType.LINE_UNDERSCORE, Font.BOLD),
            HighlighterType.UNDERLINE
        )
    }

    private fun doHighlight(editor: Editor, textAttributes: TextAttributes, type: HighlighterType) {
        val primaryCaret: Caret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart
        val end: Int = primaryCaret.selectionEnd
        val markupModel = editor.markupModel

        val userData = getUserData(editor)
        if (alreadyIsHighlightedHereWithSameAttribute(userData, start, end, type)) {
            splitAlreadyExistingHighlightion(userData, start, end, markupModel, type)
        }

        val highlighter = markupModel.addRangeHighlighter(
            start,
            end,
            HighlighterLayer.SELECTION - 1,
            textAttributes,
            HighlighterTargetArea.EXACT_RANGE
        )

        if (userData == null) {
            editor.putUserData(Constants.MYDATA, mutableListOf(TypedRangeHighlighter(type, highlighter)))
        } else {
            val userData1 = getUserData(editor)!!
            userData1.add(TypedRangeHighlighter(type, highlighter))
        }

        val userData1: MutableList<TypedRangeHighlighter> = getUserData(editor)!!
        userData1.forEach {
            //nie dziala, listuje tylko z aktualnego contextu
            println("${it.highlighter.startOffset} - ${it.highlighter.endOffset}")
        }
    }

    private fun splitAlreadyExistingHighlightion(
        userData: MutableList<TypedRangeHighlighter>?,
        start: Int,
        end: Int,
        markupModel: MarkupModel,
        type: HighlighterType
    ) {
        findHighlighersInRange(userData, start, end, type)
            .forEach {
                val hStart = it.highlighter.startOffset
                val hEnd = it.highlighter.endOffset

                it.highlighter.dispose()
                userData!!.remove(it)

                val before = markupModel.addRangeHighlighter(
                    hStart,
                    start,
                    HighlighterLayer.SELECTION - 1,
                    (it.highlighter as RangeHighlighterEx).forcedTextAttributes!!,
                    HighlighterTargetArea.EXACT_RANGE
                )

                val after = markupModel.addRangeHighlighter(
                    end,
                    hEnd,
                    HighlighterLayer.SELECTION - 1,
                    it.highlighter.forcedTextAttributes!!,
                    HighlighterTargetArea.EXACT_RANGE
                )

                userData.add(TypedRangeHighlighter(type, before))
                userData.add(TypedRangeHighlighter(type, after))
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

        return findHighlighersInRange(userData, start, end, type).isNotEmpty()
    }

    private fun findHighlighersInRange(
        userData: MutableList<TypedRangeHighlighter>?,
        start: Int,
        end: Int,
        type: HighlighterType
    ) = userData!!.stream()
        .filter { it.highlighter.range != null }
        .filter { it.highlighter.startOffset <= start && it.highlighter.endOffset >= end }
        .filter { it.type == type}
        .collect(toList())

    private fun getUserData(editor: Editor) = editor.getUserData(Constants.MYDATA)
}
