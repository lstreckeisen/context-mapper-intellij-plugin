package org.contextmapper.intellij.lsp4ij

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.extensions.PluginDescriptor
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger(CMLServer::class.java)

private const val WINDOWS_LSP_EXECUTABLE = "context-mapper-lsp.bat"
private const val LINUX_LSP_EXECUTABLE = "context-mapper-lsp"
private const val LSP_EXECUTABLE_PATH = "lib/lsp/bin"

class CMLServer(
    pluginDescriptor: PluginDescriptor,
    isOsWindows: Boolean
) : OSProcessStreamConnectionProvider() {
    init {
        val startScript =
            if (isOsWindows) {
                WINDOWS_LSP_EXECUTABLE
            } else {
                LINUX_LSP_EXECUTABLE
            }

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
