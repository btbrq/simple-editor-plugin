package com.github.btbrq.simpleeditorplugin.styling

import com.intellij.util.Consumer
import java.awt.Color
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class ColorHighlightListener(private val color: Color, private val action: Consumer<Color>) : MouseListener {
    override fun mouseClicked(e: MouseEvent) {
        action.consume(color)
    }

    override fun mousePressed(e: MouseEvent) {}
    override fun mouseReleased(e: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}
}
