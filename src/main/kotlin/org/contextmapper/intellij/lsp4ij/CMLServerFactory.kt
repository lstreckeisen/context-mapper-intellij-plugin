package org.contextmapper.intellij.lsp4ij

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerFactory
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl
import com.redhat.devtools.lsp4ij.server.StreamConnectionProvider
import org.apache.commons.lang3.SystemUtils

private const val PLUGIN_ID = "org.contextmapper.intellij"

class CMLServerFactory : LanguageServerFactory {
    override fun createConnectionProvider(p0: Project): StreamConnectionProvider {
        val pluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID))
        requireNotNull(pluginDescriptor) { "Could not find Context Mapper plugin descriptor!" }

        return CMLServer(pluginDescriptor, SystemUtils.IS_OS_WINDOWS)
    }

    override fun createLanguageClient(project: Project): LanguageClientImpl = CMLClient(project)
}
