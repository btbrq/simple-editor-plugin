package com.github.btbrq.simpleeditorplugin.popup

import com.github.btbrq.simpleeditorplugin.domain.HighlighterType
import com.github.btbrq.simpleeditorplugin.styling.ClearMouseClickListener
import com.intellij.ui.JBColor
import com.intellij.util.Consumer
import java.awt.Dimension
import javax.swing.JPanel

class ClearIcon (color: JBColor, type: HighlighterType, action: Consumer<HighlighterType?>): JPanel() {
    init {
        background = color
        addMouseListener(ClearMouseClickListener(type, action))
        minimumSize = Dimension(20, 20)
        maximumSize = Dimension(20, 20)
        preferredSize = Dimension(20, 20)
    }
}
