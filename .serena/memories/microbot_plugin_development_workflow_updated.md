# Microbot Plugin Development Workflow - Complete Guide

## Development Approach: Direct MyPlugins Development

### Single-Location Development (CURRENT METHOD)
- **Development Location**: `MyPlugins/src/main/java/com/myplugins/pluginname/`
- **Package**: `package com.myplugins.pluginname;`
- **Testing**: Build JAR and test in actual Microbot client
- **No Dual Locations**: Develop directly in export location

### Project Structure
```
MyPlugins/
├── pom.xml                              # Maven configuration
├── src/main/java/com/myplugins/
│   └── SPHSmartAccountBuilder/          # Plugin folder
│       ├── SPHAccountBuilderPlugin.java # Main plugin class
│       ├── SPHAccountBuilderScript.java # Automation logic
│       ├── SPHAccountBuilderConfig.java # Configuration
│       └── SPHAccountBuilderOverlay.java# UI overlay
└── target/
    └── sph-account-builder-1.0.1.jar   # Built JAR
```

## Plugin Architecture Pattern

### 1. Plugin Class (Entry Point)
```java
@PluginDescriptor(
    name = "<html>[<font color=#FF8C00>SPH</font>] Plugin Name",
    description = "Plugin Description",
    tags = {"microbot", "tag1", "tag2"},
    enabledByDefault = false,
    loadInSafeMode = false  // Community plugin marker
)
public class PluginNamePlugin extends Plugin {
    // Plugin lifecycle methods
}
```

### 2. Script Class (Automation Logic)
```java
public class PluginNameScript extends Script {
    public boolean run(PluginNameConfig config) {
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            // Main automation logic
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }
}
```

### 3. Configuration Interface
```java
@ConfigGroup("pluginname")
public interface PluginNameConfig extends Config {
    // Configuration options
}
```

### 4. Overlay Class (UI Display)
```java
public class PluginNameOverlay extends OverlayPanel {
    // UI rendering logic
}
```

## Build & Deployment Process

### Maven Configuration
```xml
<groupId>com.myplugins</groupId>
<artifactId>plugin-jar-name</artifactId>
<version>1.0.1</version>
```

### Build Commands
```bash
# Setup environment
source setup-env.sh

# Build plugin
cd MyPlugins
mvn clean install

# Output locations
# - Built JAR: MyPlugins/target/plugin-name.jar
# - Auto-installed: ~/.runelite/microbot-plugins/plugin-name.jar
```

### Client Integration
- **Plugin Location**: Configuration Tab → Microbot section
- **Loading**: Automatic on Microbot startup
- **Status**: Appears alongside built-in Microbot plugins