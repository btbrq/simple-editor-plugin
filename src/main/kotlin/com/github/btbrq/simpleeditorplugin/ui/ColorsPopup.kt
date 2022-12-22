package com.github.btbrq.simpleeditorplugin.ui

import com.github.btbrq.simpleeditorplugin.domain.HighlighterType
import com.github.btbrq.simpleeditorplugin.styling.Styler
import com.intellij.ui.DarculaColors
import com.intellij.util.Consumer
import java.awt.Color
import java.awt.Color.*
import java.awt.GridLayout
import javax.swing.JPanel

class ColorsPopup(styler: Styler, highlighterType: HighlighterType) : JPanel() {
    init {
        layout = GridLayout(0, 6)
        val function = actionFunction(highlighterType, styler)
        val clearAction = Consumer { type: HighlighterType -> styler.clear(type) }

        add(ColorIcon(RED, function))
        add(ColorIcon(DarculaColors.RED, function))
        add(ColorIcon(BLUE, function))
        add(ColorIcon(DarculaColors.BLUE, function))
        add(ColorIcon(WHITE, function))
        add(ColorIcon(BLACK, function))
        add(ColorIcon(GRAY, function))
        add(ColorIcon(LIGHT_GRAY, function))
        add(ColorIcon(DARK_GRAY, function))
        add(ColorIcon(PINK, function))
        add(ColorIcon(ORANGE, function))
        add(ColorIcon(YELLOW, function))
        add(ColorIcon(GREEN, function))
        add(ColorIcon(Color(98, 150, 85), function))
        add(ColorIcon(MAGENTA, function))
        add(ColorIcon(Color(151, 118, 169), function))
        add(ColorIcon(CYAN, function))
        add(ColorIcon(Color(0, 137, 137), function))

        add(ClearIcon(highlighterType, clearAction))
    }

    private fun actionFunction(highlighterType: HighlighterType, styler: Styler): Consumer<Color> {
        val colorConsumer = Consumer { color: Color ->
            styler.color(
                color
            )
        }
        val backgroundConsumer = Consumer { color: Color ->
            styler.background(
                color
            )
        }
        return if (HighlighterType.COLOR === highlighterType) colorConsumer else backgroundConsumer
    }
}
