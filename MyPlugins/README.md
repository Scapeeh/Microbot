# MyPlugins - Standalone Microbot Plugin Development

This is a standalone Maven project for developing Microbot plugins independently from the main Microbot codebase.

## Features
- ✅ Standalone compilation without rebuilding Microbot
- ✅ Automatic installation to sideloading directory
- ✅ Clean project structure for custom plugins
- ✅ Hot-reload support via Microbot's sideloading system

## Project Structure
```
MyPlugins/
├── pom.xml                    # Maven configuration
├── README.md                  # This file
└── src/
    └── main/
        └── java/
            └── com/
                └── myplugins/
                    └── [your-plugin]/
                        ├── YourPluginConfig.java
                        ├── YourPluginOverlay.java
                        ├── YourPlugin.java
                        └── YourPluginScript.java
```

## Build Commands

### Build and Install Plugin
```bash
mvn clean install
```
This will:
1. Compile your plugin
2. Package it as a JAR
3. Automatically copy it to `~/.runelite/microbot-plugins/`

### Build Only (No Install)
```bash
mvn clean install -Pno-install
```
Use this to build without automatically installing to the sideloading directory.

### Development Build
```bash
mvn compile
```
For quick compilation during development.

## Installation
After building, the plugin JAR will be automatically placed in:
- **Windows**: `%USERPROFILE%/.runelite/microbot-plugins/`
- **macOS/Linux**: `~/.runelite/microbot-plugins/`

Start Microbot and your plugin will be loaded automatically!

## Creating New Plugins
1. Create a new package under `src/main/java/com/myplugins/`
2. Follow the standard Microbot plugin structure (Config, Overlay, Plugin, Script)
3. Build and install using `mvn clean install`

## Dependencies
This project uses Microbot/RuneLite as provided dependencies, meaning:
- They're available during compilation
- They're not included in the final JAR (reduces size)
- The running Microbot client provides these at runtime