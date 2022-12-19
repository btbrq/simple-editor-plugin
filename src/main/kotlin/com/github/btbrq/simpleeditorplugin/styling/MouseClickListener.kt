package com.github.btbrq.simpleeditorplugin.styling

import com.intellij.ui.JBColor
import com.intellij.util.Consumer
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class MouseClickListener(private val color: JBColor, private val action: Consumer<JBColor?>) : MouseListener {
    override fun mouseClicked(e: MouseEvent) {
        action.consume(color)
    }

    override fun mousePressed(e: MouseEvent) {}
    override fun mouseReleased(e: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}
}
