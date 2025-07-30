# Microbot Plugin Categorization - CORRECTED

## Actual Plugin Locations in Microbot Client

### Configuration Tab → Microbot Section (CORRECT for Community Plugins)
- **Location**: Main Configuration Tab (Spanner Icon) → Microbot Plugins section
- **For**: Both built-in AND community/sideloaded Microbot plugins
- **Descriptor**: `loadInSafeMode = false` for community plugins
- **Note**: This is the CORRECT location for all Microbot plugins

### Community Plugins Tab (RuneLite Only)
- **Location**: Separate "Community Plugins" tab  
- **For**: RuneLite Plugin Hub plugins only
- **Not Used**: For Microbot sideloaded plugins

## Correct Community Plugin Descriptor
```java
@PluginDescriptor(
    name = "Plugin Name",
    description = "Plugin Description",
    tags = {"microbot", "tag1", "tag2"},
    enabledByDefault = false,
    loadInSafeMode = false    // This marks it as community plugin
    // DO NOT use hidden = true (makes plugin invisible)
)
```

## Key Parameters Explained

### `loadInSafeMode = false`
- **Purpose**: Marks plugin as community/external
- **Effect**: Plugin appears in Configuration → Microbot section  
- **Required**: For all sideloaded plugins

### `hidden = true` ❌ AVOID
- **Effect**: Makes plugin completely invisible
- **Problem**: Plugin won't appear in any tab
- **Solution**: Remove this parameter entirely

## Sideloading Behavior
1. **JAR Location**: `~/.runelite/microbot-plugins/`
2. **Plugin Detection**: Microbot scans directory on startup
3. **Display Location**: Configuration Tab → Microbot Plugins section
4. **Categorization**: Same section as built-in Microbot plugins

## Final Understanding
- **Microbot plugins** (both built-in and community) appear in Configuration → Microbot
- **RuneLite community plugins** appear in separate Community Plugins tab
- **Never use `hidden = true`** - it breaks plugin visibility entirely