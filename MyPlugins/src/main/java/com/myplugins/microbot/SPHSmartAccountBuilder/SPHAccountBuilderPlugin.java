package com.myplugins.microbot.SPHSmartAccountBuilder;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;

import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "<html>[<font color=#FF8C00>SPH</font>] Account Builder",
        description = "F2P Account Builder with Optional Members/Fletching Support",
        tags = {"microbot", "account builder", "f2p", "skilling"},
        enabledByDefault = false
)
@Slf4j
public class SPHAccountBuilderPlugin extends Plugin {
    @Inject
    private SPHAccountBuilderConfig config;
    @Provides
    SPHAccountBuilderConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SPHAccountBuilderConfig.class);
    }

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private SPHAccountBuilderOverlay sphaccountbuilderOverlay;

    @Inject
    SPHAccountBuilderScript sphaccountbuilderScript;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(sphaccountbuilderOverlay);

        }
        sphaccountbuilderScript.run(config);
        sphaccountbuilderScript.shouldThink = true;
        sphaccountbuilderScript.scriptStartTime = System.currentTimeMillis();
        Rs2Antiban.activateAntiban(); // Enable Anti Ban
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.antibanSetupTemplates.applyUniversalAntibanSetup();
        Rs2Antiban.setActivity(Activity.GENERAL_WOODCUTTING);
    }

    @Override
    protected void shutDown() {
        Rs2Antiban.resetAntibanSettings();
        Rs2Antiban.deactivateAntiban();
        sphaccountbuilderScript.shutdown();
        overlayManager.remove(sphaccountbuilderOverlay);

    }


    int ticks = 10;
    @Subscribe
    public void onGameTick(GameTick tick)
    {
        //System.out.println(getName().chars().mapToObj(i -> (char)(i + 3)).map(String::valueOf).collect(Collectors.joining()));

        if (ticks > 0) {
            ticks--;
        } else {
            ticks = 10;
        }

    }

}
