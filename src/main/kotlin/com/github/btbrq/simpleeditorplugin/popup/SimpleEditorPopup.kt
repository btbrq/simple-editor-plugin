package com.github.btbrq.simpleeditorplugin.popup

import com.github.btbrq.simpleeditorplugin.domain.HighlighterType
import com.github.btbrq.simpleeditorplugin.styling.Styler
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopupFactory
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
        dialogPanel.layout = BoxLayout(dialogPanel, BoxLayout.LINE_AXIS)

        val jButtonColor = JButton("A")
        dialogPanel.add(jButtonColor)
        jButtonColor.addActionListener {
            JBPopupFactory.getInstance().createComponentPopupBuilder(ColorsPopup(editor, HighlighterType.COLOR), null)
                .createPopup()
                .showUnderneathOf(this)
        }

        val jButtonBackground = JButton("H")
        dialogPanel.add(jButtonBackground)
        jButtonBackground.addActionListener {
            JBPopupFactory.getInstance().createComponentPopupBuilder(ColorsPopup(editor, HighlighterType.HIGHLIGHT), null)
                .createPopup()
                .showUnderneathOf(this)
        }

        val jButton1 = JButton("U")
        jButton1.addActionListener { styler.underline() }
        dialogPanel.add(jButton1)

        val jButtonBold = JButton("B")
        jButtonBold.addActionListener { styler.bold() }
        dialogPanel.add(jButtonBold)

        val jButtonItalic = JButton("I")
        jButtonItalic.addActionListener { styler.italic() }
        dialogPanel.add(jButtonItalic)

        add(dialogPanel)
    }


}
