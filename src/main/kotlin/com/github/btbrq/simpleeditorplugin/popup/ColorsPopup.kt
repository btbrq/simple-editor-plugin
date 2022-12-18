package com.github.btbrq.simpleeditorplugin.popup

import com.github.btbrq.simpleeditorplugin.domain.HighlighterType
import com.github.btbrq.simpleeditorplugin.styling.Styler
import com.intellij.openapi.editor.Editor
import com.intellij.ui.JBColor.*
import javax.swing.BoxLayout
import javax.swing.JPanel

class ColorsPopup(editor: Editor, val highlighterType: HighlighterType): JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        val styler = Styler(editor)
//        val function = highlighterType == HighlighterType.COLOR ?: { styler.color(BLUE) }
        add(ColorIcon(BLUE) { styler.color(BLUE) })
        add(ColorIcon(GREEN) { styler.color(GREEN) })
        add(ColorIcon(YELLOW) { styler.color(YELLOW) })
        add(ColorIcon(RED) { styler.color(RED) })
        add(ColorIcon(PINK) { styler.color(PINK) })
    }
}
