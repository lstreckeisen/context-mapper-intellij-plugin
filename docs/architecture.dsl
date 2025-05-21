workspace "Context Mapper IntelliJ Plugin" {
    !identifiers hierarchical

    model {
        pluginUser = person "Plugin User"
        intelliJ = softwareSystem "IntelliJ IDEA" {
            tag "External"

            cmPlugin = container "Context Mapper Plugin" "Provides Context Mapper support in IntelliJ" "Kotlin, IntelliJ Plugin SDK" {
                tag "Maintained"
                
                langConfig = component "Context Mapper Language Config" "Configures CML as language is IntelliJ" {
                    tag "Maintained"
                }

                lsp4ijConfig = component "LSP4IJ Configuration" "Configures LSP4IJ for Context Mapper" {
                    tag "Maintained"
                }

                actions = component "CML Editor Actions" "Generate additional resources from CML models" {
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

        languageServer = softwareSystem "Context Mapper Language Server" "Provides language server capabilities for Context Mapper" "Node.js, Langium" {
            tag "Maintained"

            server = container "Context Mapper Language Server" {
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

                formatters = component "CML Formatter" {
                    tag "Maintained"
                }

                completionProvider = component "CML Completion Provider" {
                    tag "Maintained"
                }

                commandHandler = component "CML Command Handler" {
                    tag "Maintained"
                }
            }
        }

        pluginUser -> intelliJ.editor "uses"

        intelliJ.lsp4ij -> intelliJ.editor "integrates with"
        intelliJ.lsp4ij -> languageServer.server "starts"
        intelliJ.lsp4ij -> languageServer.server "communicates with"
        languageServer.server -> intelliJ.lsp4ij "provides editor services"

        intelliJ.editor -> intelliJ.cmPlugin.actions "triggers actions"
        intelliJ.cmPlugin.lsp4ijConfig -> intelliJ.lsp4ij "configures"
        intelliJ.cmPlugin.actions -> intelliJ.lsp4ij "triggers command execution"
        intelliJ.cmPlugin.langConfig -> intelliJ.editor "configures"

        languageServer.server.server -> languageServer.server.tokenProvider "requests tokens from"
        languageServer.server.server -> languageServer.server.semanticValidator "requests validation from"
        languageServer.server.server -> languageServer.server.foldingRangeProvider "requests folding ranges from"
        languageServer.server.server -> languageServer.server.scopeProvider "requests reference scope from"
        languageServer.server.server -> languageServer.server.hoverProvider "requests hover documentation from"
        languageServer.server.server -> languageServer.server.formatters "gets formatting changes from"
        languageServer.server.server -> languageServer.server.completionProvider "requests autocomplete suggestions from"
        languageServer.server.server -> languageServer.server.commandHandler "trigger command/generator execution"
    }

    views {
        systemContext intelliJ "ContextDiagram" {
            include *
            autolayout lr
        }

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
            autolayout lr
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
