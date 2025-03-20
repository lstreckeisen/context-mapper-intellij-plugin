package org.contextmapper.intellij.lsp4ij

import com.intellij.openapi.extensions.PluginDescriptor
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.nio.file.Path

class CMLServerTest {
    @Test
    fun testWindowsExecutable() {
        val cmlServer = CMLServer(mockPluginDescriptor(), true)

        assertTrue(cmlServer.commandLine.exePath.endsWith(".bat"))
    }

    @Test
    fun testNonWindowsExecutable() {
        val cmlServer = CMLServer(mockPluginDescriptor(), false)

        assertFalse(cmlServer.commandLine.exePath.endsWith(".bat"))
    }

    private fun mockPluginDescriptor(): PluginDescriptor =
        mockk<PluginDescriptor> {
            every { pluginPath } returns Path.of("/tmp/test")
        }
}
