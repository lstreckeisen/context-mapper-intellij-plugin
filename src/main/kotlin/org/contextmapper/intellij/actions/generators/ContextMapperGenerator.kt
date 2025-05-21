package org.contextmapper.intellij.actions.generators

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.commands.CommandExecutor
import com.redhat.devtools.lsp4ij.commands.LSPCommandContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.contextmapper.intellij.actions.LspCommandExecutor
import org.contextmapper.intellij.utils.CONTEXT_MAPPER_SERVER_ID
import org.eclipse.lsp4j.Command
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException
import java.util.concurrent.CompletableFuture

class ContextMapperGenerator(
    private val commandExecutor: LspCommandExecutor
) {
    fun generate(
        project: Project,
        command: Command
    ): CompletableFuture<Result<GeneratorResult>> {
        val future = CompletableFuture<Result<GeneratorResult>>()

        val context = LSPCommandContext(command, project)

        val languageServer =
            try {
                LanguageServerManager.getInstance(project).getLanguageServer(CONTEXT_MAPPER_SERVER_ID)
                    .join()
            } catch (ex: Exception) {
                future.complete(
                    Result.failure(
                        ContextMapperGeneratorException(
                            "Could not find language server instance.",
                            ex,
                        ),
                    ),
                )
                return future
            }
        context.preferredLanguageServerId = CONTEXT_MAPPER_SERVER_ID
        context.preferredLanguageServer = languageServer

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

                val generatedFilesResult = extractGeneratedFiles(returnedValue)
                if (generatedFilesResult.isFailure) {
                    future.complete(Result.failure(generatedFilesResult.exceptionOrNull()!!))
                }

                val generatedFiles = generatedFilesResult.getOrNull()!!
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

    private fun extractGeneratedFiles(returnedValue: Any): Result<List<String>> {
        when (returnedValue) {
            is List<*> -> {
                val files = returnedValue.filterIsInstance<String>()
                if (files.size != returnedValue.size) {
                    return Result.failure(ContextMapperGeneratorException("Generator returned unexpected result type."))
                }
                return Result.success(files)
            }

            is ResponseErrorException -> {
                // ResponseErrors are already handled by LSP4IJ
                return Result.failure(HandledGeneratorException())
            }

            else -> {
                return Result.failure(ContextMapperGeneratorException("Generator returned unexpected result type."))
            }
        }
    }
}
