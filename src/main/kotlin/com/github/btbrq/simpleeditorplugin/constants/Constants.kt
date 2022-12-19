package com.github.btbrq.simpleeditorplugin.constants

import com.github.btbrq.simpleeditorplugin.domain.TypedRangeHighlighter
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.util.Key
import java.awt.Dimension
import java.awt.Font
import java.awt.font.TextAttribute

class Constants {
    companion object {
        val STYLES: Key<MutableList<TypedRangeHighlighter>> = Key.create("SIMPLE_EDITOR_STYLES")
        val BUTTON_SIZE: Dimension = Dimension(30, 30)
        const val HIGHLIGHTER_LAYER: Int = HighlighterLayer.SELECTION
        const val FONT_SIZE: Int = 18
    }

    class Fonts {
        companion object {
            fun underlineFont(
                baseFont: Font,
            ): Font {
                val attributes = HashMap(baseFont.attributes)
                attributes[TextAttribute.SIZE] = FONT_SIZE
                attributes[TextAttribute.UNDERLINE] = TextAttribute.UNDERLINE_ON
                return baseFont.deriveFont(attributes)
            }

            fun italicFont(baseFont: Font) = Font(baseFont.name, Font.ITALIC, FONT_SIZE)

            fun boldFont(baseFont: Font) = Font(baseFont.name, Font.BOLD, FONT_SIZE)
        }


    }

}
