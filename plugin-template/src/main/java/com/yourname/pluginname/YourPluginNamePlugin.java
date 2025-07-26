package com.yourname.pluginname;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = PluginDescriptor.Default + "Your Plugin Name",
        description = "Your microbot plugin description",
        tags = {"microbot", "automation", "your-tag"},
        enabledByDefault = false
)
@Slf4j
public class YourPluginNamePlugin extends Plugin {

    @Inject
    private YourPluginNameConfig config;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private YourPluginNameOverlay overlay;

    @Inject
    private YourPluginNameScript script;

    @Provides
    YourPluginNameConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(YourPluginNameConfig.class);
    }

    @Override
    protected void startUp() throws AWTException {
        log.info("Your Plugin Name started!");
        
        // Add overlay to display plugin status
        if (overlayManager != null) {
            overlayManager.add(overlay);
        }
        
        // Start the main script
        script.run(config);
    }

    @Override
    protected void shutDown() {
        log.info("Your Plugin Name stopped!");
        
        // Remove overlay
        if (overlayManager != null) {
            overlayManager.remove(overlay);
        }
        
        // Stop the script
        script.shutdown();
    }

    @Subscribe
    public void onGameTick(GameTick tick) {
        // Handle game tick events if needed
        // This is called every game tick (approximately every 600ms)
    }
}