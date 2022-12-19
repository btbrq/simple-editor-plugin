package com.github.btbrq.simpleeditorplugin.ui

import com.github.btbrq.simpleeditorplugin.constants.Constants.Fonts.Companion.boldFont
import com.github.btbrq.simpleeditorplugin.constants.Constants.Fonts.Companion.italicFont
import com.github.btbrq.simpleeditorplugin.constants.Constants.Fonts.Companion.underlineFont
import com.github.btbrq.simpleeditorplugin.domain.HighlighterType
import com.github.btbrq.simpleeditorplugin.styling.Styler
import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopupFactory
import java.awt.event.ActionEvent
import javax.swing.JButton
import javax.swing.JPanel

class SimpleEditorPopup(editor: Editor) : JPanel() {

    init {
        val selectionModel = editor.selectionModel
        val styler = Styler(editor)
        if (!selectionModel.hasSelection()) {
            selectionModel.selectWordAtCaret(false)
        }

        val colorButton = PopupIconButton(AllIcons.FileTypes.Text, "Text color")
        colorButton.addActionListener(popupAction(editor, HighlighterType.COLOR, colorButton))
        add(colorButton)

        val backgroundButton = PopupIconButton(AllIcons.Actions.Highlighting, "Highlight")
        backgroundButton.addActionListener(popupAction(editor, HighlighterType.HIGHLIGHT, colorButton))
        add(backgroundButton)

        val underlineButton = PopupTextButton(
            "U",
            underlineFont(backgroundButton.font),
            { styler.underline() },
            "Underline"
        )
        add(underlineButton)

        val boldButton = PopupTextButton("B", boldFont(backgroundButton.font), { styler.bold() }, "Bold")
        add(boldButton)

        val italicButton = PopupTextButton("I", italicFont(backgroundButton.font), { styler.italic() }, "Italic")
        add(italicButton)
    }

    private fun popupAction(
        editor: Editor,
        highlighterType: HighlighterType,
        button: JButton
    ): (e: ActionEvent) -> Unit = {
        JBPopupFactory.getInstance().createComponentPopupBuilder(ColorsPopup(editor, highlighterType), null)
            .createPopup()
            .showUnderneathOf(button)
    }


}
