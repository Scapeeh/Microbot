# Account Builder Context - Complete Development Guide

## Project Overview

### What is SPH Account Builder?
A sophisticated OSRS (Old School RuneScape) account development plugin built on the Microbot framework. This plugin automatically trains F2P and P2P accounts by rotating between multiple skills with intelligent resource management, gear optimization, and webwalker integration.

**Key Features:**
- Automatic skill rotation with configurable timing
- Smart gear checking (bank-first approach before purchasing)
- Intelligent coin management (full stack withdrawal, strategic deposits)
- Auto-equipping of beneficial teleport items for webwalker optimization
- F2P/P2P world detection with automatic activity adjustment
- Comprehensive error handling and plugin safety mechanisms

## Architecture Overview

### Project Structure
```
microbot/
├── MyPlugins/                          # Standalone plugin development
│   ├── pom.xml                         # Maven config with JAR packaging
│   └── src/main/java/com/myplugins/SPHSmartAccountBuilder/
│       ├── SPHAccountBuilderPlugin.java      # Main plugin class
│       ├── SPHAccountBuilderScript.java      # Core logic engine
│       ├── SPHAccountBuilderConfig.java      # Configuration interface
│       └── SPHAccountBuilderOverlay.java     # UI overlay display
├── runelite-client/                    # Main microbot framework
├── runelite-api/                       # Core API definitions
└── local/                              # Local JDK 11 & Maven installations
```

### Plugin Architecture Pattern
- **Plugin Class**: Entry point, lifecycle management, dependency injection
- **Script Class**: Main execution logic, scheduled loop, state management
- **Config Class**: User configuration interface using RuneLite config system
- **Overlay Class**: Real-time UI display with activity status and countdown timer

## Microbot Framework Understanding

### What is Microbot?
- Fork of RuneLite with extensive automation API extensions
- Provides high-level utilities for common OSRS operations (banking, trading, combat, etc.)
- Built-in anti-ban systems, break handlers, and safety mechanisms
- Supports plugin sideloading from `~/.runelite/microbot-plugins/` directory

### Key Microbot Extensions
- **Rs2Bank**: Enhanced banking operations with smart withdrawal/deposit
- **Rs2Inventory**: Advanced inventory management and item interactions
- **Rs2Equipment**: Equipment slot management and wearing operations
- **Rs2GrandExchange**: Automated trading with price calculations and markup
- **Rs2Walker**: Intelligent pathfinding with teleport optimization
- **Rs2Player**: Player state checking, skill levels, world detection
- **Rs2Antiban**: Anti-detection systems with activity-specific templates

### Plugin Development Workflow
1. **Development**: Code in main microbot plugin directory for testing
2. **Export**: Copy to MyPlugins project for JAR compilation
3. **Build**: `mvn clean install` creates JAR and auto-installs to sideloading directory
4. **Runtime**: Plugin loads automatically when Microbot starts

## API Knowledge & Patterns

### RuneLite API Core Concepts
- **Event-driven architecture**: Subscribe to game events (GameTick, inventory changes, etc.)
- **Widget system**: UI interaction through widget IDs and component references
- **Item management**: ItemManager for price data, item IDs, and metadata
- **Configuration system**: Type-safe config interfaces with automatic UI generation

### Microbot API Extensions
- **Utility-first design**: High-level methods that handle complex operations
- **Safety mechanisms**: Built-in timeout handling, state validation, break integration
- **Smart waiting**: `sleepUntil()` patterns for condition-based delays
- **Resource management**: Automatic cleanup and state restoration

### Essential API Patterns

#### Banking Pattern
```java
// Open bank and ensure readiness
Rs2Bank.walkToBank() / Rs2Bank.openBank()
sleepUntil(() -> Rs2Bank.isOpen(), timeout)

// Smart checking before operations
if (Rs2Bank.getBankItem("item", exact) != null) {
    int quantity = Rs2Bank.getBankItem("item", exact).getQuantity();
    // Proceed with withdrawal logic
}

// Strategic deposits
Rs2Bank.depositAll() / Rs2Bank.depositAllExcept("Coins")
```

#### Grand Exchange Pattern
```java
// Price calculation with markup
Rs2ItemManager itemManager = new Rs2ItemManager();
int basePrice = itemManager.getGEPrice(itemId);
int totalCost = (int)(basePrice * quantity * 1.30); // 30% markup

// Offer creation
GrandExchangeRequest.builder()
    .itemName(item).exact(true)
    .action(GrandExchangeAction.BUY)
    .percent(quantity <= 3 ? 99 : 20)
    .quantity(quantity).build()
```

#### Equipment Management Pattern
```java
// Check equipment slots
Rs2Equipment.get(EquipmentInventorySlot.RING)
Rs2Equipment.isWearing("item name")

// Auto-equipping with validation
Rs2Inventory.wield(itemName)
sleepUntil(() -> Rs2Equipment.isWearing(itemName), timeout)
```

#### State Management Pattern
```java
// Multi-condition safety checks
if (!Microbot.isLoggedIn()) return;
if (!super.run()) return;
if (Rs2Player.isMoving() || Rs2Player.isAnimating()) return;

// World-specific logic
if (Rs2Player.isInMemberWorld()) {
    // P2P-only operations
}
```

## Current Implementation Details

### Core Features Implemented

#### 1. Skill Rotation System
- **F2P Skills**: Woodcutting, Mining, Fishing, Smelting, Firemaking, Cooking, Crafting, Selling
- **P2P Additional**: Fletching (Bronze arrows)
- **Dynamic distribution**: Balanced probability ranges for fair skill distribution
- **Level-based progression**: Tool upgrades based on skill levels

#### 2. Smart Gear Management
- **Bank-first checking**: Always check bank before purchasing items
- **Intelligent withdrawal**: Use existing items before buying new ones
- **Detailed logging**: Clear feedback on gear availability decisions
- **Essential items system**: Comprehensive list management for required tools

#### 3. Financial Management
- **Full cash stack withdrawal**: Withdraw all coins when purchasing needed
- **Strategic deposits**: Keep coins during buying session, deposit all after completion
- **Affordability checking**: Minimum 50k coins required, auto-stop if insufficient
- **Cost calculation**: 30% markup pricing for reliable GE purchases

#### 4. Activity Timing & UI
- **Configurable intervals**: Min/max minutes between activity changes
- **Real-time countdown**: Overlay shows time until next activity switch
- **Status display**: Current activity, initialization state, timing information
- **Clean UI**: "SPH Account Builder v1.0.1" branding with organized layout

#### 5. Webwalker Integration
- **Teleport jewelry priority**: Ring of dueling, Combat bracelet, Amulet of glory
- **Members-only detection**: Only equip/buy teleport items in P2P worlds
- **Auto-equipping system**: Automatically wear beneficial items from inventory
- **Walker optimization**: Equipped teleports improve pathfinding efficiency

### Safety & Reliability Features

#### Initialization System
- **Multi-tick verification**: Requires 3 stable ticks before starting
- **State validation**: Checks login status, movement, animations
- **Progressive loading**: Clear status feedback during startup
- **Error recovery**: Graceful handling of initialization failures

#### Break Handler Integration
- **Automatic break detection**: Respects break handler timing
- **State locking**: Prevents operations during breaks
- **Safe logout procedures**: Proper cleanup before breaks
- **Resume capability**: Smooth continuation after breaks

#### Plugin Safety
- **Affordability enforcement**: Auto-stop if insufficient funds
- **Error logging**: Comprehensive exception handling with stack traces
- **State consistency**: Proper cleanup on shutdown
- **Resource management**: Prevents memory leaks and hanging operations

## Development Guidelines

### Code Style & Patterns
- **Camel case naming**: Follow Java conventions
- **Descriptive logging**: Use `Microbot.log()` for user feedback
- **Error handling**: Wrap complex operations in try-catch blocks
- **State validation**: Always check preconditions before operations
- **Timeout handling**: Use `sleepUntil()` with reasonable timeouts (2-5 seconds typical)

### Testing Approach
- **Manual testing**: Use IntelliJ RuneLite configuration for development testing
- **Incremental changes**: Test small modifications before complex features
- **Log analysis**: Monitor console output for unexpected behavior
- **Break testing**: Verify break handler integration
- **Resource monitoring**: Watch for memory leaks or hanging operations

### Common Issues & Solutions
- **Banking failures**: Always verify bank is open before operations
- **Timing issues**: Use appropriate delays and condition checking
- **Item detection**: Use exact name matching where possible
- **World detection**: Check member status before P2P-only operations
- **GE failures**: Handle timeout scenarios and offer completion checking

## Future Enhancement Opportunities

### Potential Improvements
- **Dynamic pricing**: Real-time GE price monitoring for better margins
- **Advanced pathing**: Custom routes for specific activities
- **Skill prioritization**: User-configurable skill focus weights
- **Achievement integration**: Automatic quest and achievement completion
- **Performance analytics**: Detailed statistics and progress tracking

### API Evolution Awareness
- **RuneLite updates**: Monitor for API changes and deprecations
- **Microbot enhancements**: Leverage new utility methods as they become available
- **Game updates**: Adapt to OSRS content and mechanic changes
- **Security considerations**: Stay updated with anti-detection best practices

---

## Claude Agent Instructions

When working with this project:

1. **Read the existing code first** - Understand current implementation before making changes
2. **Follow established patterns** - Use existing API patterns and code style
3. **Test incrementally** - Make small changes and verify functionality
4. **Log extensively** - Provide clear feedback for debugging and user understanding
5. **Consider safety first** - Always implement proper error handling and state validation
6. **Respect the architecture** - Maintain separation between Plugin, Script, Config, and Overlay classes

The codebase is well-structured and follows consistent patterns. Focus on understanding the existing logic flow before implementing new features or modifications.