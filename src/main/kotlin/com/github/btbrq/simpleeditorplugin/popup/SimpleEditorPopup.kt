package com.github.btbrq.simpleeditorplugin.popup

import com.github.btbrq.simpleeditorplugin.constants.Constants
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.*
import com.intellij.refactoring.suggested.range
import com.intellij.ui.JBColor
import java.awt.Font
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel

class SimpleEditorPopup(editor: Editor) : JPanel() {

    private val highlighters: HashMap<String, RangeHighlighter> = HashMap()
//    val underline: RangeHighlighter = null

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

        val jButton1r = JButton("remove underline")
        jButton1r.addActionListener({ removeUnderline(editor) })
        dialogPanel.add(jButton1r)

        val jButton1hh = JButton("highlighters")
        jButton1hh.addActionListener({
            editor.markupModel.allHighlighters.forEach {
                println("${it.range!!.startOffset} - ${it.range!!.endOffset}")
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
        //create full ide map
        //when adding highlightion check if given text range there is any highlightion
        //if it is of the same type, split into 3 highliters - before, THIS, after, for THIS remove highlightion
        val primaryCaret: Caret = editor.caretModel.primaryCaret
        val start: Int = primaryCaret.selectionStart
        val end: Int = primaryCaret.selectionEnd
        val markupModel = editor.markupModel

//        if (alreadyIsHighlightedHereWithSameAttribute) {}

        val highlighter = markupModel.addRangeHighlighter(
            start,
            end,
            HighlighterLayer.SELECTION - 1,
            textAttributes,
            HighlighterTargetArea.EXACT_RANGE
        )
        highlighters.put(name, highlighter)

        val userData = editor.getUserData(Constants.MYDATA)
        if (userData == null) {
            editor.putUserData(Constants.MYDATA, mutableListOf(highlighter))
        } else {
            val userData1: MutableList<RangeHighlighter> = editor.getUserData(Constants.MYDATA)!!
            userData1.add(highlighter)
        }

        val userData1: MutableList<RangeHighlighter> = editor.getUserData(Constants.MYDATA)!!
        userData1.forEach {
            //nie dziala, listuje tylko z aktualnego contextu
            println("${it.range!!.startOffset} - ${it.range!!.endOffset}")
        }
    }

    fun removeUnderline(editor: Editor) {
        val markupModel = editor.markupModel
//nie przejdzie bo musi byc kazdy na roznym tekscie
        if (highlighters.get("underline") != null) {
            val get = highlighters.get("underline")!!
            get.dispose()
            highlighters.remove("underline")
        } else {
            val primaryCaret: Caret = editor.caretModel.primaryCaret
            val start: Int = primaryCaret.selectionStart
            val end: Int = primaryCaret.selectionEnd

            val textAttributes =
                TextAttributes(null, null, null, null, 0)


            val highlighter = markupModel.addRangeHighlighter(
                start,
                end,
                HighlighterLayer.SELECTION,
                textAttributes,
                HighlighterTargetArea.EXACT_RANGE
            )
            highlighters.put("underline", highlighter)

        }
    }
}
