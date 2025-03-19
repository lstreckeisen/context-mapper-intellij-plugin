package org.contextmapper.intellij.lsp4ij

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider
import org.apache.commons.lang3.SystemUtils
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(CMLServer::class.java)

private const val WINDOWS_LSP_EXECUTABLE = "context-mapper-lsp.bat"
private const val LINUX_LSP_EXECUTABLE = "context-mapper-lsp"
private const val LSP_EXECUTABLE_PATH = "lib/lsp/bin"

private const val PLUGIN_ID = "org.contextmapper.intellij"

class CMLServer : OSProcessStreamConnectionProvider() {
    init {
        val startScript =
            if (SystemUtils.IS_OS_WINDOWS) {
                WINDOWS_LSP_EXECUTABLE
            } else {
                LINUX_LSP_EXECUTABLE
            }

        val pluginDescriptor = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID))
        requireNotNull(pluginDescriptor) { "Could not find Context Mapper plugin descriptor!" }

        val lspResourcePath =
            pluginDescriptor.pluginPath
                .resolve("$LSP_EXECUTABLE_PATH/$startScript")
                .toAbsolutePath()
                .toString()

        logger.debug("Language server executable: $lspResourcePath")

        val command = GeneralCommandLine(lspResourcePath)
        commandLine = command
    }
}
