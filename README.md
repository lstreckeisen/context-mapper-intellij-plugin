<!-- Plugin description -->
![Context Mapper](https://raw.githubusercontent.com/wiki/ContextMapper/context-mapper-dsl/logo/cm-logo-github-small.png)
# Context Mapper IntelliJ Plugin
[ContextMapper](https://contextmapper.org/) is an open source tool providing a Domain-specific Language based on Domain-Driven Design (DDD) patterns for context mapping and service decomposition.

## System Requirements
To use the ContextMapper VS Code extension you need the following tools (besides [VS Code](https://code.visualstudio.com/) and our extension) installed locally:

* [Oracle Java](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or [OpenJDK](https://openjdk.java.net/) (JRE 11 or newer)
* If you want to use our [Context Map generator](https://contextmapper.org/docs/context-map-generator/) you need to have [Graphviz](https://www.graphviz.org/) installed on your system.
    * Ensure that the binaries are part of the _PATH_ environment variable and can be called from the terminal.
    * Especially on Windows this is not the case after the installation of [Graphviz](https://www.graphviz.org/). The default installation path is
      `C:\Program Files (x86)\GraphvizX.XX`, which means you have to add `C:\Program Files (x86)\GraphvizX.XX\bin` to your _PATH_ variable.
* Maybe you want to install the [PlantUML extension](https://marketplace.visualstudio.com/items?itemName=jebbs.plantuml) for the generated PlantUML diagrams.

<!-- Plugin description end -->

## Build and/or Run Extension Locally
This project uses [Gradle](https://gradle.org/) to build the IntelliJ plugin.

To be able to work on the plugin in IntelliJ, you need to have the [Plugin DevKit](https://plugins.jetbrains.com/plugin/22851-plugin-devkit) plugin installed.
After cloning this repository, you can build the project with the following command:

```bash
./gradlew clean buildPlugin
```

Use the following command to build and run the plugin:

```bash
./gradlew runIde
```
