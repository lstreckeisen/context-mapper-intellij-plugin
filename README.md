<!-- Plugin description -->
![Context Mapper](https://raw.githubusercontent.com/wiki/ContextMapper/context-mapper-dsl/logo/cm-logo-github-small.png)
# Context Mapper IntelliJ Plugin

> **_NOTE:_** This plugin is a proof of concept. It does not support all Context Mapper features yet.

[![IntelliJ Plugin Build](https://github.com/ContextMapper/context-mapper-intellij-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/ContextMapper/context-mapper-intellij-plugin/actions/workflows/build.yml) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=ContextMapper_context-mapper-intellij-plugin&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=ContextMapper_context-mapper-intellij-plugin) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

[Context Mapper](https://contextmapper.org/) is an open source tool providing a Domain-specific Language based on Domain-Driven Design (DDD) patterns for context mapping and service decomposition.

## System Requirements
To use the Context Mapper IntelliJ plugin you need the following tools (besides IntelliJ and our extension) installed locally:

* [Oracle Java](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or [OpenJDK](https://openjdk.java.net/) (JRE 11 or newer)
* [Node.js](https://nodejs.org/en/download) (v22)
* Maybe you want to install a [PlantUML extension](https://plugins.jetbrains.com/plugin/7017-plantuml-integration) for the generated PlantUML diagrams.
* [Graphviz](https://graphviz.org/) if you want to render generated PlantUML diagrams in IntelliJ.
* LSP4IJ IntelliJ plugin (will be installed automatically when installing our plugin)

<!-- Plugin description end -->

## Getting Started
This plugin provides Context Mapper DSL support in IntelliJ. It recognizes `.cml` files, starts the bundled Context Mapper language server through LSP4IJ, provides semantic highlighting, and can ask the language server to generate PlantUML `.puml` diagrams.

### 1. Install the IntelliJ plugins
Install the Context Mapper plugin ZIP in IntelliJ:

1. Open **Settings > Plugins**.
2. Click the gear icon and choose **Install Plugin from Disk...**.
3. Select the built plugin ZIP from `build/distributions/context-mapper-intellij-plugin.zip`.
4. Restart IntelliJ when prompted.

The Context Mapper plugin depends on LSP4IJ. IntelliJ should install LSP4IJ automatically with this plugin. If syntax highlighting or generation does not work, check that both plugins are enabled under **Settings > Plugins**.

### 2. Install local tools
The Context Mapper language server is a Node.js process. Make sure `node` is installed and visible to IntelliJ:

```bash
node --version
which node
```

On macOS with Homebrew, Node is commonly installed under `/opt/homebrew/bin/node`. If IntelliJ was launched from Finder and cannot find Node, restart IntelliJ after installing Node or set the `CONTEXT_MAPPER_NODE` environment variable to the full Node executable path before launching IntelliJ.

To preview generated PlantUML diagrams inside IntelliJ, install the PlantUML Integration plugin and Graphviz:

```bash
brew install graphviz
which dot
dot -V
```

Then configure the PlantUML plugin's Graphviz/Dot executable path to the result of `which dot`, for example `/opt/homebrew/bin/dot`. A common broken default is `/opt/local/bin/dot`, which is a MacPorts path and may not exist on Homebrew-based systems.

### 3. Create a simple CML file
Create a file such as `architecture/context-maps/example.cml`:

```cml
ContextMap ExampleMap {
  contains OrderingContext
  contains BillingContext

  OrderingContext [SK]<->[SK] BillingContext
}

BoundedContext OrderingContext {
  type = APPLICATION
  domainVisionStatement = "Handles customer order placement."
}

BoundedContext BillingContext {
  type = APPLICATION
  domainVisionStatement = "Handles invoicing and payment workflows."
}
```

Syntax highlighting should appear after the file opens. If the file icon appears but keywords are not colored, the file type is registered but the language server may not be running.

### 4. Generate a PlantUML diagram
Open the `.cml` file in the editor, then run:

1. Right-click inside the editor.
2. Choose **Context Mapper > Generate PlantUML Diagrams**.

You can also search for the action with **Cmd+Shift+A** and run **Generate PlantUML Diagrams**.

The generated file is written to:

```text
<project-root>/src-gen/<source-file-name>.puml
```

For example:

```text
src-gen/example.puml
```

Open that `.puml` file to preview it with the PlantUML plugin.

### Troubleshooting
If `.cml` files have the Context Mapper icon but no syntax highlighting, verify that LSP4IJ is enabled and that IntelliJ can start Node. Search the IntelliJ log for `cml-language-server`, `contextmapper`, `LSP4IJ`, or `node`.

If generation reports that no PlantUML diagrams were created, make sure the `.cml` file contains a `ContextMap`. Standalone `BoundedContext` declarations are valid CML, but the current generator creates a component diagram from the first `ContextMap` in the file.

If the PlantUML preview shows `Cannot find Graphviz`, the generated `.puml` file is usually fine. Configure the PlantUML plugin's Dot executable to the path returned by `which dot`.

## Build and/or Run Extension Locally
This project uses [Gradle](https://gradle.org/) to build the IntelliJ plugin.

### Requirements
To be able to work on the plugin in IntelliJ, you need to have the [Plugin DevKit](https://plugins.jetbrains.com/plugin/22851-plugin-devkit) plugin installed.

The language server package is downloaded from the GitHub NPM registry, which requires you to provide an authentication token.
You can get a token by creating a [personal access token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens) in your GitHub account.
Make sure that the token includes the **package:read** permission.

To configure the registry and authentication, add this configuration to the `.npmrc` file in your home directory.
```
@contextmapper:registry=https://npm.pkg.github.com
//npm.pkg.github.com/:_authToken=<TOKEN>
```

### Commands
After cloning this repository, you can build the project with the following command:

```bash
./gradlew clean buildPlugin
```

Before installing a built plugin ZIP into a newer IntelliJ IDEA version, verify plugin compatibility:

```bash
./gradlew verifyPlugin
```

The verifier target defaults to IntelliJ IDEA Ultimate 2026.1.3. Override it when needed:

```bash
./gradlew verifyPlugin -PintellijVerifierIdeVersion=<IDE_VERSION>
```

Use the following command to build and run the plugin:

```bash
./gradlew runIde
```

## Contributing
Contribution is always welcome! Here are some ways how you can contribute:
* Create Github issues if you find bugs or just want to give suggestions for improvements.
* This is an open source project: if you want to code, [create pull requests](https://help.github.com/articles/creating-a-pull-request/) from [forks of this repository](https://help.github.com/articles/fork-a-repo/). Please refer to a Github issue if you contribute this way.
* If you want to contribute to our documentation and user guides on our website [https://contextmapper.org/](https://contextmapper.org/), create pull requests from forks of the corresponding page repo [https://github.com/ContextMapper/contextmapper.github.io](https://github.com/ContextMapper/contextmapper.github.io) or create issues [there](https://github.com/ContextMapper/contextmapper.github.io/issues).

## Licence
ContextMapper is released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
