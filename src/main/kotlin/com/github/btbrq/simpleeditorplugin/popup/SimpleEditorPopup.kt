package com.github.btbrq.simpleeditorplugin.popup

import com.github.btbrq.simpleeditorplugin.styling.Styler
import com.intellij.openapi.editor.Editor
import com.intellij.ui.JBColor
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JPanel

class SimpleEditorPopup(editor: Editor) : JPanel() {

    init {
        val selectionModel = editor.selectionModel
        val styler = Styler(editor)
        if (!selectionModel.hasSelection()) {
            selectionModel.selectWordAtCaret(false)
        }

        val dialogPanel = JPanel()
        dialogPanel.layout = BoxLayout(dialogPanel, BoxLayout.PAGE_AXIS)
        val jButton = JButton("green")
        dialogPanel.add(jButton)
        jButton.addActionListener({ styler.color(JBColor.GREEN) })

        val jButtonb = JButton("blue")
        dialogPanel.add(jButtonb)
        jButtonb.addActionListener({ styler.color(JBColor.BLUE) })

        val jButtonBackground = JButton("green background")
        dialogPanel.add(jButtonBackground)
        jButtonBackground.addActionListener({ styler.background(JBColor.GREEN) })

        val jButtonBackground2 = JButton("blue background")
        dialogPanel.add(jButtonBackground2)
        jButtonBackground2.addActionListener({ styler.background(JBColor.BLUE) })


        val jButton1 = JButton("underline")
        jButton1.addActionListener({ styler.underline() })
        dialogPanel.add(jButton1)

        val jButtonBold = JButton("bold")
        jButtonBold.addActionListener({ styler.bold() })
        dialogPanel.add(jButtonBold)

        val jButtonItalic = JButton("italic")
        jButtonItalic.addActionListener({ styler.italic() })
        dialogPanel.add(jButtonItalic)

        add(dialogPanel)
    }


}
