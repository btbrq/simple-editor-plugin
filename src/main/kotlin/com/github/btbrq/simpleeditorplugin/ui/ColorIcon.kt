package com.github.btbrq.simpleeditorplugin.ui

import com.github.btbrq.simpleeditorplugin.constants.Constants.Companion.COLOR_SIZE
import com.github.btbrq.simpleeditorplugin.styling.ColorHighlightListener
import com.intellij.util.Consumer
import java.awt.Color
import javax.swing.JPanel

class ColorIcon(color: Color, action: Consumer<Color>): JPanel() {
    init {
        background = color
        addMouseListener(ColorHighlightListener(color, action))
        minimumSize = COLOR_SIZE
        maximumSize = COLOR_SIZE
        preferredSize = COLOR_SIZE
    }
}
