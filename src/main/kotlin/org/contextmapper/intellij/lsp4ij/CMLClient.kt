package org.contextmapper.intellij.lsp4ij

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl

class CMLClient(project: Project) : LanguageClientImpl(project)
