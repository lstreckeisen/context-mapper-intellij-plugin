package org.contextmapper.intellij.actions

import com.redhat.devtools.lsp4ij.commands.CommandExecutor
import com.redhat.devtools.lsp4ij.commands.LSPCommandContext

typealias LspCommandExecutor = (LSPCommandContext) -> CommandExecutor.LSPCommandResponse

val lspCommandExecutor: LspCommandExecutor = { context -> CommandExecutor.executeCommand(context) }

const val PLANT_UML_GENERATOR_COMMAND = "org.contextmapper.GeneratePlantUML"
