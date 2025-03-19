package org.contextmapper.intellij.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider

class CMLServerFactory : LanguageServerFactory {
    override fun createConnectionProvider(p0: Project): StreamConnectionProvider {
        return CMLServer()
    }

    override fun createLanguageClient(project: Project): LanguageClientImpl {
        return CMLClient(project)
    }
}
