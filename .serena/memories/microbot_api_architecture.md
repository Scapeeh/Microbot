# Microbot API Architecture & RuneLite Integration

## API Hierarchy

### RuneLite Core API (Base Layer)
- **Location**: `net.runelite.api.*`
- **Purpose**: Core game client interaction (Events, Client, NPCs, GameObjects)
- **Examples**: 
  - `net.runelite.api.events.GameTick`
  - `net.runelite.api.Client`
  - `net.runelite.api.NPC`

### RuneLite Client Framework (Middle Layer)  
- **Location**: `net.runelite.client.*`
- **Purpose**: Plugin framework, UI, configuration
- **Examples**:
  - `net.runelite.client.plugins.Plugin`
  - `net.runelite.client.plugins.PluginDescriptor` 
  - `net.runelite.client.config.Config`
  - `net.runelite.client.ui.overlay.OverlayPanel`

### Microbot Extension Layer (Top Layer)
- **Location**: `net.runelite.client.plugins.microbot.*`
- **Purpose**: Automation utilities and enhanced functionality
- **Key Utilities**:
  - `net.runelite.client.plugins.microbot.Microbot` - Core microbot class
  - `net.runelite.client.plugins.microbot.Script` - Base automation script
  - `net.runelite.client.plugins.microbot.util.*` - Utility classes

## Critical Microbot Utilities

### Player & Status
- `Rs2Player` - Player status, skills, location, actions
- `Microbot.isLoggedIn()` - Login status check
- `Microbot.status` - Current bot status string

### Interaction Utilities
- `Rs2Inventory` - Inventory management and interactions
- `Rs2Bank` - Banking operations and item management  
- `Rs2Equipment` - Equipment slots and worn items
- `Rs2GameObject` - Game object interactions (trees, rocks, etc.)
- `Rs2Npc` - NPC interactions and targeting

### Navigation & Movement
- `Rs2Walker` - Pathfinding and world navigation
- `Rs2Camera` - Camera control and targeting

### Anti-Detection
- `Rs2Antiban` - Human-like behavior simulation
- `Rs2Random` - Randomized delays and actions

## Plugin Development Dependencies

### Maven Dependencies Required
```xml
<dependency>
    <groupId>net.runelite</groupId>
    <artifactId>client</artifactId>
    <version>1.11.12-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>net.runelite</groupId>
    <artifactId>runelite-api</artifactId>
    <version>1.11.12-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

### Build Requirement
- Main Microbot project MUST be built first (`mvn clean install`)
- Dependencies installed to local Maven repository (`~/.m2/repository`)
- MyPlugins project then references these local dependencies