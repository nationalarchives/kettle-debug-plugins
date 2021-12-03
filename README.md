# Debug Plugins for Pentaho KETTLE

[![CI](https://github.com/nationalarchives/kettle-debug-plugins/workflows/CI/badge.svg)](https://github.com/nationalarchives/kettle-debug-plugins/actions?query=workflow%3ACI)
[![Java 8](https://img.shields.io/badge/java-8+-blue.svg)](https://adoptopenjdk.net/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)

This project contains plugins for [Pentaho Data Integration](https://github.com/pentaho/pentaho-kettle) (or KETTLE as it is commonly known),
that add functionality to assist in debugging workflows.

The plugins provided are:

1. Log Row Step
   
    <img alt="Log Row Step Icon" src="https://raw.githubusercontent.com/nationalarchives/kettle-debug-plugins/main/src/main/resources/LogRowStep.svg" width="32"/>
    This utility plugin can be used to debug information about each row sent to it.

This project was developed by [Evolved Binary](https://evolvedbinary.com) as part of Project OMEGA for the [National Archives](https://nationalarchives.gov.uk).

## Getting the Plugins

You can either download the plugins from our GitHub releases page: https://github.com/nationalarchives/kettle-debug-plugins/releases/, or you can build them from source.

## Building from Source Code
The plugins can be built from Source code by installing the pre-requisites and following the steps described below.

### Pre-requisites for building the project:
* [Apache Maven](https://maven.apache.org/), version 3+
* [Java JDK](https://adoptopenjdk.net/) 1.8
* [Git](https://git-scm.com)

### Build steps:
1. Clone the Git repository
    ```
    $ git clone https://github.com/nationalarchives/kettle-debug-plugins.git
    ```

2. Compile a package
    ```
    $ cd kettle-debug-plugins
    $ mvn clean package
    ```
    
3. The plugins directory is then available at `target/kettle-debug-plugins-1.0.0-SNAPSHOT-kettle-plugin/kettle-debug-plugins`


## Installing the plugins
* Tested with Pentaho Data Integration - Community Edition - version: 9.1.0.0-324

You need to copy the plugins directory `kettle-debug-plugins` (from building above) into the `plugins` sub-directory of your KETTLE installation.

This can be done by either running:
```
  $ mvn -Pdeploy-pdi-local -Dpentaho-kettle.plugins.dir=/opt/data-integration/plugins antrun:run@deploy-to-pdi
```

or, you can do so manually, e.g.:
```
  $ cp -r target/kettle-debug-plugins-1.0.0-SNAPSHOT-kettle-plugin/kettle-debug-plugins /opt/data-integration/plugins/
```

## Using the plugins
*TODO(AR)*
