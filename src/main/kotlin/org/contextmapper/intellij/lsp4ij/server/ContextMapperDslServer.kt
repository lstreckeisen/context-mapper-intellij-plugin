package org.contextmapper.intellij.lsp4ij.server

import com.intellij.execution.configurations.GeneralCommandLine
import com.intellij.openapi.extensions.PluginDescriptor
import com.redhat.devtools.lsp4ij.server.OSProcessStreamConnectionProvider
import java.nio.file.Files
import kotlin.io.path.Path

internal const val CONTEXT_MAPPER_NODE_ENV = "CONTEXT_MAPPER_NODE"

class ContextMapperDslServer(
    pluginDescriptor: PluginDescriptor
) : OSProcessStreamConnectionProvider() {
    init {
        val command =
            GeneralCommandLine(
                resolveNodeExecutable(),
                pluginDescriptor.pluginPath.resolve("lib/lsp/index.js").toString(),
                "--stdio",
            )
        commandLine = command
    }
}

internal fun resolveNodeExecutable(
    environment: Map<String, String> = System.getenv(),
    isExecutable: (String) -> Boolean = { Files.isExecutable(Path(it)) }
): String {
    environment[CONTEXT_MAPPER_NODE_ENV]
        ?.takeIf { it.isNotBlank() }
        ?.let { return it }

    return listOf(
        "/opt/homebrew/bin/node",
        "/usr/local/bin/node",
        "/usr/bin/node",
    ).firstOrNull(isExecutable) ?: "node"
}
