# Microbot Development Environment Setup

## Overview
This document describes the complete setup for the Microbot RuneLite development environment in WSL2 Ubuntu.

## ✅ Installation Complete

### Java 11 (OpenJDK)
- **Location**: `/mnt/e/Development/Unity/Projects/microbot/local/jdk-11.0.2`
- **Version**: OpenJDK 11.0.2 (2019-01-15)
- **JAVA_HOME**: Set to local installation path

### Maven 3.9.6
- **Location**: `/mnt/e/Development/Unity/Projects/microbot/local/maven/apache-maven-3.9.6`
- **Binary**: `/mnt/e/Development/Unity/Projects/microbot/local/maven/apache-maven-3.9.6/bin/mvn`
- **Status**: ✅ Working correctly with Java 11

## Environment Configuration

### Quick Setup (Recommended)
Run this command in the microbot project directory:
```bash
source setup-env.sh
```

### Manual Setup
```bash
export JAVA_HOME=/mnt/e/Development/Unity/Projects/microbot/local/jdk-11.0.2
export PATH=$JAVA_HOME/bin:/mnt/e/Development/Unity/Projects/microbot/local/maven/apache-maven-3.9.6/bin:$PATH
```

### Permanent Setup
Add the contents of `bashrc-addition.txt` to your `~/.bashrc` file:
```bash
cat bashrc-addition.txt >> ~/.bashrc
source ~/.bashrc
```

## Compilation Status

### ✅ Successfully Compiled Modules
1. **Cache Module** - `cache/` - Compiles without errors
2. **RuneLite API** - `runelite-api/` - Compiles successfully (contains Cuboid class)

### ⚠️ Known Issues
1. **Maven Plugin Module** - Missing plugin descriptor (not critical for plugin development)
2. **MyPlugins Project** - Has compilation errors in plugin source code (requires code fixes)

## Usage Instructions

### Basic Compilation Commands
```bash
# Set up environment (run this first in each new terminal)
source setup-env.sh

# Compile individual modules
cd cache && mvn clean compile
cd runelite-api && mvn clean compile

# Full project compilation (may have issues with maven-plugin module)
mvn clean compile -DskipTests
```

### MyPlugins Development
```bash
# Set up environment
source setup-env.sh

# Compile MyPlugins (will show compilation errors that need fixing)
cd MyPlugins && mvn clean compile
```

## Project Structure
```
microbot/
├── local/                          # Local installations
│   ├── jdk-11.0.2/                # OpenJDK 11 installation
│   └── maven/apache-maven-3.9.6/  # Maven installation
├── cache/                          # Game cache handling (✅ compiles)
├── runelite-api/                   # Core API with Cuboid class (✅ compiles)
├── runelite-client/                # Main application + microbot plugins
├── runelite-maven-plugin/          # Build tools (⚠️ plugin descriptor issue)
├── MyPlugins/                      # Custom plugin development (⚠️ needs fixes)
├── setup-env.sh                    # Environment setup script
├── bashrc-addition.txt             # Permanent bash configuration
└── ENVIRONMENT_SETUP.md            # This documentation
```

## Verification Commands
```bash
# Verify Java installation
java -version
# Expected: openjdk version "11.0.2" 2019-01-15

# Verify Maven installation
mvn -version
# Expected: Apache Maven 3.9.6 with Java version: 11.0.2

# Test compilation
source setup-env.sh
cd cache && mvn clean compile
# Expected: BUILD SUCCESS
```

## Next Steps
1. **Environment is ready** - Java 11 and Maven are properly configured
2. **Core modules compile** - Basic microbot framework is functional
3. **MyPlugins needs fixes** - Plugin source code has compilation errors that need to be resolved
4. **Development ready** - You can now start fixing plugin code and developing new features

## Troubleshooting

### If Java is not found
```bash
echo $JAVA_HOME
# Should show: /mnt/e/Development/Unity/Projects/microbot/local/jdk-11.0.2
source setup-env.sh
```

### If Maven compilation fails
```bash
# Ensure environment is loaded
source setup-env.sh
mvn -version
# Verify Maven shows Java 11.0.2
```

### For permission issues
```bash
chmod +x setup-env.sh
```

---
**Status**: ✅ Environment Setup Complete - Ready for Development