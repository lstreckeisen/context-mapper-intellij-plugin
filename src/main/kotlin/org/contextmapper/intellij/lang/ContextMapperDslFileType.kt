package org.contextmapper.intellij.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import org.jetbrains.annotations.NonNls
import javax.swing.Icon

class ContextMapperDslFileType : LanguageFileType(ContextMapperDslLanguage.INSTANCE) {
    override fun getName(): @NonNls String = "Context Mapper DSL"

    override fun getDescription(): @NlsContexts.Label String = "Context Mapper DSL"

    @Suppress("UnstableApiUsage") // NlsSafe is used by interface
    override fun getDefaultExtension(): @NlsSafe String = "cml"

    override fun getIcon(): Icon = ContextMapperDslIcons.FILE

    companion object {
        @JvmField
        @Suppress("Unused") // required for plugin.xml
        val INSTANCE = ContextMapperDslFileType()
    }
}
