package org.contextmapper.intellij.actions.generators

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerItem
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.commands.LSPCommandContext
import org.contextmapper.intellij.actions.LspCommandExecutor
import org.contextmapper.intellij.utils.CONTEXT_MAPPER_SERVER_ID
import org.eclipse.lsp4j.Command
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException
import java.util.concurrent.CompletableFuture

class ContextMapperGenerator(
    private val commandExecutor: LspCommandExecutor,
    private val languageServerManager: LanguageServerManager
) {
    fun generate(
        project: Project,
        command: Command
    ): CompletableFuture<Result<GeneratorResult>> {
        val future = CompletableFuture<Result<GeneratorResult>>()

        val context = LSPCommandContext(command, project)

        languageServerManager.getLanguageServer(CONTEXT_MAPPER_SERVER_ID)
            .thenAcceptAsync { languageServer ->
                executeCommand(future, context, languageServer)
            }
            .exceptionally { throwable ->
                future.complete(
                    Result.failure(
                        ContextMapperGeneratorException(
                            "Could not find language server instance.",
                            throwable,
                        ),
                    ),
                )
                null
            }

        return future
    }

    private fun executeCommand(
        future: CompletableFuture<Result<GeneratorResult>>,
        context: LSPCommandContext,
        languageServerItem: LanguageServerItem?
    ) {
        if (languageServerItem == null) {
            future.complete(
                Result.failure(
                    ContextMapperGeneratorException(
                        "Could not find language server instance.",
                    ),
                ),
            )
            return
        }
        context.preferredLanguageServerId = CONTEXT_MAPPER_SERVER_ID
        context.preferredLanguageServer = languageServerItem

        val response = commandExecutor(context).response
        if (response == null) {
            future.complete(Result.failure(ContextMapperGeneratorException("Generator failed without error")))
            return
        }

        response.thenAcceptAsync { generatorResult -> processResponse(future, generatorResult) }
            .exceptionally { throwable ->
                future.complete(
                    Result.failure(
                        ContextMapperGeneratorException(
                            "Generator failed with exception: ${throwable.message}",
                            throwable,
                        ),
                    ),
                )
                null
            }
    }

    private fun processResponse(
        future: CompletableFuture<Result<GeneratorResult>>,
        generatorResult: Any
    ) {
        val generatedFilesResult = extractGeneratedFiles(generatorResult)
        if (generatedFilesResult.isFailure) {
            future.complete(Result.failure(generatedFilesResult.exceptionOrNull()!!))
            return
        }

        val generatedFiles = generatedFilesResult.getOrNull()!!
        if (generatedFiles.isEmpty()) {
            future.complete(Result.success(GeneratorResult(listOf())))
        } else {
            future.complete(Result.success(GeneratorResult(generatedFiles)))
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
