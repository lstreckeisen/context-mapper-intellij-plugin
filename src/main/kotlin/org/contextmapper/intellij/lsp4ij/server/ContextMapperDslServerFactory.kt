package org.contextmapper.intellij.lsp4ij.server

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import org.contextmapper.intellij.PLUGIN_ID

class ContextMapperDslServerFactory : LanguageServerFactory {
    override fun createConnectionProvider(project: Project): StreamConnectionProvider {
        val pluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID))
        requireNotNull(pluginDescriptor) { "Could not find Context Mapper plugin descriptor!" }

        return ContextMapperDslServer(pluginDescriptor)
    }

    override fun createLanguageClient(project: Project): LanguageClientImpl = ContextMapperDslClient(project)
}
