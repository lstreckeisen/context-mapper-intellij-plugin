workspace "ContextMapper IntelliJ Plugin" {
    !identifiers hierarchical

    model {
        pluginUser = person "Plugin User"
        intelliJ = softwareSystem "IntelliJ IDEA" {
            tag "External"

            cmPlugin = container "ContextMapper Plugin" "Provides ContextMapper support in IntelliJ" {
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
            lsp = container "ContextMapper Language Server" "Provides language server capabilities for ContextMapper" {
                tag "Maintained"
            }
        }

        pluginUser -> intelliJ.editor "uses"
        intelliJ.cmPlugin -> intelliJ.lsp4ij "configures"
        intelliJ.cmPlugin -> intelliJ.editor "extends"
        intelliJ.lsp4ij -> intelliJ.editor "integrates with"
        intelliJ.lsp4ij -> intelliJ.lsp "starts"
        intelliJ.lsp4ij -> intelliJ.lsp "communicates with"
        intelliJ.lsp -> intelliJ.lsp4ij "provides editor services"

        intelliJ.editor -> intelliJ.cmPlugin.generators "triggers"
        intelliJ.cmPlugin.lsp4ijConfig -> intelliJ.lsp4ij "configures"
        intelliJ.cmPlugin.cml -> intelliJ.editor "extends"
    }

    views {
        container intelliJ "ContainerDiagram" {
            include *
            autolayout lr
        }

        component intelliJ.cmPlugin "ComponentDiagram" {
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
