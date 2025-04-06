package org.contextmapper.intellij.lang

import com.intellij.lang.Language

class CMLLanguage : Language("Context Mapper DSL") {
    companion object {
        val INSTANCE = CMLLanguage()
    }
}
