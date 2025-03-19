package org.contextmapper.intellij.lsp4ij

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider
import org.apache.commons.lang3.SystemUtils
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(CMLServer::class.java)

class CMLServer : OSProcessStreamConnectionProvider() {
    init {
        val startScript =
            if (SystemUtils.IS_OS_WINDOWS) {
                "context-mapper-lsp.bat"
            } else {
                "context-mapper-lsp"
            }

        val pluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId("org.contextmapper.intellij"))
        requireNotNull(pluginDescriptor) { "Could not find Context Mapper plugin descriptor!" }

        val lspResourcePath =
            pluginDescriptor.pluginPath.resolve("lib/lsp/bin/$startScript")
                .toAbsolutePath()
                .toString()

        logger.debug("Language server executable: $lspResourcePath")

        val command = GeneralCommandLine(lspResourcePath)
        commandLine = command
    }
}
