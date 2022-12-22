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
        val styler = Styler(editor)

        val colorButton = PopupIconButton(AllIcons.FileTypes.Text, "Text color")
        colorButton.addActionListener(popupAction(styler, HighlighterType.COLOR, colorButton))
        add(colorButton)

        val backgroundButton = PopupIconButton(AllIcons.Actions.Highlighting, "Highlight")
        backgroundButton.addActionListener(popupAction(styler, HighlighterType.HIGHLIGHT, colorButton))
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

        val clearAllButton = ClearAllIcon { styler.clearAll() }
        add(clearAllButton)
    }

    private fun popupAction(
        styler: Styler,
        highlighterType: HighlighterType,
        button: JButton
    ): (e: ActionEvent) -> Unit = {
        JBPopupFactory.getInstance().createComponentPopupBuilder(ColorsPopup(styler, highlighterType), null)
            .createPopup()
            .showUnderneathOf(button)
    }


}
