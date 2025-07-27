# Microbot Development Patterns and Best Practices

## Plugin Architecture Components
1. Plugin Class - Main entry point with @PluginDescriptor
2. Script Class - Automation logic extending Script class
3. Config Class - Configuration interface extending Config
4. Overlay Class - UI overlay extending OverlayPanel

## Common Microbot Utility Classes
- Rs2Player: Player actions and status
- Rs2Inventory: Inventory interactions  
- Rs2Bank: Banking operations
- Rs2Combat: Combat actions
- Rs2Magic: Spellcasting
- Rs2Prayer: Prayer management
- Rs2GameObject: Game object interactions
- Rs2Npc: NPC interactions
- Rs2Walker: Movement and pathfinding
- Rs2Antiban: Anti-ban measures

## Standard Script Pattern
```java
public boolean run(ConfigClass config) {
    mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
        try {
            if (!Microbot.isLoggedIn()) return;
            if (!super.run()) return;
            
            // Main automation logic here
            
        } catch (Exception ex) {
            log.error("Error in script", ex);
        }
    }, 0, 1000, TimeUnit.MILLISECONDS);
    return true;
}
```

## Critical Development Rules
- Always check Microbot.isLoggedIn() before actions
- Always call super.run() for proper script lifecycle
- Use proper error handling with try-catch blocks
- Implement proper shutdown() method
- Use Rs2Antiban methods for human-like behavior