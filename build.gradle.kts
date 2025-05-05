import org.apache.commons.lang3.SystemUtils
import org.jetbrains.changelog.markdownToHTML
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.nio.file.Files

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.5.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
    id("org.jetbrains.changelog") version "2.2.1"
}

idea {
    module {
        testResources.from("src/test/testData")
    }
}

repositories {
    mavenLocal()
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

val lsp4ijVersion = property("lsp4ijVersion") as String
val jUnitVersion = property("jUnitVersion") as String
val mockkVersion = property("mockkVersion") as String

dependencies {
    intellijPlatform {
        intellijIdeaCommunity(property("intelliJVersion") as String)

        plugins("com.redhat.devtools.lsp4ij:$lsp4ijVersion")

        testFramework(TestFrameworkType.Platform)
    }

    implementation(files(layout.buildDirectory.dir("lsp")))

    testImplementation("junit:junit:$jUnitVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
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
            sinceBuild = property("ideaSinceBuild") as String
            untilBuild = property("ideaUntilBuild") as String
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

    val npmInstall by registering(Exec::class) {
        description = "Downloads the ContextMapper Language Server NPM package"
        group = LifecycleBasePlugin.BUILD_GROUP
        workingDir(layout.projectDirectory.dir("lsp"))
        val installCommand = "npm install"
        commandLine =
            if (SystemUtils.IS_OS_WINDOWS) {
                listOf("cmd", "/c", installCommand)
            } else {
                listOf("sh", "-c", installCommand)
            }
    }

    val copyLanguageServer by registering(Copy::class) {
        description = "Extracts and copies the ContextMapper Language Server to the build directory"
        group = LifecycleBasePlugin.BUILD_GROUP
        dependsOn(npmInstall)

        val packagePath = file("lsp/node_modules/@lstreckeisen/context-mapper-language-server")
        val sourcePath =
            if (Files.isSymbolicLink(packagePath.toPath())) { // for local development
                packagePath.toPath().toRealPath().resolve("cml-ls")
            } else {
                packagePath.toPath().resolve("cml-ls")
            }

        from(sourcePath)
        into(layout.buildDirectory.dir("lsp/lsp"))
    }

    compileJava {
        dependsOn(copyLanguageServer)
    }

    test {
        useJUnit()

        reports {
            junitXml.required = true
            html.required = false
        }
    }

    clean {
        delete(layout.projectDirectory.dir("lsp/node_modules"))
        delete(layout.projectDirectory.file("lsp/package-lock.json"))
    }
}
