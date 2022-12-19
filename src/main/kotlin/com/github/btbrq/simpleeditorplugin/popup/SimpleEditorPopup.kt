package com.github.btbrq.simpleeditorplugin.popup

import com.github.btbrq.simpleeditorplugin.domain.HighlighterType
import com.github.btbrq.simpleeditorplugin.styling.Styler
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopupFactory
import java.awt.Dimension
import java.awt.Font
import java.awt.font.TextAttribute
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

        val jButtonColor = JButton(AllIcons.FileTypes.Text)
        jButtonColor.minimumSize = Dimension(40, 40)
        jButtonColor.maximumSize = Dimension(40, 40)
        jButtonColor.preferredSize = Dimension(40, 40)
        dialogPanel.add(jButtonColor)
        jButtonColor.addActionListener {
            JBPopupFactory.getInstance().createComponentPopupBuilder(ColorsPopup(editor, HighlighterType.COLOR), null)
                .createPopup()
                .showUnderneathOf(jButtonColor)
        }

        val jButtonBackground = JButton(AllIcons.Actions.Highlighting)
        dialogPanel.add(jButtonBackground)
        jButtonBackground.minimumSize = Dimension(40, 40)
        jButtonBackground.maximumSize = Dimension(40, 40)
        jButtonBackground.preferredSize = Dimension(40, 40)
        jButtonBackground.addActionListener {
            JBPopupFactory.getInstance()
                .createComponentPopupBuilder(ColorsPopup(editor, HighlighterType.HIGHLIGHT), null)
                .createPopup()
                .showUnderneathOf(jButtonBackground)
        }

        val jButton1 = JButton("U")
        var btnFont = jButton1.font
        val attributes = HashMap(font.attributes)
        attributes.put(TextAttribute.SIZE, 20)
        attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON)
        btnFont = btnFont.deriveFont(attributes)
        jButton1.font = btnFont
        jButton1.minimumSize = Dimension(40, 40)
        jButton1.maximumSize = Dimension(40, 40)
        jButton1.preferredSize = Dimension(40, 40)
        jButton1.addActionListener { styler.underline() }
        dialogPanel.add(jButton1)

        val jButtonBold = JButton("B")
        jButtonBold.font = Font(jButtonBold.font.name, Font.BOLD, 20)
        jButtonBold.minimumSize = Dimension(40, 40)
        jButtonBold.maximumSize = Dimension(40, 40)
        jButtonBold.preferredSize = Dimension(40, 40)
        jButtonBold.addActionListener { styler.bold() }
        dialogPanel.add(jButtonBold)

        val jButtonItalic = JButton("I")
        jButtonItalic.font = Font(jButtonItalic.font.name, Font.ITALIC, 20)
        jButtonItalic.minimumSize = Dimension(40, 40)
        jButtonItalic.maximumSize = Dimension(40, 40)
        jButtonItalic.preferredSize = Dimension(40, 40)
        jButtonItalic.addActionListener { styler.italic() }
        dialogPanel.add(jButtonItalic)

        add(dialogPanel)
    }


}
