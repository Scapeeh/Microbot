# Claude Microbot Development Instructions

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
- Break down tasks into logical, sequential steps
- Stay in planning mode until 99% confident in execution approach
- Ask clarifying questions before implementation
- Create detailed todo lists for complex tasks

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

### Development Focus - MyPlugins Standalone Project
- **PRIMARY APPROACH**: Use the MyPlugins standalone project for all plugin development
- Located at: `/Microbot/MyPlugins/` 
- Independent Maven project that compiles separately from Microbot
- Automatic installation to sideloading directory (`~/.runelite/microbot-plugins/`)
- No need to rebuild entire Microbot client

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

## Workflow Process

1. **Initialization**
   - Check available tools
   - Read project structure
   - Load relevant memories
   
2. **Planning Phase**
   - Understand requirements thoroughly
   - Research similar implementations
   - Ask all clarifying questions
   - Create detailed implementation plan
   
3. **Implementation Phase**
   - **MANDATORY**: Use Context7 and ultrathinking for all coding
   - Work within MyPlugins project structure
   - Follow existing code patterns
   - Write clean, maintainable code
   - Add appropriate error handling
   
4. **Build & Test Phase**
   - Build using: `cd MyPlugins && mvn clean install`
   - JAR created at: `/MyPlugins/target/myplugins-1.0.0.jar`
   - JAR automatically copied to: `%USERPROFILE%/.runelite/microbot-plugins/`
   - Test by starting Microbot (plugin loads automatically)
   - No manual file copying required
   
5. **Documentation Phase**
   - Update memories with learnings
   - Document any gotchas or special considerations
   - Create user-friendly instructions

## Critical Reminders
- Never assume - always verify
- Research before implementing
- Stay in planning mode until certain
- Use Context7 for all coding
- Document everything in memories
- Focus on plugin builds, not full rebuilds