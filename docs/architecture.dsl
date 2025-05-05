workspace "ContextMapper IntelliJ Plugin" {
    !identifiers hierarchical

    model {
        pluginUser = person "Plugin User"
        intelliJ = softwareSystem "IntelliJ IDEA" {
            tag "External"

            cmPlugin = container "ContextMapper Plugin" "Provides ContextMapper support in IntelliJ" "Kotlin, IntelliJ Plugin SDK" {
                tag "Maintained"

                lsp4ijConfig = component "LSP4IJ Configuration" "Configures LSP4IJ for ContextMapper" {
                    tag "Maintained"
                }

                cml = component "CML Editor Features" "Provides CML editor features" {
                    tag "Maintained"
                }

                generators = component "CML Generators" "Generate additional resources from CML models" {
                    tag "Maintained"
                }
            }
            lsp4ij = container "LSP4IJ Plugin" "Manages language server configurations and LSP integration for IntelliJ" {
                tag "External
            }
            editor = container "IntelliJ Editor" "IntelliJ's text editor" {
                tag "External"
            }
        }

        languageServer = softwareSystem "ContextMapper Language Server" "Provides language server capabilities for ContextMapper" "Node.js, Langium" {
            tag "Maintained"

            server = container "ContextMapper Language Server" {
                tag "External"

                server = component "Langium Language Server" {
                    tag "External"
                }

                tokenProvider = component "CML Semantic Token Provider" {
                    tag "Maintained"
                }

                semanticValidator = component "CML Semantic Validator" {
                    tag "Maintained"
                }

                foldingRangeProvider = component "CML Folding Range Provider" {
                    tag "Maintained"
                }

                scopeProvider = component "CML Reference Scope Provider" {
                    tag "Maintained"
                }

                hoverProvider = component "CML Hover Provider" {
                    tag "Maintained"
                }
            }
        }

        pluginUser -> intelliJ.editor "uses"
        intelliJ.cmPlugin -> intelliJ.lsp4ij "configures"
        intelliJ.cmPlugin -> intelliJ.editor "extends"
        intelliJ.lsp4ij -> intelliJ.editor "integrates with"
        intelliJ.lsp4ij -> languageServer.server "starts"
        intelliJ.lsp4ij -> languageServer.server "communicates with"
        languageServer.server -> intelliJ.lsp4ij "provides editor services"

        intelliJ.editor -> intelliJ.cmPlugin.generators "triggers"
        intelliJ.cmPlugin.lsp4ijConfig -> intelliJ.lsp4ij "configures"
        intelliJ.cmPlugin.cml -> intelliJ.editor "extends"

        languageServer.server.server -> languageServer.server.tokenProvider "requests tokens from"
        languageServer.server.server -> languageServer.server.semanticValidator "requests validation from"
        languageServer.server.server -> languageServer.server.foldingRangeProvider "requests folding ranges from"
        languageServer.server.server -> languageServer.server.scopeProvider "requests reference scope from"
        languageServer.server.server -> languageServer.server.hoverProvider "requests hover documentation from"
    }

    views {
        container intelliJ "ContainerDiagram" {
            include *
            autolayout lr
        }

        component intelliJ.cmPlugin "plugin-ComponentDiagram" {
            include *
            autolayout bt
        }

        component languageServer.server "languageServer-ComponentDiagram" {
            include *
            autolayout bt
        }

        styles {
            element "Element" {
                color #ffffff
            }
            element "Person" {
                background #85bbf0
                shape person
            }
            element "Maintained" {
                background #027368
            }
            element "External" {
                background #0A4771
            }
        }
    }
}
