package com.myplugins.example;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;

import java.util.concurrent.TimeUnit;

public class ExampleCustomScript extends Script {
    
    public static double version = 1.0;
    private ExampleCustomConfig config;
    
    public boolean run(ExampleCustomConfig config) {
        this.config = config;
        
        // Important: Disable auto-run if not needed
        Microbot.enableAutoRunOn = false;
        
        // Schedule the main script loop
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            if (!super.run()) return;
            if (!config.enabled()) return;
            
            try {
                long startTime = System.currentTimeMillis();
                
                // Example script logic
                executeScriptLogic();
                
                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("[MyPlugins Example] Loop completed in " + totalTime + "ms");
                
            } catch (Exception ex) {
                System.out.println("[MyPlugins Example] Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }, 0, config.interval(), TimeUnit.MILLISECONDS);
        
        return true;
    }
    
    private void executeScriptLogic() {
        // Example: Check if player is logged in
        if (!Rs2Player.isLoggedIn()) {
            System.out.println("[MyPlugins Example] Player not logged in");
            return;
        }
        
        // Example: Get player information
        var localPlayer = Microbot.getClient().getLocalPlayer();
        if (localPlayer != null) {
            var location = localPlayer.getWorldLocation();
            var animation = localPlayer.getAnimation();
            
            System.out.println("[MyPlugins Example] Player at: " + location + 
                             ", Animation: " + animation);
        }
        
        // Add your custom script logic here!
        // Examples:
        // - Rs2Bank.openBank();
        // - Rs2Inventory.use("item name");
        // - Rs2Npc.attack("monster name");
        // - Rs2GameObject.interact("object name", "action");
    }
    
    @Override
    public void shutdown() {
        System.out.println("[MyPlugins Example] Shutting down script");
        super.shutdown();
    }
}