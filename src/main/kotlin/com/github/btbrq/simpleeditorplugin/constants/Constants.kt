package com.github.btbrq.simpleeditorplugin.constants

import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.util.Key

class Constants {
    companion object {
        public val MYDATA: Key<MutableList<RangeHighlighter>> = Key.create("MYDATALIST")
    }

}
