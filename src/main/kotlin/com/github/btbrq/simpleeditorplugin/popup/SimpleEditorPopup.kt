package com.github.btbrq.simpleeditorplugin.popup

import com.github.btbrq.simpleeditorplugin.constants.Constants
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
                println("${it.startOffset} - ${it.endOffset}")
            }
        })
        dialogPanel.add(jButton1hh)

        add(dialogPanel)
    }

    fun highlight(editor: Editor, color: JBColor) {
        doHighlight(
            editor,
            TextAttributes(color, null, null, null, 0),
            "color"
        )
    }

    fun underline(editor: Editor) {
        doHighlight(
            editor,
            TextAttributes(null, null, JBColor.YELLOW, EffectType.LINE_UNDERSCORE, Font.BOLD),
            "underline"
        )
    }

    private fun doHighlight(editor: Editor, textAttributes: TextAttributes, name: String) {
        val primaryCaret: Caret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart
        val end: Int = primaryCaret.selectionEnd
        val markupModel = editor.markupModel

        val userData = getUserData(editor)
        if (alreadyIsHighlightedHereWithSameAttribute(userData, start, end)) {
            splitAlreadyExistingHighlightion(userData, start, end, markupModel)
        }

        val highlighter = markupModel.addRangeHighlighter(
            start,
            end,
            HighlighterLayer.SELECTION - 1,
            textAttributes,
            HighlighterTargetArea.EXACT_RANGE
        )

        if (userData == null) {
            editor.putUserData(Constants.MYDATA, mutableListOf(highlighter))
        } else {
            val userData1: MutableList<RangeHighlighter> = getUserData(editor)!!
            userData1.add(highlighter)
        }

        val userData1: MutableList<RangeHighlighter> = getUserData(editor)!!
        userData1.forEach {
            //nie dziala, listuje tylko z aktualnego contextu
            println("${it.startOffset} - ${it.endOffset}")
        }
    }

    private fun splitAlreadyExistingHighlightion(
        userData: MutableList<RangeHighlighter>?,
        start: Int,
        end: Int,
        markupModel: MarkupModel
    ) {
        findHighlighersInRange(userData, start, end)
            .forEach {
                val hStart = it.startOffset
                val hEnd = it.endOffset

                it.dispose()
                userData!!.remove(it)

                val before = markupModel.addRangeHighlighter(
                    hStart,
                    start,
                    HighlighterLayer.SELECTION - 1,
                    (it as RangeHighlighterEx).forcedTextAttributes!!,
                    HighlighterTargetArea.EXACT_RANGE
                )

                val after = markupModel.addRangeHighlighter(
                    end,
                    hEnd,
                    HighlighterLayer.SELECTION - 1,
                    it.forcedTextAttributes!!,
                    HighlighterTargetArea.EXACT_RANGE
                )

                userData.add(before)
                userData.add(after)
            }
    }


    private fun alreadyIsHighlightedHereWithSameAttribute(
        userData: MutableList<RangeHighlighter>?,
        start: Int,
        end: Int
    ): Boolean {
        if (userData == null) {
            return false
        }

        return findHighlighersInRange(userData, start, end).isNotEmpty()
    }

    private fun findHighlighersInRange(
        userData: MutableList<RangeHighlighter>?,
        start: Int,
        end: Int
    ) = userData!!.stream()
        .filter { it.range != null }
        .filter { it.startOffset <= start && it.endOffset >= end }
        //and has the same highlightion type
        .collect(toList())

    private fun getUserData(editor: Editor) = editor.getUserData(Constants.MYDATA)
}
