package com.yourname.pluginname;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayMenuEntry;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

import static net.runelite.api.MenuAction.RUNELITE_OVERLAY_CONFIG;
import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class YourPluginNameOverlay extends OverlayPanel {

    private final Client client;
    private final YourPluginNamePlugin plugin;
    private final YourPluginNameConfig config;
    private final YourPluginNameScript script;

    @Inject
    private YourPluginNameOverlay(Client client, YourPluginNamePlugin plugin, 
                                  YourPluginNameConfig config, YourPluginNameScript script) {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.script = script;
        
        setPosition(OverlayPosition.TOP_LEFT);
        getMenuEntries().add(new OverlayMenuEntry(RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Your Plugin Name overlay"));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        // Only render if plugin is enabled
        if (!config.enablePlugin()) {
            return null;
        }

        panelComponent.getChildren().clear();

        // Title
        panelComponent.getChildren().add(TitleComponent.builder()
                .text("Your Plugin Name")
                .color(Color.GREEN)
                .build());

        // Script status
        String status = script.isRunning() ? "Running" : "Stopped";
        Color statusColor = script.isRunning() ? Color.GREEN : Color.RED;
        
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Status:")
                .right(status)
                .rightColor(statusColor)
                .build());

        // Example configuration display
        panelComponent.getChildren().add(LineComponent.builder()
                .left("Delay:")
                .right(config.exampleDelay() + "ms")
                .build());

        if (config.debugMode()) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Debug:")
                    .right("ON")
                    .rightColor(Color.YELLOW)
                    .build());
        }

        return super.render(graphics);
    }
}