package org.contextmapper.intellij.generators

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.commands.CommandExecutor
import com.redhat.devtools.lsp4ij.commands.LSPCommandContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.contextmapper.intellij.actions.LspCommandExecutor
import org.contextmapper.intellij.lsp4ij.server.CONTEXT_MAPPER_SERVER_ID
import org.eclipse.lsp4j.Command
import java.util.concurrent.CompletableFuture

class ContextMapperGenerator(
    private val commandExecutor: LspCommandExecutor
) {
    fun generate(
        project: Project,
        command: Command
    ): CompletableFuture<Result<GeneratorResult>> {
        val context = LSPCommandContext(command, project)
        context.preferredLanguageServerId = CONTEXT_MAPPER_SERVER_ID

        val future = CompletableFuture<Result<GeneratorResult>>()

        val response = commandExecutor(context)
        CoroutineScope(Dispatchers.IO).launch {
            processResponse(response, future)
        }
        return future
    }

    private fun processResponse(
        response: CommandExecutor.LSPCommandResponse,
        future: CompletableFuture<Result<GeneratorResult>>
    ) {
        val result = response.response

        if (result != null) {
            try {
                val returnedValue = result.join()

                val generatedFiles: List<String> =
                    if (returnedValue is List<*>) {
                        val files = returnedValue.filterIsInstance<String>()
                        if (files.size != returnedValue.size) {
                            future.complete(Result.failure(ContextMapperGeneratorException("Generator returned unexpected result type.")))
                            return
                        }
                        files
                    } else {
                        future.complete(Result.failure(ContextMapperGeneratorException("Generator returned unexpected result type.")))
                        return
                    }

                if (generatedFiles.isEmpty()) {
                    future.complete(Result.success(GeneratorResult(listOf())))
                } else {
                    future.complete(Result.success(GeneratorResult(generatedFiles)))
                }
            } catch (ex: Exception) {
                future.complete(
                    Result.failure(
                        ContextMapperGeneratorException(
                            "Generator failed with exception: ${ex.message}",
                            ex,
                        ),
                    ),
                )
            }
        } else {
            future.complete(Result.failure(ContextMapperGeneratorException("Generator failed without error")))
        }
    }
}
