package com.github.btbrq.simpleeditorplugin.ui

import com.github.btbrq.simpleeditorplugin.constants.Constants.Companion.BUTTON_SIZE
import com.intellij.icons.AllIcons
import javax.swing.JButton

class ClearAllIcon(action: () -> Unit) : JButton(AllIcons.Actions.DeleteTag) {
    init {
        addActionListener { action.invoke() }
        toolTipText = "Clear all"
        minimumSize = BUTTON_SIZE
        maximumSize = BUTTON_SIZE
        preferredSize = BUTTON_SIZE
    }
}
