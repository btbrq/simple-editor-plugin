package com.github.btbrq.simpleeditorplugin.styling

import com.github.btbrq.simpleeditorplugin.domain.HighlighterType
import com.intellij.util.Consumer
import java.awt.event.MouseEvent
import java.awt.event.MouseListener

class ClearListener(private val type: HighlighterType, private val action: Consumer<HighlighterType?>) : MouseListener {
    override fun mouseClicked(e: MouseEvent) {
        action.consume(type)
    }

    override fun mousePressed(e: MouseEvent) {}
    override fun mouseReleased(e: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}
}
