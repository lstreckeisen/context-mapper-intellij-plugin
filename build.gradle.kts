import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.3.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
    id("org.jetbrains.changelog") version "2.2.1"
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

configurations {
    create("cmlLsp")
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.1.7")

        plugins("com.redhat.devtools.lsp4ij:${property("lsp4ijVersion")}")
    }

    "cmlLsp"("org.contextmapper:context-mapper-lsp:${property("cmlVersion")}@tar")

    implementation(files(layout.buildDirectory.dir("lsp")))

    testImplementation("org.junit.jupiter:junit-jupiter-api:${property("jUnitVersion")}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${property("jUnitVersion")}")
    testImplementation("io.mockk:mockk:${property("mockkVersion")}")
}

intellijPlatform {
    projectName = project.name

    pluginConfiguration {
        id = "org.contextmapper.intellij-plugin"
        name = "ContextMapper"
        version = "0.1.0"

        productDescriptor {
        }

        ideaVersion {
            sinceBuild = "241"
            untilBuild = "243.*"
        }

        vendor {
            name = "ContextMapper"
            url = "https://contextmapper.org"
        }

        // From https://github.com/redhat-developer/intellij-quarkus/blob/main/build.gradle.kts
        // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's manifest
        description =
            providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
                val start = "<!-- Plugin description -->"
                val end = "<!-- Plugin description end -->"

                with(it.lines()) {
                    if (!containsAll(listOf(start, end))) {
                        throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                    }
                    subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
                }
            }
    }

    signing {
        certificateChain = System.getenv("CERTIFICATE_CHAIN")
        privateKey = System.getenv("PRIVATE_KEY")
        password = System.getenv("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = System.getenv("PUBLISH_TOKEN")
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

tasks {
    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    /**
     * inspired by:
     * - https://github.com/ContextMapper/vscode-extension/blob/master/build.gradle#L100
     * - https://github.com/redhat-developer/intellij-quarkus/blob/main/build.gradle.kts
     */
    val copyLanguageServer by registering(Copy::class) {
        description = "Extracts and copies the ContextMapper Language Server to the build directory"
        group = "build"

        from(tarTree(configurations.getByName("cmlLsp").singleFile)) {
            eachFile {
                relativePath =
                    RelativePath(
                        true,
                        *relativePath.parent.segments
                            .drop(1)
                            .toTypedArray(),
                        name,
                    )
            }
        }
        into(layout.buildDirectory.dir("lsp/lsp"))
    }

    prepareSandbox {
        dependsOn(copyLanguageServer)
    }

    test {
        useJUnitPlatform()

        reports {
            junitXml.required = true
            html.required = false
        }
    }
}
