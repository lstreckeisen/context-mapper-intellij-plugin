import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.3.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.2.0"
}

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

val cmlVersion: String by project
val lsp4ijVersion = "0.11.0"
val jUnitVersion = "5.8.2"

configurations {
    create("cmlLsp")
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.1.7")

        plugins("com.redhat.devtools.lsp4ij:$lsp4ijVersion")
    }

    "cmlLsp"("org.contextmapper:context-mapper-lsp:$cmlVersion@tar")

    implementation(files(layout.buildDirectory.dir("lsp")))

    testImplementation("org.junit.jupiter:junit-jupiter-api:$jUnitVersion")
}

intellijPlatform {
    projectName = project.name

    pluginConfiguration {
        id = "org.contextmapper.intellij"
        name = "ContextMapper"
        version = "0.1.0"
        description = "Context Mapper plugin for IntelliJ"

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

    val copyLanguageServer by registering(Copy::class) {
        description = "Extracts and copies the ContextMapper Language Server to the build directory"
        group = "build"

        from(tarTree(configurations.getByName("cmlLsp").singleFile)) {
            eachFile {
                relativePath =
                    RelativePath(
                        true,
                        *relativePath.parent.segments.drop(1).toTypedArray(),
                        name,
                    )
            }
        }
        into(layout.buildDirectory.dir("lsp/lsp"))
    }

    prepareSandbox {
        dependsOn(copyLanguageServer)
    }

    /*withType<Test> {
        reports {
            junitXml.required.set(true)
        }
    }*/
}
