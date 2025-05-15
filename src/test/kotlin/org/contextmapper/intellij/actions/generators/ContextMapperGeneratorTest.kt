package org.contextmapper.intellij.actions.generators

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.commands.CommandExecutor
import com.redhat.devtools.lsp4ij.settings.UserDefinedLanguageServerSettings
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.contextmapper.intellij.actions.LspCommandExecutor
import org.eclipse.lsp4j.Command
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ContextMapperGeneratorTest() {
    private val command = Command("TestCommand", "cmd.id")

    private lateinit var successMessage: CapturingSlot<String>
    private lateinit var lspCommandExecutor: LspCommandExecutor
    private lateinit var project: Project
    private lateinit var generator: ContextMapperGenerator

    @BeforeEach
    fun setup() {
        successMessage = slot()
        lspCommandExecutor = mockk(relaxed = true)
        project =
            mockk {
                every { basePath } returns "/tmp"
                every { getService(any<Class<UserDefinedLanguageServerSettings>>()) } returns
                    mockk<UserDefinedLanguageServerSettings>(
                        relaxed = true,
                    )
            }
        generator = ContextMapperGenerator(lspCommandExecutor)
    }

    @Test
    fun testSuccessfulGeneration() {
        every { lspCommandExecutor.invoke(any()) } returns
            mockk<CommandExecutor.LSPCommandResponse> {
                every { response } returns
                    mockk {
                        every { join() } returns listOf("diagram.puml")
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
                        every { join() } returns listOf<String>()
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
                        every { join() } throws Exception("Test Exception")
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
}
