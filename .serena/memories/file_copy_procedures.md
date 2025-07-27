# File Copy Procedures for Plugin Export

## Copy Direction
FROM: microbot/runelite-client/src/main/java/net/runelite/client/plugins/microbot/yourpluginname/
TO: MyPlugins/src/main/java/com/myplugins/yourpluginname/

## Package Name Changes Required
When copying files, change package declaration in ALL Java files:

FROM:
package net.runelite.client.plugins.microbot.yourpluginname;

TO:  
package com.myplugins.yourpluginname;

## Files to Copy
- YourPluginNamePlugin.java
- YourPluginNameScript.java  
- YourPluginNameConfig.java
- YourPluginNameOverlay.java

## Import Statements
Update imports if they reference the old package:
- Change: net.runelite.client.plugins.microbot.yourpluginname.*
- To: com.myplugins.yourpluginname.*

## Critical Reminders
- Always copy FROM development TO export location
- Never forget to update package declarations
- Verify all files copied successfully before building