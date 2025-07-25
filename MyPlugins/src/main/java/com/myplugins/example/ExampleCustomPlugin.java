package com.myplugins.example;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
    name = "Example Custom Plugin",
    description = "An example plugin from MyPlugins standalone project",
    tags = {"example", "myplugins", "custom"},
    enabledByDefault = false
)
@Slf4j
public class ExampleCustomPlugin extends Plugin {
    
    @Inject
    private ExampleCustomConfig config;
    
    @Inject
    private OverlayManager overlayManager;
    
    @Inject
    private ExampleCustomOverlay overlay;
    
    @Inject
    private ExampleCustomScript script;
    
    @Override
    protected void startUp() throws AWTException {
        log.info("MyPlugins Example Custom Plugin started!");
        
        // Add overlay
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }
        
        // Start script
        script.run(config);
    }
    
    @Override
    protected void shutDown() {
        log.info("MyPlugins Example Custom Plugin stopped!");
        
        // Remove overlay
        if (overlayManager != null) {
            overlayManager.remove(overlay);
        }
        
        // Stop script
        script.shutdown();
    }
    
    @Provides
    ExampleCustomConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(ExampleCustomConfig.class);
    }
}