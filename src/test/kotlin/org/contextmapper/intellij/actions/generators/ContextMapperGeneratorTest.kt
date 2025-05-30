package org.contextmapper.intellij.actions.generators

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.LanguageServerItem
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.commands.CommandExecutor
import com.redhat.devtools.lsp4ij.settings.UserDefinedLanguageServerSettings
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.contextmapper.intellij.actions.LspCommandExecutor
import org.contextmapper.intellij.utils.CONTEXT_MAPPER_SERVER_ID
import org.eclipse.lsp4j.Command
import org.eclipse.lsp4j.jsonrpc.ResponseErrorException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class ContextMapperGeneratorTest() {
    private val command = Command("TestCommand", "cmd.id")

    private lateinit var successMessage: CapturingSlot<String>
    private lateinit var lspCommandExecutor: LspCommandExecutor
    private lateinit var languageServerManager: LanguageServerManager
    private lateinit var project: Project
    private lateinit var generator: ContextMapperGenerator

    @BeforeEach
    fun setup() {
        successMessage = slot()
        lspCommandExecutor = mockk(relaxed = true)
        languageServerManager =
            mockk(relaxed = true) {
                every { getLanguageServer(any()) } returns
                    mockk {
                        val actionSlot = slot<Consumer<LanguageServerItem?>>()
                        every { thenAcceptAsync(capture(actionSlot)) } answers {
                            actionSlot.captured.accept(mockk(relaxed = true))
                            CompletableFuture.completedFuture(null)
                        }
                    }
            }
        project =
            mockk {
                every { basePath } returns "/tmp"
                every { getService(any<Class<UserDefinedLanguageServerSettings>>()) } returns
                    mockk<UserDefinedLanguageServerSettings>(
                        relaxed = true,
                    )
            }
        generator = ContextMapperGenerator(lspCommandExecutor, languageServerManager)
    }

    @Test
    fun testSuccessfulGeneration() {
        every { lspCommandExecutor.invoke(any()) } returns
            mockk<CommandExecutor.LSPCommandResponse> {
                every { response } returns
                    mockk {
                        val actionSlot = slot<Consumer<Any>>()
                        every { thenAcceptAsync(capture(actionSlot)) } answers {
                            actionSlot.captured.accept(listOf("diagram.puml"))
                            CompletableFuture.completedFuture(null)
                        }
                    }
            }

        val result =
            generator.generate(project, command)
                .join()

        assertNotNull(result)
        assertTrue(result.isSuccess)
        assertEquals(listOf("diagram.puml"), result.getOrNull()!!.generatedFiles)
    }

    @Test
    fun testSuccessfulGenerationWithNoDiagrams() {
        every { lspCommandExecutor.invoke(any()) } returns
            mockk<CommandExecutor.LSPCommandResponse> {
                every { response } returns
                    mockk {
                        val actionSlot = slot<Consumer<Any>>()
                        every { thenAcceptAsync(capture(actionSlot)) } answers {
                            actionSlot.captured.accept(listOf<String>())
                            CompletableFuture.completedFuture(null)
                        }
                    }
            }

        val result =
            generator.generate(project, command)
                .join()

        assertNotNull(result)
        assertTrue(result.isSuccess)
        assertEquals(listOf<String>(), result.getOrNull()!!.generatedFiles)
    }

    @Test
    fun testFailedGeneration() {
        every { lspCommandExecutor.invoke(any()) } returns
            mockk<CommandExecutor.LSPCommandResponse> {
                every { response } returns
                    mockk {
                        val actionSlot = slot<Consumer<Any>>()
                        every { thenAcceptAsync(capture(actionSlot)) } answers {
                            CompletableFuture.failedFuture(Exception("Test Exception"))
                        }
                    }
            }

        val result =
            generator.generate(project, command)
                .join()

        assertNotNull(result)
        assertTrue(result.isFailure)
        assertTrue { result.exceptionOrNull() is ContextMapperGeneratorException }
    }

    @Test
    fun testFailedGenerationWithoutResponse() {
        every { lspCommandExecutor.invoke(any()) } returns
            mockk<CommandExecutor.LSPCommandResponse> {
                every { response } returns null
            }

        val result =
            generator.generate(project, command)
                .join()

        assertNotNull(result)
        assertTrue(result.isFailure)
        assertTrue { result.exceptionOrNull() is ContextMapperGeneratorException }
    }

    @Test
    fun testFailedGenerationWithHandledException() {
        every { lspCommandExecutor.invoke(any()) } returns
            mockk<CommandExecutor.LSPCommandResponse> {
                every { response } returns
                    mockk {
                        val actionSlot = slot<Consumer<Any>>()
                        every { thenAcceptAsync(capture(actionSlot)) } answers {
                            actionSlot.captured.accept(mockk<ResponseErrorException>())
                            CompletableFuture.completedFuture(null)
                        }
                    }
            }

        val result =
            generator.generate(project, command)
                .join()

        assertNotNull(result)
        assertTrue { result.isFailure }
        assertTrue { result.exceptionOrNull() is HandledGeneratorException }
    }

    @Test
    fun testGeneratorWithMissingLanguageServer() {
        every { languageServerManager.getLanguageServer(eq(CONTEXT_MAPPER_SERVER_ID)) } returns
            mockk {
                val actionSlot = slot<Consumer<LanguageServerItem?>>()
                every { thenAcceptAsync(capture(actionSlot)) } answers {
                    CompletableFuture.failedFuture(RuntimeException())
                }
            }

        val result =
            generator.generate(project, command)
                .join()

        assertNotNull(result)
        assertTrue { result.isFailure }
        assertTrue { result.exceptionOrNull() is ContextMapperGeneratorException }
    }
}
