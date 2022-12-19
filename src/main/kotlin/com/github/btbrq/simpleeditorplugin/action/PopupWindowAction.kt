package com.github.btbrq.simpleeditorplugin.action

import com.github.btbrq.simpleeditorplugin.ui.SimpleEditorPopup
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.popup.JBPopupFactory


class PopupWindowAction : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val editor: Editor = event.getRequiredData(CommonDataKeys.EDITOR)

        JBPopupFactory.getInstance().createComponentPopupBuilder(SimpleEditorPopup(editor), null)
            .createPopup()
            .showInBestPositionFor(editor)
    }

}
