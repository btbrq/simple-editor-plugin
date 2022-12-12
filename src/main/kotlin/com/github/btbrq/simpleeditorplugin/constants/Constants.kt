package com.github.btbrq.simpleeditorplugin.constants

import com.github.btbrq.simpleeditorplugin.domain.TypedRangeHighlighter
import com.intellij.openapi.util.Key

class Constants {
    companion object {
        public val MYDATA: Key<MutableList<TypedRangeHighlighter>> = Key.create("MYDATALIST")
    }

}
