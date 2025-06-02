<!-- Plugin description -->
![Context Mapper](https://raw.githubusercontent.com/wiki/ContextMapper/context-mapper-dsl/logo/cm-logo-github-small.png)
# Context Mapper IntelliJ Plugin

> **_NOTE:_** This plugin is a proof of concept. It does not support all Context Mapper features yet.

[ContextMapper](https://contextmapper.org/) is an open source tool providing a Domain-specific Language based on Domain-Driven Design (DDD) patterns for context mapping and service decomposition.

## System Requirements
To use the ContextMapper IntelliJ plugin you need the following tools (besides IntelliJ and our extension) installed locally:

* [Oracle Java](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) or [OpenJDK](https://openjdk.java.net/) (JRE 11 or newer)
* [Node.js](https://nodejs.org/en/download) (v22)
* Maybe you want to install a [PlantUML extension](https://plugins.jetbrains.com/plugin/7017-plantuml-integration) for the generated PlantUML diagrams.
* LSP4IJ IntelliJ plugin (will be installed automatically when installing our plugin)

<!-- Plugin description end -->

## Build and/or Run Extension Locally
This project uses [Gradle](https://gradle.org/) to build the IntelliJ plugin.

### Requirements
To be able to work on the plugin in IntelliJ, you need to have the [Plugin DevKit](https://plugins.jetbrains.com/plugin/22851-plugin-devkit) plugin installed.

The language server package is downloaded from the GitHub NPM registry, which requires you to provide an authentication token.
You can get a token by creating a [personal access token](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens) in your GitHub account.
Make sure that the token includes the **package:read** permission.

To configure the registry and authentication, add this configuration to the `.npmrc` file in your home directory.
```
@lstreckeisen:registry=https://npm.pkg.github.com
//npm.pkg.github.com/:_authToken=<TOKEN>
```

### Commands
After cloning this repository, you can build the project with the following command:

```bash
./gradlew clean buildPlugin
```

Use the following command to build and run the plugin:

```bash
./gradlew runIde
```
