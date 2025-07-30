# Microbot Compilation Troubleshooting Guide

## Environment Setup Requirements

### Java & Maven Configuration
- **Java Version**: OpenJDK 11 (specifically 11.0.2 or compatible)
- **Maven Version**: Apache Maven 3.9.6
- **Environment Setup**: Use provided `setup-env.sh` script
- **JAVA_HOME**: Must point to local JDK installation
- **PATH**: Must include both Java and Maven binaries

### Dependency Resolution Order
1. **Main Project First**: Build microbot core (`mvn clean install -DskipTests`)
2. **Dependencies Available**: Check `~/.m2/repository/net/runelite/`
3. **Plugin Build**: Then build MyPlugins project

## Common Compilation Errors & Fixes

### "Package does not exist" Errors
- **Cause**: Main microbot project not built
- **Fix**: Run `mvn clean install` in main microbot directory
- **Verification**: Check `~/.m2/repository/net/runelite/client/1.11.12-SNAPSHOT/`

### "Local variables must be final" Lambda Errors  
- **Cause**: Variables referenced in lambda expressions aren't effectively final
- **Fix**: Create final copy: `final String finalVar = originalVar;`
- **Pattern**: Always use final variables in lambda expressions

### "Cannot find symbol" Errors
- **Cause**: Missing imports or incorrect package references
- **Fix**: Verify imports match available classes in dependencies
- **Check**: Ensure using correct microbot package structure

### Environment Issues
- **Maven not found**: Verify PATH includes Maven bin directory
- **Java version mismatch**: Ensure Java 11 is active
- **WSL permissions**: Use proper file permissions in WSL environment

## Successful Build Indicators
- **Build Success**: `BUILD SUCCESS` message in Maven output
- **JAR Creation**: File appears in `target/` directory
- **Auto-installation**: JAR copied to `~/.runelite/microbot-plugins/`
- **Warnings Only**: Deprecation warnings are acceptable

## Build Commands Reference
```bash
# Setup environment
source setup-env.sh

# Main project build (required first)
mvn clean install -DskipTests

# Plugin project build  
cd MyPlugins
mvn clean install

# Compilation check only
mvn clean compile
```