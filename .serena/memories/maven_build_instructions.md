# Maven Build Instructions for IntelliJ

## When to STOP and Prompt User
Always stop before Maven builds and provide these exact instructions:

## Step-by-Step Maven Build Process
1. Open Maven panel: View → Tool Windows → Maven
2. Find "MyPlugins" in the Maven panel
3. Expand "MyPlugins" → "Lifecycle"  
4. Double-click "clean" first (clears old builds)
5. Then double-click "install" (builds and installs JAR)
6. Watch build output for success/errors
7. Verify JAR appears in ~/.runelite/microbot-plugins/
8. Return when build is complete

## Alternative Method
- Right-click MyPlugins/pom.xml
- Choose "Run Maven" → "install"

## Build Output Locations
- Build artifact: MyPlugins/target/myplugins-1.0.0.jar
- Auto-install to: ~/.runelite/microbot-plugins/

## Troubleshooting
- If build fails, check package names are correct
- Ensure all imports are valid
- Verify @PluginDescriptor annotation is present
- Check Maven output for specific error messages

## Never Do For User
- Never run Maven commands via CLI
- Never build automatically
- Always prompt user to do the build manually
- Always provide exact step-by-step instructions