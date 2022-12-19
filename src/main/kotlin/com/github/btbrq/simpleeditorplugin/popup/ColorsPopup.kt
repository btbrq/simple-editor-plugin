package com.github.btbrq.simpleeditorplugin.popup

import com.github.btbrq.simpleeditorplugin.domain.HighlighterType
import com.github.btbrq.simpleeditorplugin.styling.Styler
import com.intellij.openapi.editor.Editor
import com.intellij.ui.JBColor
import com.intellij.ui.JBColor.*
import com.intellij.util.Consumer
import javax.swing.BoxLayout
import javax.swing.JPanel

class ColorsPopup(editor: Editor, highlighterType: HighlighterType): JPanel() {
    init {
        layout = BoxLayout(this, BoxLayout.LINE_AXIS)
        val styler = Styler(editor)
        val function = actionFunction(highlighterType, styler)
        val clearAction = Consumer { type: HighlighterType? -> styler.clear(type!!) }

        add(ColorIcon(BLUE, function))
        add(ColorIcon(GREEN, function))
        add(ColorIcon(YELLOW, function))
        add(ColorIcon(RED, function))
        add(ColorIcon(PINK, function))
        add(ClearIcon(BLACK, highlighterType, clearAction))
    }

    private fun actionFunction(highlighterType: HighlighterType, styler: Styler): Consumer<JBColor?> {
        val colorConsumer = Consumer { color: JBColor? ->
            styler.color(
                color!!
            )
        }
        val backgroundConsumer = Consumer { color: JBColor? ->
            styler.background(
                color!!
            )
        }
        return if (HighlighterType.COLOR === highlighterType) colorConsumer else backgroundConsumer
    }
}
