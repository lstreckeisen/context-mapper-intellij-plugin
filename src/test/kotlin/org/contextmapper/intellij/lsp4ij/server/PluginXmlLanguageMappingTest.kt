package org.contextmapper.intellij.lsp4ij.server

import org.contextmapper.intellij.utils.CONTEXT_MAPPER_LANGUAGE_ID
import org.contextmapper.intellij.utils.CONTEXT_MAPPER_SERVER_ID
import org.w3c.dom.Element
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PluginXmlLanguageMappingTest {
    @Test
    fun `maps Context Mapper files to bundled language server language id`() {
        val document =
            DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(File("src/main/resources/META-INF/plugin.xml"))

        val languageMapping =
            (0 until document.getElementsByTagName("languageMapping").length)
                .map { document.getElementsByTagName("languageMapping").item(it) as Element }
                .singleOrNull { it.getAttribute("serverId") == CONTEXT_MAPPER_SERVER_ID }

        assertNotNull(languageMapping)
        assertEquals(CONTEXT_MAPPER_LANGUAGE_ID, languageMapping.getAttribute("languageId"))
    }
}
