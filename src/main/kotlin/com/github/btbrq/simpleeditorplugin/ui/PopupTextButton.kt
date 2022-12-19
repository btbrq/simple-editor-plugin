package com.github.btbrq.simpleeditorplugin.ui

import com.github.btbrq.simpleeditorplugin.constants.Constants.Companion.BUTTON_SIZE
import java.awt.Font
import javax.swing.JButton

class PopupTextButton(text: String, font: Font, action: () -> Unit, tooltipText: String) : JButton(text) {
    init {
        this.font = font
        this.toolTipText = tooltipText
        minimumSize = BUTTON_SIZE
        maximumSize = BUTTON_SIZE
        preferredSize = BUTTON_SIZE
        addActionListener { action.invoke() }
    }
}
