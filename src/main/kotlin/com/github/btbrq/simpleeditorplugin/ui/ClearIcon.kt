package com.github.btbrq.simpleeditorplugin.ui

import com.github.btbrq.simpleeditorplugin.constants.Constants.Companion.COLOR_SIZE
import com.github.btbrq.simpleeditorplugin.domain.HighlighterType
import com.intellij.icons.AllIcons
import com.intellij.util.Consumer
import javax.swing.JButton

class ClearIcon (type: HighlighterType, action: Consumer<HighlighterType>): JButton(AllIcons.Actions.DeleteTag) {
    init {
        addActionListener { action.consume(type) }
        toolTipText = "Clear"
        minimumSize = COLOR_SIZE
        maximumSize = COLOR_SIZE
        preferredSize = COLOR_SIZE
    }
}
