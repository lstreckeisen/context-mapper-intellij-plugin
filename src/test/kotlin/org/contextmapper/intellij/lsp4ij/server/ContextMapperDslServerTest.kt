package org.contextmapper.intellij.lsp4ij.server

import kotlin.test.Test
import kotlin.test.assertEquals

class ContextMapperDslServerTest {
    @Test
    fun `uses explicit node executable override when configured`() {
        val executable =
            resolveNodeExecutable(
                environment = mapOf(CONTEXT_MAPPER_NODE_ENV to "/custom/node"),
                isExecutable = { false },
            )

        assertEquals("/custom/node", executable)
    }

    @Test
    fun `uses common macos node location when available`() {
        val executable =
            resolveNodeExecutable(
                environment = emptyMap(),
                isExecutable = { it == "/opt/homebrew/bin/node" },
            )

        assertEquals("/opt/homebrew/bin/node", executable)
    }

    @Test
    fun `falls back to path lookup when common node locations are unavailable`() {
        val executable =
            resolveNodeExecutable(
                environment = emptyMap(),
                isExecutable = { false },
            )

        assertEquals("node", executable)
    }
}
