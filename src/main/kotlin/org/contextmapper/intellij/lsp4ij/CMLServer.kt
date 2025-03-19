package org.contextmapper.intellij.lsp4ij

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(CMLServer::class.java)

class CMLServer : OSProcessStreamConnectionProvider() {
    init {
        val pluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId("org.contextmapper.intellij"))
        requireNotNull(pluginDescriptor) { "Could not find Context Mapper plugin descriptor!" }

        val lspResourcePath = pluginDescriptor.pluginPath.resolve("lib/lsp/bin/context-mapper-lsp")
            .toAbsolutePath()
            .toString()

        logger.debug("Language server executable: $lspResourcePath")

        val command = GeneralCommandLine(lspResourcePath)
        commandLine = command
    }
}