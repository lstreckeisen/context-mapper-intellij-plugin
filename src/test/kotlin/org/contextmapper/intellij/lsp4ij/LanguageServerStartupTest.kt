package org.contextmapper.intellij.lsp4ij

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.util.ui.UIUtil
import com.redhat.devtools.lsp4ij.LanguageServerItem
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.ServerStatus

class LanguageServerStartupTest : BasePlatformTestCase() {
    fun testLanguageServerStartup() {
        LanguageServerManager.getInstance(project).start("cml-language-server")
        val languageServerItem = LanguageServerManager.getInstance(project).getLanguageServer("cml-language-server").join()!!
        waitForServer(languageServerItem)

        languageServerItem.server.shutdown().join()
    }

    private fun waitForServer(languageServerItem: LanguageServerItem) {
        var counter = 0
        while (counter < 10) {
            if (languageServerItem.serverWrapper.serverStatus != ServerStatus.started) {
                UIUtil.dispatchAllInvocationEvents()
                Thread.sleep(100)
                counter++
            } else {
                return
            }
        }

        languageServerItem.server.shutdown().join()
        fail("Server did not start.")
    }
}
