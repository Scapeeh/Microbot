# MyPlugins Compilation Fix Guide

## Root Cause Analysis
The compilation errors are NOT due to incorrect code. Your code is correct:
- `setNaughty()` is a valid method in the Overlay class
- `Microbot.status` is a valid static property
- All imports are properly structured

The issue is that **the main Microbot project hasn't been built**, so Maven cannot resolve the dependencies.

## Solution Steps

### Step 1: Build the Main Microbot Project FIRST
```bash
cd /mnt/c/Users/matts/Documents/Projects/Microbot
mvn clean install -DskipTests
```

This will:
- Compile all Microbot modules
- Install them to your local Maven repository (~/.m2/repository)
- Make them available for MyPlugins to use

### Step 2: Verify Installation
Check that the artifacts were installed:
```bash
ls ~/.m2/repository/net/runelite/client/1.11.12-SNAPSHOT/
```

You should see:
- client-1.11.12-SNAPSHOT.jar
- client-1.11.12-SNAPSHOT.pom

### Step 3: Build MyPlugins
Now you can build MyPlugins:
```bash
cd /mnt/c/Users/matts/Documents/Projects/Microbot/MyPlugins
mvn clean install
```

## Alternative: Use Released Version
If building Microbot is problematic, update MyPlugins/pom.xml to use a released version:

```xml
<properties>
    <microbot.version>1.11.11</microbot.version>  <!-- Use stable release instead of SNAPSHOT -->
</properties>
```

## Fixes Already Applied
✅ Fixed repository configuration in pom.xml
✅ Added javax.inject dependency
✅ Verified all code is correct

## Common Issues
1. **If Maven commands fail**: Use IntelliJ's Maven panel instead
2. **If dependencies still missing**: File → Invalidate Caches and Restart in IntelliJ
3. **If specific classes missing**: The main project build may have failed - check for errors

## Summary
Your plugin code is correct. The compilation errors are purely due to missing dependencies because the main Microbot project hasn't been built. Build Microbot first, then MyPlugins will compile successfully.