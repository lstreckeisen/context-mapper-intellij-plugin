package org.contextmapper.intellij.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.vfs.LocalFileSystem
import org.contextmapper.intellij.generators.ContextMapperGenerator
import org.contextmapper.intellij.notifications.showErrorNotification
import org.contextmapper.intellij.notifications.showInfoNotification
import org.eclipse.lsp4j.Command
import kotlin.io.path.Path

private val logger = Logger.getInstance(PlantUMLAction::class.java)

class PlantUMLAction : AnAction() {
    private val generator = ContextMapperGenerator(lspCommandExecutor)

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project
        val editor = event.getData(CommonDataKeys.EDITOR)
        val file = event.getData(CommonDataKeys.VIRTUAL_FILE)

        if (editor == null || project == null || project.basePath == null || file == null) {
            return
        }

        val outDir = Path(project.basePath!!).resolve("src-gen").toString()

        val commandArgs = listOf(file.path, outDir)
        val command = Command("Generate PlantUML Diagrams", PLANT_UML_GENERATOR_COMMAND, commandArgs)

        generator.generate(project, command)
            .whenComplete { result, ex ->
                if (ex != null || result.isFailure) {
                    val errorMessage =
                        if (ex != null) {
                            "An unexpected error occurred while generating PlantUML diagrams"
                        } else {
                            result.exceptionOrNull()!!.message!!
                        }

                    logger.error(errorMessage, ex)
                    showErrorNotification(project, errorMessage)
                    return@whenComplete
                }

                val generatedFiles = result.getOrNull()!!.generatedFiles
                if (generatedFiles.isEmpty()) {
                    showInfoNotification(project, "No PlantUML diagrams were created")
                } else {
                    showInfoNotification(project, "Successfully created ${generatedFiles.size} PlantUML diagram(s)")
                }
                LocalFileSystem.getInstance().refresh(true)
            }
    }
}
