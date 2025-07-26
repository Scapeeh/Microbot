package com.myplugins.example;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class ExampleCustomOverlay extends OverlayPanel {
    
    private final Client client;
    private final ExampleCustomConfig config;
    private final ExampleCustomScript script;
    
    @Inject
    private ExampleCustomOverlay(Client client, ExampleCustomConfig config, ExampleCustomScript script) {
        this.client = client;
        this.config = config;
        this.script = script;
        setPosition(OverlayPosition.TOP_LEFT);
    }
    
    @Override
    public Dimension render(Graphics2D graphics) {
        if (!config.enabled() || !script.isRunning()) {
            return null;
        }
        
        panelComponent.getChildren().add(LineComponent.builder()
            .left("MyPlugins Example")
            .leftColor(Color.CYAN)
            .build());
            
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Status:")
            .right(script.isRunning() ? "Running" : "Stopped")
            .rightColor(script.isRunning() ? Color.GREEN : Color.RED)
            .build());
            
        panelComponent.getChildren().add(LineComponent.builder()
            .left("Message:")
            .right(config.greeting())
            .rightColor(Color.WHITE)
            .build());
            
        if (client.getLocalPlayer() != null) {
            panelComponent.getChildren().add(LineComponent.builder()
                .left("Location:")
                .right(client.getLocalPlayer().getWorldLocation().toString())
                .rightColor(Color.YELLOW)
                .build());
        }
        
        return super.render(graphics);
    }
}