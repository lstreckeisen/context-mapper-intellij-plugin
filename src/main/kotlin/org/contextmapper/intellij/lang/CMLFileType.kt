package org.contextmapper.intellij.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

class CMLFileType : LanguageFileType(CMLLanguage.INSTANCE) {
    override fun getName(): @NonNls String {
        return "CML File"
    }

    override fun getDescription(): @NlsContexts.Label String {
        return "ContextMapper language file"
    }

    override fun getDefaultExtension(): @NlsSafe String {
        return "cml"
    }

    override fun getIcon(): Icon? {
        return CMLIcons.FILE
    }

    companion object {
        val INSTANCE = CMLFileType()
    }
}