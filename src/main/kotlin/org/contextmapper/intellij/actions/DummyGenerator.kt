package org.contextmapper.intellij.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.redhat.devtools.lsp4ij.LanguageServerManager
import com.redhat.devtools.lsp4ij.commands.CommandExecutor
import org.contextmapper.intellij.actions.generators.ContextMapperGenerator
import org.contextmapper.intellij.utils.showInfoNotification
import org.eclipse.lsp4j.Command

class DummyGenerator : AnAction() {
    private val commandExecutor: LspCommandExecutor = { context -> CommandExecutor.executeCommand(context) }

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        val editor = event.getData(CommonDataKeys.EDITOR)
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE)

        if (editor == null || project == null || project.basePath == null || file == null) {
            return
        }

        val generator = ContextMapperGenerator(commandExecutor, LanguageServerManager.getInstance(project))
        val command = Command("Dummy generator", "org.contextmapper.Dummy", listOf(file.path))

        val result =
            generator.generate(project, command)
                .join()

        showInfoNotification(project, result.getOrNull()?.generatedFiles[0] ?: "Generator failed")
    }
}
