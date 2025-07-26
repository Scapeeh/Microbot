# Microbot Plugin Template

This template provides everything you need to create a custom microbot plugin that can be distributed as a standalone JAR file.

## Quick Start

### 1. Clone/Copy the Template
Copy the entire `plugin-template/` directory and rename it to your plugin name.

### 2. Customize the Plugin
Edit these files with your plugin details:

**pom.xml:**
- Change `<groupId>` to your package (e.g., `com.yourname`)  
- Change `<artifactId>` to your plugin name (e.g., `my-awesome-plugin`)
- Update `<name>` and `<description>`
- Update the `<Plugin-Class>` in the manifest to match your main plugin class

**Java Files:**
- Rename the package from `com.yourname.pluginname` to your actual package
- Rename all classes from `YourPluginName*` to your actual plugin name
- Update the `@PluginDescriptor` in your main plugin class

### 3. Build the Plugin

From the plugin directory, run:
```bash
mvn clean package
```

This creates a JAR file in the `target/` directory: `your-plugin-name-1.0.0.jar`

### 4. Install the Plugin

Copy the JAR file to your RuneLite plugins directory:
```
~/.runelite/microbotplugins/your-plugin-name-1.0.0.jar
```

On Windows: `%USERPROFILE%/.runelite/microbotplugins/`

### 5. Use the Plugin

1. Restart RuneLite
2. Go to Plugin Settings
3. Find your plugin in the list
4. Enable it and configure settings

## Development

### Plugin Structure

- **Plugin Class**: Main entry point, handles startup/shutdown
- **Config Class**: Configuration interface for plugin settings  
- **Overlay Class**: UI overlay that displays on screen
- **Script Class**: Main automation logic using microbot APIs

### Available APIs

Your plugin has access to all microbot utilities:

- `Rs2Player` - Player actions and status
- `Rs2Inventory` - Inventory interactions
- `Rs2Bank` - Banking operations
- `Rs2Combat` - Combat actions
- `Rs2Magic` - Spellcasting
- `Rs2Prayer` - Prayer management
- `Rs2GameObject` - Interact with game objects
- `Rs2Npc` - NPC interactions
- `Rs2Walker` - Movement and pathfinding
- `Rs2Antiban` - Anti-ban measures
- And many more!

### Example Usage

```java
// Check if inventory is full
if (Rs2Inventory.isFull()) {
    // Bank items
    if (Rs2Bank.openBank()) {
        Rs2Bank.depositAll();
    }
}

// Find and mine a rock
GameObject rock = Rs2GameObject.findObjectById(11364);
if (rock != null) {
    Rs2GameObject.interact(rock, "Mine");
    Rs2Player.waitForAnimation();
}

// Use food when low health
if (Rs2Player.getCurrentHealth() < 50) {
    Rs2Inventory.interact("Lobster", "Eat");
}
```

## Distribution

Once built, you can share your plugin JAR with others. They just need to:

1. Download your JAR file
2. Place it in `~/.runelite/microbotplugins/`  
3. Restart RuneLite
4. Enable the plugin

## Tips

- Always test your plugin thoroughly before distribution
- Use `config.debugMode()` for debug logging that users can toggle
- Include proper error handling in your script
- Use `Rs2Antiban` methods to make your automation more human-like
- Check if the player is logged in before performing actions
- Use `sleep()` or `Rs2Player.waitForAnimation()` between actions

## File Structure

```
plugin-template/
├── pom.xml                           # Maven build configuration
├── README.md                         # This file
└── src/main/java/com/yourname/pluginname/
    ├── YourPluginNamePlugin.java     # Main plugin class
    ├── YourPluginNameConfig.java     # Configuration interface
    ├── YourPluginNameOverlay.java    # UI overlay
    └── YourPluginNameScript.java     # Main automation logic
```