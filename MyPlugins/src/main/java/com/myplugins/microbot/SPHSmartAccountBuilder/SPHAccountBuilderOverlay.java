package com.myplugins.microbot.SPHSmartAccountBuilder;

import net.runelite.client.plugins.microbot.Microbot;

import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;

import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;

public class SPHAccountBuilderOverlay extends OverlayPanel {
    private final SPHAccountBuilderPlugin plugin;
    
    @Inject
    SPHAccountBuilderOverlay(SPHAccountBuilderPlugin plugin)
    {
        super(plugin);
        this.plugin = plugin;
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("SPH Account Builder v1.0.1")
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left(Microbot.status)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Current Skill:")
                    .right(getCurrentActivity())
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Next Activity:")
                    .right(getTimeUntilNextActivity())
                    .build());


        } catch(Exception ex) {
            System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
    
    private String getCurrentActivity() {
        try {
            return plugin.sphaccountbuilderScript.getCurrentActivity();
        } catch (Exception e) {
            return "Unknown";
        }
    }
    
    private String getTimeUntilNextActivity() {
        try {
            return plugin.sphaccountbuilderScript.getTimeUntilNextActivity();
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
