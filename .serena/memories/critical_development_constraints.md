# Critical Development Constraints

## NEVER MODIFY MICROBOT CORE
- **ONLY work within**: MyPlugins/src/main/java/com/myplugins/f2pAccountBuilder/
- **ONLY modify**: Files inside the f2pAccountBuilder plugin folder
- **ONLY rename**: The f2pAccountBuilder folder itself
- **NEVER touch**: Any files outside MyPlugins/f2pAccountBuilder/
- **NEVER modify**: runelite-client/ or any microbot core files
- **NEVER create**: New files outside the plugin folder

## Allowed Operations
✅ Edit files inside f2pAccountBuilder/ folder
✅ Rename f2pAccountBuilder/ folder 
✅ Add new files inside the plugin folder
✅ Modify plugin configuration and logic

## Forbidden Operations
❌ Any changes to microbot core utilities
❌ Modifying Rs2Walker, Rs2Combat, etc.
❌ Creating files outside plugin folder
❌ Touching runelite-client directory