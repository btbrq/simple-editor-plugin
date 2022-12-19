package com.github.btbrq.simpleeditorplugin.ui

import com.github.btbrq.simpleeditorplugin.constants.Constants.Companion.BUTTON_SIZE
import javax.swing.Icon
import javax.swing.JButton

class PopupIconButton(icon: Icon, tooltipText: String) : JButton(icon) {
    init {
        this.toolTipText = tooltipText
        minimumSize = BUTTON_SIZE
        maximumSize = BUTTON_SIZE
        preferredSize = BUTTON_SIZE
    }
}
