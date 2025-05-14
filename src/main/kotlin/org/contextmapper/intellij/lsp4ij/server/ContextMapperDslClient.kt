package org.contextmapper.intellij.lsp4ij.server

import com.intellij.openapi.project.Project
import com.redhat.devtools.lsp4ij.client.LanguageClientImpl

class ContextMapperDslClient(project: Project) : LanguageClientImpl(project)
