package org.contextmapper.intellij.lsp4ij

import com.intellij.openapi.extensions.PluginDescriptor
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.nio.file.Path

class CMLServerTest {
    @Test
    fun testExecutablePath() {
        val cmlServer = CMLServer(mockPluginDescriptor())

        assertEquals(
            "/tmp/test/lib/lsp/index.cjs",
            cmlServer.commandLine.parametersList.get(0),
        )
    }

    private fun mockPluginDescriptor(): PluginDescriptor =
        mockk<PluginDescriptor> {
            every { pluginPath } returns Path.of("/tmp/test")
        }
}
