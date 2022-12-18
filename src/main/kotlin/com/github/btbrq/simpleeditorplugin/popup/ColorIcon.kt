package com.github.btbrq.simpleeditorplugin.popup

import com.github.btbrq.simpleeditorplugin.styling.MouseClickListener
import com.intellij.ui.JBColor
import java.awt.Dimension
import javax.swing.JPanel

class ColorIcon(color: JBColor, function: () -> Unit): JPanel() {
    init {
        background = color
        addMouseListener(MouseClickListener(color, function))
        minimumSize = Dimension(15, 15)
        maximumSize = Dimension(15, 15)
        preferredSize = Dimension(15, 15)
    }
}
