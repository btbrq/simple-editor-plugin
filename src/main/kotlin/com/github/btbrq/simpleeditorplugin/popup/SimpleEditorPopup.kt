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
        jButton.addActionListener({ color(editor, JBColor.GREEN) })

        val jButtonb = JButton("blue")
        dialogPanel.add(jButtonb)
        jButtonb.addActionListener({ color(editor, JBColor.BLUE) })

        val jButtonBackground = JButton("green background")
        dialogPanel.add(jButtonBackground)
        jButtonBackground.addActionListener({ background(editor, JBColor.GREEN) })

        val jButtonBackground2 = JButton("blue background")
        dialogPanel.add(jButtonBackground2)
        jButtonBackground2.addActionListener({ background(editor, JBColor.BLUE) })


        val jButton1 = JButton("underline")
        jButton1.addActionListener({ underline(editor) })
        dialogPanel.add(jButton1)

        val jButtonBold = JButton("bold")
        jButtonBold.addActionListener({ bold(editor) })
        dialogPanel.add(jButtonBold)

        val jButtonItalic = JButton("italic")
        jButtonItalic.addActionListener({ italic(editor) })
        dialogPanel.add(jButtonItalic)

        val jButton1hh = JButton("highlighters")
        jButton1hh.addActionListener({
            getUserData(editor)!!.forEach {
                println("${it.highlighter.startOffset} - ${it.highlighter.endOffset}")
            }
        })
        dialogPanel.add(jButton1hh)

        add(dialogPanel)
    }

    fun color(editor: Editor, color: JBColor) {
        doHighlight(
            editor,
            TextAttributes(color, null, null, null, 0),
            HighlighterType.COLOR
        )
    }

    fun background(editor: Editor, color: JBColor) {
        doHighlight(
            editor,
            TextAttributes(null, color, null, null, 0),
            HighlighterType.HIGHLIGHT
        )
    }

    fun underline(editor: Editor) {
        doHighlight(
            editor,
            TextAttributes(null, null, JBColor.YELLOW, EffectType.LINE_UNDERSCORE, 0),
            HighlighterType.UNDERLINE
        )
    }

    fun bold(editor: Editor) {
        doHighlight(
            editor,
            TextAttributes(null, null, null, null, Font.BOLD),
            HighlighterType.BOLD
        )
    }

    fun italic(editor: Editor) {
        doHighlight(
            editor,
            TextAttributes(null, null, null, null, Font.ITALIC),
            HighlighterType.ITALIC
        )
    }

    private fun doHighlight(editor: Editor, textAttributes: TextAttributes, type: HighlighterType) {
        val primaryCaret: Caret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart
        val end: Int = primaryCaret.selectionEnd
        val markupModel = editor.markupModel

        val userData = getUserData(editor)
        if (alreadyIsHighlightedHereWithSameAttribute(userData, start, end, type)) {
            splitAlreadyExistingHighlighting(userData, start, end, markupModel, type)
        }
//        else {
//            addHighlighting(markupModel, start, end, textAttributes, userData, editor, type)
//        }

//        if (alreadyIsHighlightedHereWithSameAttribute(userData, start, end, type) && type.isOverridable()) {
        addHighlighting(markupModel, start, end, textAttributes, userData, editor, type)
//        }
    }

    private fun addHighlighting(
        markupModel: MarkupModel,
        start: Int,
        end: Int,
        textAttributes: TextAttributes,
        userData: MutableList<TypedRangeHighlighter>?,
        editor: Editor,
        type: HighlighterType
    ) {
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
    }

    private fun splitAlreadyExistingHighlighting(
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
                println("${start}-${end} highlight: ${hStart}-${hEnd}")

                if (isWithinExistingRange(start, end, hStart, hEnd)) {
                    println("within")

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
                } else if (includesEntireExistingRange(start, end, hStart, hEnd)) {
                    println("includes")

                    //just delete
                } else if (startsWithinExistingRange(start, end, hStart, hEnd)) {
                    //   aaaaaa
                    //      xxxxxx
                    println("within")
                    val before = markupModel.addRangeHighlighter(
                        hStart,
                        start,
                        HighlighterLayer.SELECTION - 1,
                        (it.highlighter as RangeHighlighterEx).forcedTextAttributes!!,
                        HighlighterTargetArea.EXACT_RANGE
                    )
                    userData.add(TypedRangeHighlighter(type, before))
                } else if (startsBeforeExistingRange(start, end, hStart, hEnd)) {
                    //   aaaaaa
                    // xxxxxx
                    println("after")
                    val after = markupModel.addRangeHighlighter(
                        end,
                        hEnd,
                        HighlighterLayer.SELECTION - 1,
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

        return findHighlighersInRange(userData, start, end, type).isNotEmpty()
    }

    private fun findHighlighersInRange(
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


//            aaaaaaaa
//              XXXX

//              aaaa
//            XXXXXXXX

//              aaaa
//                XXXX

//              aaaa
//            XXXX
        }
        .collect(toList())

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

    private fun getUserData(editor: Editor) = editor.getUserData(Constants.MYDATA)
}
