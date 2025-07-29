# Claude Microbot Development Instructions

## Recent Completed Work - SPH Account Builder Plugin ✅
- ✅ Fletching integration with bronze arrow prioritization
- ✅ Configurable activity timing (min/max minutes)  
- ✅ Plugin naming with bright orange [SPH] branding
- ✅ Proper plugin categorization for Microbot folder
- ✅ Equal weight activity distribution system
- ✅ Build system and JAR distribution setup

## Core Operating Principles

### 1. Tool Discovery & Usage
- **ALWAYS** check available tools at the start of each session
- Use `mcp__serena__get_current_config` to understand available MCP servers
- Prioritize Serena MCP tools for code analysis and memory management
- **ALWAYS** use Context7 MCP server when implementing or coding

### 2. Project Understanding Protocol
- Read through the entire project directory structure systematically
- Understand syntax, context, architecture, and component interactions
- Map out how different modules work together
- Document findings in Serena memories for future reference

### 3. Sequential Thinking & Planning
- **MANDATORY**: Use sequential thinking for ALL development tasks
- Break down tasks into logical, sequential steps
- Stay in planning mode until 99% confident in execution approach
- Ask clarifying questions before implementation
- Create detailed todo lists for complex tasks
- **CRITICAL**: When time to test or build, STOP and prompt user to do it manually

### 4. Memory Management
- Use `mcp__serena__write_memory` after completing todo lists or milestones
- Store key insights about:
  - API patterns and usage
  - Build configurations
  - Plugin architecture
  - Common issues and solutions
- Read relevant memories before starting new tasks

### 5. Research & Documentation
- **ALWAYS** fetch external pages and GitHub links to understand:
  - RuneLite API documentation
  - OSRS game mechanics
  - Similar plugin implementations
  - Best practices and patterns
- Use WebFetch tool for external documentation

### 6. Implementation Guidelines
- **MANDATORY**: Use ultrathinking for all implementations
- **MANDATORY**: Use MCP Context7 server for all coding tasks
- Focus on plugin-specific builds, not entire Microbot rebuilds
- Ensure all plugins compile successfully
- Document plugin JAR location and installation instructions

## Microbot Specific Context

### What is Microbot?
- Fork of RuneLite with custom API extensions
- Designed for building automation scripts for Old School RuneScape (OSRS)
- Provides additional utilities and helpers beyond standard RuneLite
- **SUPPORTS SIDELOADING**: Can load plugins from JAR files at runtime

### Development Focus - Dual-Location Workflow
- **DEVELOPMENT LOCATION**: Develop plugins in main microbot plugin directory for testing
  - Located at: `microbot/runelite-client/src/main/java/net/runelite/client/plugins/microbot/yourplugin/`
  - Allows real-time testing within RuneLite using IntelliJ run configuration
- **EXPORT LOCATION**: MyPlugins project for JAR creation and distribution
  - Located at: `MyPlugins/src/main/java/com/myplugins/yourplugin/`
  - Independent Maven project that compiles separately from Microbot
  - Automatic installation to sideloading directory (`~/.runelite/microbot-plugins/`)

### MyPlugins Project Structure
```
MyPlugins/
├── pom.xml                    # Standalone Maven configuration
├── README.md                  # Project documentation
└── src/main/java/com/myplugins/
    └── [plugin-name]/
        ├── [PluginName]Config.java
        ├── [PluginName]Overlay.java
        ├── [PluginName]Plugin.java
        └── [PluginName]Script.java
```

### Build Commands for MyPlugins
```bash
cd MyPlugins
mvn clean install                    # Build and auto-install to sideloading directory
mvn clean install -Pno-install      # Build only, no installation
mvn compile                         # Quick compilation for development
```

### JAR File Locations
1. **Build Output**: `/MyPlugins/target/myplugins-1.0.0.jar`
2. **Sideloading Directory**: `%USERPROFILE%/.runelite/microbot-plugins/` (Windows)
3. **Sideloading Directory**: `~/.runelite/microbot-plugins/` (macOS/Linux)

### Key Deliverables for Each Plugin
1. Compiled plugin JAR file: `myplugins-1.0.0.jar`
2. JAR automatically copied to sideloading directory during `mvn install`
3. Plugin loads automatically when Microbot starts
4. Configuration via standard RuneLite config system
5. No manual installation steps required

## Complete Plugin Development Workflow

### Phase 1: Initialization & Planning
1. **Tool Discovery**
   - Check available tools with `mcp__serena__get_current_config`
   - Load relevant Serena memories for project context
   - **MANDATORY**: Use sequential thinking to plan the entire development process

2. **Requirements Analysis**
   - Understand plugin requirements thoroughly
   - Research similar implementations using Context7
   - Ask ALL clarifying questions before coding
   - Create detailed implementation plan

### Phase 2: Development Location Setup
**Create plugin in development location:**
```
microbot/runelite-client/src/main/java/net/runelite/client/plugins/microbot/
└── yourpluginname/                    ← Create HERE for development
    ├── YourPluginNamePlugin.java
    ├── YourPluginNameScript.java  
    ├── YourPluginNameConfig.java
    └── YourPluginNameOverlay.java
```

**Package declaration in files:**
```java
package net.runelite.client.plugins.microbot.yourpluginname;
```

### Phase 3: Implementation
- **MANDATORY**: Use Context7 MCP server for all coding tasks
- **MANDATORY**: Use sequential thinking throughout implementation
- Follow existing microbot plugin patterns
- Write clean, maintainable code with proper error handling

### Phase 4: Testing Protocol
**STOP HERE - Prompt user to test:**
```
🛑 TESTING TIME: Please run the RuneLite configuration in IntelliJ:
1. Open IntelliJ Run Configurations
2. Select "RuneLite" configuration  
3. Click the green play button
4. Test your plugin in the actual game environment
5. Verify all functionality works as expected
6. Return here when testing is complete
```

### Phase 5: Export Preparation
**When user confirms plugin works, copy to export location:**

**Copy FROM (Development):**
```
microbot/runelite-client/src/main/java/net/runelite/client/plugins/microbot/yourpluginname/
```

**Copy TO (Export):**
```
MyPlugins/src/main/java/com/myplugins/yourpluginname/
```

**CRITICAL**: Change package declarations in copied files:
```java
// FROM:
package net.runelite.client.plugins.microbot.yourpluginname;

// TO:
package com.myplugins.yourpluginname;
```

### Phase 6: Maven Build Protocol
**STOP HERE - Prompt user to build:**
```
🛑 BUILD TIME: Please build the JAR using IntelliJ Maven panel:

Step-by-Step Instructions:
1. Open Maven panel: View → Tool Windows → Maven
2. Find "MyPlugins" in the Maven panel
3. Expand "MyPlugins" → "Lifecycle"
4. Double-click "clean" first
5. Then double-click "install"
6. Watch build output for success/errors
7. Check that JAR appears in ~/.runelite/microbot-plugins/
8. Return here when build is complete

Alternative Method:
- Right-click MyPlugins/pom.xml
- Choose "Run Maven" → "install"
```

### Phase 7: Documentation & Memory Storage
- Store successful patterns in Serena memories
- Document any issues encountered and solutions
- Update CLAUDE.md if new patterns discovered

## Critical Reminders
- Never assume - always verify
- Research before implementing
- Stay in planning mode until certain
- Use Context7 for all coding
- Document everything in memories
- Focus on plugin builds, not full rebuilds