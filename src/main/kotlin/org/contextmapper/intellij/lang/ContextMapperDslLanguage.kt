package org.contextmapper.intellij.lang

import com.intellij.lang.Language

class ContextMapperDslLanguage : Language("Context Mapper DSL") {
    companion object {
        val INSTANCE = ContextMapperDslLanguage()
    }
}
