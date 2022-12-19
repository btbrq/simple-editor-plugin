package com.github.btbrq.simpleeditorplugin.ui

import com.github.btbrq.simpleeditorplugin.constants.Constants.Companion.COLOR_SIZE
import com.github.btbrq.simpleeditorplugin.domain.HighlighterType
import com.intellij.util.Consumer
import javax.swing.Icon
import javax.swing.JButton

class ClearIcon (icon: Icon, type: HighlighterType, action: Consumer<HighlighterType>): JButton(icon) {
    init {
        addActionListener { action.consume(type) }
        minimumSize = COLOR_SIZE
        maximumSize = COLOR_SIZE
        preferredSize = COLOR_SIZE
    }
}
