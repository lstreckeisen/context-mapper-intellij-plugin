package org.contextmapper.intellij.lsp4ij.server

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.extensions.PluginDescriptor
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider

const val CONTEXT_MAPPER_SERVER_ID = "cml-language-server"

class ContextMapperDslServer(
    pluginDescriptor: PluginDescriptor
) : OSProcessStreamConnectionProvider() {
    init {
        val command =
            GeneralCommandLine("node", pluginDescriptor.pluginPath.resolve("lib/lsp/index.js").toString(), "--stdio")
        commandLine = command
    }
}
