package com.github.btbrq.simpleeditorplugin.popup

import com.github.btbrq.simpleeditorplugin.styling.MouseClickListener
import com.intellij.ui.JBColor
import com.intellij.util.Consumer
import java.awt.Dimension
import javax.swing.JPanel

class ColorIcon(color: JBColor, action: Consumer<JBColor?>): JPanel() {
    init {
        background = color
        addMouseListener(MouseClickListener(color, action))
        minimumSize = Dimension(20, 20)
        maximumSize = Dimension(20, 20)
        preferredSize = Dimension(20, 20)
    }
}
