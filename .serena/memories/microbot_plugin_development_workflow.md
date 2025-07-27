# Microbot Plugin Development Workflow

## Complete Development Process

### Development Location (Testing)
```
microbot/runelite-client/src/main/java/net/runelite/client/plugins/microbot/yourpluginname/
```
- Package: `package net.runelite.client.plugins.microbot.yourpluginname;`
- Purpose: Real-time testing with RuneLite in IntelliJ
- Run Configuration: "RuneLite" (net.runelite.client.RuneLite)

### Export Location (JAR Building)
```
MyPlugins/src/main/java/com/myplugins/yourpluginname/
```
- Package: `package com.myplugins.yourpluginname;`
- Purpose: Creating distributable JAR files
- Build: IntelliJ Maven panel → MyPlugins → Lifecycle → install

## Critical Protocol Rules
1. **ALWAYS** use sequential thinking for development tasks
2. **STOP** and prompt user for testing before export
3. **STOP** and prompt user for Maven builds
4. **REMEMBER** to change package names when copying
5. **VERIFY** file locations before proceeding

## File Structure Template
```
YourPluginNamePlugin.java     - Main plugin class (@PluginDescriptor)
YourPluginNameScript.java     - Automation logic (extends Script)
YourPluginNameConfig.java     - Configuration interface (extends Config)
YourPluginNameOverlay.java    - UI overlay (extends OverlayPanel)
```