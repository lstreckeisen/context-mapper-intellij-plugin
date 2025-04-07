package org.contextmapper.intellij.lsp4ij

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.extensions.PluginDescriptor
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider

class CMLServer(
    pluginDescriptor: PluginDescriptor
) : OSProcessStreamConnectionProvider() {
    init {
        val command =
            GeneralCommandLine("node", pluginDescriptor.pluginPath.resolve("lib/lsp/index.cjs").toString(), "--stdio")
        commandLine = command
    }
}
