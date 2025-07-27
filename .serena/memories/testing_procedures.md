# Testing Procedures for Microbot Plugins

## IntelliJ RuneLite Configuration
- Main Class: net.runelite.client.RuneLite
- Module: client
- VM Options: -Xmx2g -XX:+UseG1GC -Djava.awt.headless=false
- Working Directory: $PROJECT_DIR$

## Testing Protocol
1. Develop plugin in: microbot/runelite-client/src/main/java/net/runelite/client/plugins/microbot/yourpluginname/
2. Run "RuneLite" configuration in IntelliJ
3. Plugin loads automatically in the client
4. Test all functionality in actual game environment
5. Verify configuration options work correctly
6. Check overlay displays properly
7. Test error handling and edge cases

## When to STOP and Prompt User
- Before running tests: "Please test your plugin using RuneLite configuration"
- Provide exact steps for user to follow
- Wait for user confirmation that testing is complete
- Only proceed to export phase after successful testing

## Testing Checklist
- Plugin appears in Plugin Hub
- Configuration panel opens without errors
- Script starts/stops correctly
- Overlay displays expected information
- No console errors or exceptions
- Plugin handles game state changes properly