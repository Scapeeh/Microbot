package com.myplugins.microbot.SPHSmartAccountBuilder;

import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.ObjectID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.Script;
import net.runelite.client.plugins.microbot.breakhandler.BreakHandlerScript;
import net.runelite.client.plugins.microbot.smelting.enums.Bars;
import net.runelite.client.plugins.microbot.util.antiban.Rs2Antiban;
import net.runelite.client.plugins.microbot.util.antiban.Rs2AntibanSettings;
import net.runelite.client.plugins.microbot.util.antiban.enums.Activity;
import net.runelite.client.plugins.microbot.util.bank.Rs2Bank;
import net.runelite.client.plugins.microbot.util.bank.enums.BankLocation;
import net.runelite.client.plugins.microbot.util.camera.Rs2Camera;
import net.runelite.client.plugins.microbot.util.equipment.Rs2Equipment;
import net.runelite.client.plugins.microbot.util.gameobject.Rs2GameObject;
import net.runelite.client.plugins.microbot.util.grandexchange.GrandExchangeAction;
import net.runelite.client.plugins.microbot.util.grandexchange.GrandExchangeRequest;
import net.runelite.client.plugins.microbot.util.grandexchange.Rs2GrandExchange;
import net.runelite.client.plugins.microbot.util.inventory.Rs2Inventory;
import net.runelite.client.plugins.microbot.util.item.Rs2ItemManager;
import net.runelite.client.plugins.microbot.util.keyboard.Rs2Keyboard;
import net.runelite.client.plugins.microbot.util.math.Rs2Random;
import net.runelite.client.plugins.microbot.util.misc.Rs2UiHelper;
import net.runelite.client.plugins.microbot.util.npc.Rs2Npc;
import net.runelite.client.plugins.microbot.util.npc.Rs2NpcModel;
import net.runelite.client.plugins.microbot.util.player.Rs2Player;
import net.runelite.client.plugins.microbot.util.walker.Rs2Walker;
import net.runelite.client.plugins.microbot.util.widget.Rs2Widget;
import net.runelite.http.api.worlds.World;

import java.awt.event.KeyEvent;
import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;


public class SPHAccountBuilderScript extends Script {

    public static boolean test = false;
    public volatile boolean shouldThink = true;
    public volatile long scriptStartTime = System.currentTimeMillis();
    private long howLongUntilThink = Rs2Random.between(8,40);
    private int totalGP = 0;
    private SPHAccountBuilderConfig config;
    
    // Initialization state tracking
    private boolean isInitialized = false;
    private int initializationTicks = 0;
    private static final int REQUIRED_INITIALIZATION_TICKS = 1;
    
    // Buying session tracking
    private boolean isBuyingSession = false;

    private boolean shouldWoodcut = false;
    private boolean shouldMine = false;
    private boolean shouldFish = false;
    private boolean shouldSmelt = false;
    private boolean shouldFiremake = false;
    private boolean shouldCook = false;
    private boolean shouldCraft = false;
    private boolean shouldFletching = false;
    private boolean shouldSellItems = false;

    private boolean weChangeActivity = false;

    private WorldPoint chosenSpot = null;
    
    // Public method to get current activity for overlay
    public String getCurrentActivity() {
        if (!isInitialized) return "Initializing...";
        if (shouldWoodcut) return "Woodcutting";
        if (shouldMine) return "Mining";
        if (shouldFish) return "Fishing";
        if (shouldSmelt) return "Smelting";
        if (shouldFiremake) return "Firemaking";
        if (shouldCook) return "Cooking";
        if (shouldCraft) return "Crafting";
        if (shouldFletching) return "Fletching";
        if (shouldSellItems) return "Selling Items";
        return "Thinking...";
    }
    
    // Public method to get time until next activity for overlay
    public String getTimeUntilNextActivity() {
        if (!isInitialized) return "Initializing...";
        
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - scriptStartTime;
        long targetTime = howLongUntilThink * 60 * 1000; // Convert minutes to milliseconds
        long remainingTime = targetTime - elapsedTime;
        
        if (remainingTime <= 0) {
            return "Changing now...";
        }
        
        // Convert back to minutes and seconds
        long remainingMinutes = remainingTime / (60 * 1000);
        long remainingSeconds = (remainingTime % (60 * 1000)) / 1000;
        
        return String.format("%dm %ds", remainingMinutes, remainingSeconds);
    }

    /**
     * Handle script initialization - ensures player is ready before starting activities
     * @return true if initialization is complete, false if we need to wait
     */
    private boolean handleInitialization() {
        // Only check if logged in
        if (!Microbot.isLoggedIn()) {
            return false;
        }
        
        // Initialize immediately and trigger activity selection
        isInitialized = true;
        shouldThink = true;
        return true;
    }

    /**
     * Auto-equip beneficial teleport items for members worlds only
     * Only equips: Ring of dueling, Combat bracelet, Amulet of glory
     */
    private void autoEquipBeneficialItems() {
        // Skip if we're in the middle of complex operations
        if (Rs2Bank.isOpen() || Rs2GrandExchange.isOpen()) return;
        if (Rs2Player.isMoving() || Rs2Player.isAnimating()) return;
        
        // Only equip teleport jewelry if in members world
        if (!Rs2Player.isInMemberWorld()) {
            return; // Skip entirely for F2P worlds
        }
        
        // Members-only teleport jewelry
        String[] membersItems = {
            "Ring of dueling",
            "Combat bracelet", 
            "Amulet of glory"
        };
        
        int itemsEquipped = 0;
        for (String itemName : membersItems) {
            // Check if we already have this type of item equipped
            if (itemName.contains("Ring of dueling")) {
                if (Rs2Equipment.get(EquipmentInventorySlot.RING) != null) continue;
            } else if (itemName.contains("Combat bracelet")) {
                if (Rs2Equipment.get(EquipmentInventorySlot.GLOVES) != null) continue;
            } else if (itemName.contains("Amulet of glory")) {
                if (Rs2Equipment.get(EquipmentInventorySlot.AMULET) != null) continue;
            }
            
            // Check if item is in inventory and equip it
            if (Rs2Inventory.hasItem(itemName)) {
                Microbot.log("Auto-equipping members teleport item: " + itemName);
                if (Rs2Inventory.wield(itemName)) {
                    sleepUntil(() -> Rs2Equipment.isWearing(itemName), Rs2Random.between(2000, 4000));
                    sleepHumanReaction();
                    itemsEquipped++;
                    
                    // Limit to equipping 1 item per cycle to avoid spam
                    if (itemsEquipped >= 1) break;
                }
            }
        }
    }

    /**
     * Withdraw full cash stack when preparing to buy items
     * This ensures we have all available coins for purchases
     */
    private void ensureCoinsInInventory() {
        if (!Rs2Bank.isOpen()) return;
        
        // If we already have coins in inventory, we're good
        if (Rs2Inventory.contains("Coins")) {
            return;
        }
        
        // Withdraw ALL coins from bank
        if (Rs2Bank.getBankItem("Coins") != null) {
            int totalCoins = Rs2Bank.getBankItem("Coins").getQuantity();
            Microbot.log("Withdrawing all " + totalCoins + " coins for purchases");
            Rs2Bank.withdrawAll("Coins");
            sleepUntil(() -> Rs2Inventory.contains("Coins"), Rs2Random.between(2000, 5000));
            sleepHumanReaction();
        } else {
            Microbot.log("ERROR: No coins in bank - cannot afford any purchases!");
            // Disable the plugin
            shouldThink = true;
            Microbot.showMessage("SPH Account Builder: No coins available - plugin stopping");
        }
    }
    
    /**
     * Check if we have enough total coins to afford required purchases
     * If not, disable the plugin
     */
    private boolean checkAffordability() {
        if (totalGP < 50000) { // Minimum 50k coins required
            Microbot.log("ERROR: Insufficient funds - need at least 50k coins, have " + totalGP);
            Microbot.showMessage("SPH Account Builder: Insufficient funds (" + totalGP + " coins) - plugin stopping");
            // Stop the plugin
            super.shutdown();
            return false;
        }
        return true;
    }



    public boolean run(SPHAccountBuilderConfig config) {
        this.config = config;
        // Initialize time with config values
        howLongUntilThink = Rs2Random.between(config.minTimeMinutes(), config.maxTimeMinutes());
        Microbot.enableAutoRunOn = false;
        mainScheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(() -> {
            try {
                if (!Microbot.isLoggedIn()) return;
                if (!super.run()) {
                    // Plugin has been disabled - cancel the scheduled task to stop execution
                    if (mainScheduledFuture != null) {
                        mainScheduledFuture.cancel(false);
                        Microbot.log("SPH Account Builder: Plugin disabled, stopping execution");
                    }
                    return;
                }
                if(BreakHandlerScript.breakIn != -1 && BreakHandlerScript.isBreakActive()) return;

                // Handle initialization first
                if (!isInitialized) {
                    if (!handleInitialization()) {
                        return; // Wait for next tick if not ready
                    }
                }

                long startTime = System.currentTimeMillis();

                thinkVoid(); // decide what we're going to do.

                thinkBasedOnTime(); // Change our activity if it's been X amount of time.

                // Auto-equip beneficial items first
                autoEquipBeneficialItems();

                // Buy all essential items on startup if needed
                if (weChangeActivity) {
                    buyAllEssentialItems();
                }

                handleBreaks();

                getBuyAndEquipP2PTeles();

                // Ensure we have required equipment before starting activities
                ensureRequiredEquipment();

                //Skilling
                woodCutting();
                mining();
                fishing();
                smelting();
                firemake();
                cook();
                craft();
                fletching();

                //Skilling

                sellItems();


                long endTime = System.currentTimeMillis();
                long totalTime = endTime - startTime;
                System.out.println("Total time for loop " + totalTime);

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
        return true;
    }

    public void handleBreaks(){
        if(BreakHandlerScript.breakIn != -1 && BreakHandlerScript.breakIn < 30) {
            if (Microbot.loggedIn) {
                Rs2Player.logout();
                sleepUntil(() -> !Microbot.loggedIn, Rs2Random.between(2000, 5000));
            }
            sleepUntil(() -> BreakHandlerScript.isBreakActive(), Rs2Random.between(30000, 60000));
        }
    }

    public void thinkVoid(){
        if(shouldThink){
            //set our booleans to false
            this.shouldWoodcut = false;
            this.shouldMine = false;
            this.shouldFish = false;
            this.shouldSmelt = false;
            this.shouldFiremake = false;
            this.shouldCook = false;
            this.shouldCraft = false;
            this.shouldFletching = false;

            this.shouldSellItems = false;

            this.chosenSpot = null;
            this.weChangeActivity = true;

            int random = Rs2Random.between(0,1000);
            
            if(Rs2Player.isInMemberWorld()) {
                // 9 activities total for P2P - each gets ~111 points
                if(random <= 111){
                    Microbot.log("We're going woodcutting.");
                    Rs2Antiban.antibanSetupTemplates.applyWoodcuttingSetup();
                    Rs2Antiban.setActivity(Activity.GENERAL_WOODCUTTING);
                    shouldWoodcut = true;
                    shouldThink = false;
                    return;
                }
                if(random > 111 && random <= 222){
                    Microbot.log("We're going mining.");
                    Rs2Antiban.antibanSetupTemplates.applyMiningSetup();
                    Rs2Antiban.setActivity(Activity.GENERAL_MINING);
                    shouldMine = true;
                    shouldThink = false;
                    return;
                }
                if(random > 222 && random <= 333){
                    Microbot.log("We're going fishing.");
                    Rs2Antiban.antibanSetupTemplates.applyFishingSetup();
                    Rs2Antiban.setActivity(Activity.GENERAL_FISHING);
                    shouldFish = true;
                    shouldThink = false;
                    return;
                }
                if(random > 333 && random <= 444){
                    Microbot.log("We're going smelting.");
                    Rs2Antiban.antibanSetupTemplates.applySmithingSetup();
                    Rs2Antiban.setActivity(Activity.SMELTING_BRONZE_BARS);
                    shouldSmelt = true;
                    shouldThink = false;
                    return;
                }
                if(random > 444 && random <= 555){
                    Microbot.log("We're going firemaking.");
                    Rs2Antiban.antibanSetupTemplates.applyFiremakingSetup();
                    Rs2Antiban.setActivity(Activity.GENERAL_FIREMAKING);
                    shouldFiremake = true;
                    shouldThink = false;
                    return;
                }
                if(random > 555 && random <= 666){
                    Microbot.log("We're going to cook.");
                    Rs2Antiban.antibanSetupTemplates.applyCookingSetup();
                    Rs2Antiban.setActivity(Activity.GENERAL_COOKING);
                    shouldCook = true;
                    shouldThink = false;
                    return;
                }
                if(random > 666 && random <= 777){
                    Microbot.log("We're going to craft.");
                    Rs2Antiban.antibanSetupTemplates.applyCraftingSetup();
                    Rs2Antiban.setActivity(Activity.GENERAL_CRAFTING);
                    shouldCraft = true;
                    shouldThink = false;
                    return;
                }
                if(random > 777 && random <= 888){
                    Microbot.log("We're going fletching.");
                    Rs2Antiban.antibanSetupTemplates.applyGeneralBasicSetup();
                    Rs2Antiban.setActivity(Activity.GENERAL_FLETCHING);
                    shouldFletching = true;
                    shouldThink = false;
                    return;
                }
                if(random > 888){
                    Microbot.log("We're going to sell what we have.");
                    Rs2Antiban.antibanSetupTemplates.applyGeneralBasicSetup();
                    shouldSellItems = true;
                    shouldThink = false;
                    return;
                }
            } else {
                // 8 activities for F2P - keep original 125-point ranges
                if(random <= 125){
                    Microbot.log("We're going woodcutting.");
                    Rs2Antiban.antibanSetupTemplates.applyWoodcuttingSetup();
                    Rs2Antiban.setActivity(Activity.GENERAL_WOODCUTTING);
                    shouldWoodcut = true;
                    shouldThink = false;
                    return;
                }
                if(random > 125 && random <= 250){
                    Microbot.log("We're going mining.");
                    Rs2Antiban.antibanSetupTemplates.applyMiningSetup();
                    Rs2Antiban.setActivity(Activity.GENERAL_MINING);
                    shouldMine = true;
                    shouldThink = false;
                    return;
                }
                if(random > 250 && random <= 375){
                    Microbot.log("We're going fishing.");
                    Rs2Antiban.antibanSetupTemplates.applyFishingSetup();
                    Rs2Antiban.setActivity(Activity.GENERAL_FISHING);
                    shouldFish = true;
                    shouldThink = false;
                    return;
                }
                if(random > 375 && random <= 500){
                    Microbot.log("We're going smelting.");
                    Rs2Antiban.antibanSetupTemplates.applySmithingSetup();
                    Rs2Antiban.setActivity(Activity.SMELTING_BRONZE_BARS);
                    shouldSmelt = true;
                    shouldThink = false;
                    return;
                }
                if(random > 500 && random <= 625){
                    Microbot.log("We're going firemaking.");
                    Rs2Antiban.antibanSetupTemplates.applyFiremakingSetup();
                    Rs2Antiban.setActivity(Activity.GENERAL_FIREMAKING);
                    shouldFiremake = true;
                    shouldThink = false;
                    return;
                }
                if(random > 625 && random <= 750){
                    Microbot.log("We're going to cook.");
                    Rs2Antiban.antibanSetupTemplates.applyCookingSetup();
                    Rs2Antiban.setActivity(Activity.GENERAL_COOKING);
                    shouldCook = true;
                    shouldThink = false;
                    return;
                }
                if(random > 750 && random <= 875){
                    Microbot.log("We're going to craft.");
                    Rs2Antiban.antibanSetupTemplates.applyCraftingSetup();
                    Rs2Antiban.setActivity(Activity.GENERAL_CRAFTING);
                    shouldCraft = true;
                    shouldThink = false;
                    return;
                }
                if(random > 875){
                    Microbot.log("We're going to sell what we have.");
                    Rs2Antiban.antibanSetupTemplates.applyGeneralBasicSetup();
                    shouldSellItems = true;
                    shouldThink = false;
                    return;
                }
            }

        }
    }

    public void closeTheBank(){
        if(Rs2Bank.isOpen()){
            Rs2Bank.closeBank();
            sleepUntil(()-> !Rs2Bank.isOpen(), Rs2Random.between(2000,5000));
            sleepHumanReaction();
        }
    }

    public void getBuyAndEquipP2PTeles(){
        if(Rs2Player.isInMemberWorld()){
            if(!Rs2Equipment.isWearing(it->it!=null&&it.getName().contains("Amulet of glory("))
                    || !Rs2Equipment.isWearing(it->it!=null&&it.getName().contains("Ring of wealth ("))
                        || !Rs2Equipment.isWearing(it->it!=null&&it.getName().contains("Combat bracelet("))){
                Microbot.log("We need our teleports");

                this.weChangeActivity = true;

                if(!Rs2Equipment.isWearing(it->it!=null&&it.getName().contains("Amulet of glory("))){
                    goToBankandGrabAnItem("Amulet of glory(6)", 1);
                }

                if(!Rs2Equipment.isWearing(it->it!=null&&it.getName().contains("Ring of dueling("))){
                    goToBankandGrabAnItem("Ring of dueling(8)", 1);
                }

                if(!Rs2Equipment.isWearing(it->it!=null&&it.getName().contains("Combat bracelet("))){
                    goToBankandGrabAnItem("Combat bracelet(6)", 1);
                }

                if(Rs2GrandExchange.isOpen()){
                    Rs2GrandExchange.closeExchange();
                    sleepUntil(()-> !Rs2GrandExchange.isOpen(), Rs2Random.between(2000,5000));
                    sleepHumanReaction();
                }

                if(Rs2Bank.isOpen()){
                    Rs2Bank.closeBank();
                    sleepUntil(()-> !Rs2Bank.isOpen(), Rs2Random.between(2000,5000));
                    sleepHumanReaction();
                }

                if(Rs2Inventory.contains(it->it!=null&&it.getName().contains("Amulet of glory(")) && !Rs2Equipment.isWearing(it->it!=null&&it.getName().contains("Amulet of glory("))){
                    if(Rs2Inventory.interact("Amulet of glory(6)", "Wear")){
                        sleepUntil(()-> Rs2Equipment.isWearing(it->it!=null&&it.getName().equals("Amulet of glory(6)")), Rs2Random.between(2000,5000));
                        sleepHumanReaction();
                    }
                }

                if(Rs2Inventory.contains(it->it!=null&&it.getName().contains("Ring of dueling(")) && !Rs2Equipment.isWearing(it->it!=null&&it.getName().contains("Ring of dueling("))){
                    if(Rs2Inventory.interact("Ring of dueling(8)", "Wear")){
                        sleepUntil(()-> Rs2Equipment.isWearing(it->it!=null&&it.getName().equals("Ring of dueling(8)")), Rs2Random.between(2000,5000));
                        sleepHumanReaction();
                    }
                }

                if(Rs2Inventory.contains(it->it!=null&&it.getName().contains("Combat bracelet(")) && !Rs2Equipment.isWearing(it->it!=null&&it.getName().contains("Combat bracelet("))){
                    if(Rs2Inventory.interact("Combat bracelet(6)", "Wear")){
                        sleepUntil(()-> Rs2Equipment.isWearing(it->it!=null&&it.getName().equals("Combat bracelet(6)")), Rs2Random.between(2000,5000));
                        sleepHumanReaction();
                    }
                }

            }
        }
    }

    public void thinkBasedOnTime(){
            long currentTime = System.currentTimeMillis();
            if (currentTime - scriptStartTime >= howLongUntilThink * 60 * 1000) {
                Microbot.log("Changing activity it's been "+howLongUntilThink+" minutes");

                shouldThink = true;

                scriptStartTime = currentTime;

                howLongUntilThink = Rs2Random.between(config.minTimeMinutes(), config.maxTimeMinutes());

                Microbot.log("We'll change activity again in "+howLongUntilThink+" minutes");
            }
    }

    private void buyAllEssentialItems() {
        List<String> essentialItems = getEssentialItemsList();
        List<String> itemsToBuy = new ArrayList<>();
        
        // SMART CHECK: Only buy items that are actually missing from bank and inventory
        // First ensure bank is open for accurate checking
        if (!Rs2Bank.isOpen()) {
            if (!Rs2Bank.walkToBank()) {
                Microbot.log("Failed to walk to bank for item checking");
                return;
            }
            if (!Rs2Bank.openBank()) {
                Microbot.log("Failed to open bank for item checking");
                return;
            }
            sleepUntil(() -> Rs2Bank.isOpen(), 5000);
        }
        
        for (String item : essentialItems) {
            if (!hasItemInBankOrInventorySmartCheck(item)) {
                itemsToBuy.add(item);
            }
        }
        
        if (!itemsToBuy.isEmpty()) {
            Microbot.log("Smart check complete - Need to buy " + itemsToBuy.size() + " essential items: " + String.join(", ", itemsToBuy));
            
            // Calculate total cost
            int totalCost = 0;
            Rs2ItemManager itemManager = new Rs2ItemManager();
            
            for (String item : itemsToBuy) {
                int itemPrice = itemManager.getGEPrice(itemManager.getItemId(item));
                int quantity = getItemQuantity(item);
                totalCost += (int)(itemPrice * quantity * 1.30); // 30% markup
            }
            
            if (totalCost > totalGP) {
                Microbot.log("Not enough GP for all items. Need: " + totalCost + ", Have: " + totalGP);
                return;
            }
            
            // Buy all items that are actually needed
            for (String item : itemsToBuy) {
                int quantity = getItemQuantity(item);
                buyItemBulk(item, quantity);
            }
        } else {
            Microbot.log("Smart check complete - All essential items already available in bank/inventory");
        }
    }
    
    private List<String> getEssentialItemsList() {
        List<String> items = new ArrayList<>();
        
        // Basic tools
        items.add("Iron axe");
        items.add("Steel axe");
        items.add("Mithril axe");
        items.add("Adamant axe");
        items.add("Rune axe");
        
        items.add("Iron pickaxe");
        items.add("Mithril pickaxe");
        items.add("Adamant pickaxe");
        items.add("Rune pickaxe");
        
        items.add("Small fishing net");
        items.add("Fly fishing rod");
        items.add("Feather");
        
        items.add("Tinderbox");
        items.add("Knife");
        
        // Crafting supplies
        items.add("Thread");
        items.add("Needle");
        items.add("Necklace mould");
        
        // If P2P, add specific teleports
        if (Rs2Player.isInMemberWorld()) {
            items.add("Amulet of glory(6)");
            items.add("Ring of dueling(8)");
            items.add("Combat bracelet(6)");
        }
        
        return items;
    }
    
    private boolean hasItemInBankOrInventory(String itemName) {
        // Check inventory first
        if (Rs2Inventory.contains(itemName)) return true;
        if (Rs2Equipment.isWearing(it -> it != null && it.getName().equals(itemName))) return true;
        
        // Check bank
        if (Rs2Bank.getBankItem(itemName, true) != null) {
            int quantity = Rs2Bank.getBankItem(itemName, true).getQuantity();
            int requiredQuantity = getItemQuantity(itemName);
            return quantity >= requiredQuantity;
        }
        
        return false;
    }
    
    /**
     * Enhanced version with detailed logging for smart gear checking
     */
    private boolean hasItemInBankOrInventorySmartCheck(String itemName) {
        int requiredQuantity = getItemQuantity(itemName);
        
        // Check inventory first - with error handling
        try {
            if (Rs2Inventory.contains(itemName)) {
                int invQuantity = Rs2Inventory.count(itemName);
                if (invQuantity >= requiredQuantity) {
                    Microbot.log("Smart check: " + itemName + " - Found " + invQuantity + " in inventory (need " + requiredQuantity + ")");
                    return true;
                }
            }
        } catch (Exception e) {
            Microbot.log("Warning: Inventory check failed for " + itemName + ": " + e.getMessage());
        }
        
        // Check equipped items - comprehensive check
        try {
            if (Rs2Equipment.isWearing(it -> it != null && it.getName().equals(itemName))) {
                Microbot.log("Smart check: " + itemName + " - Currently equipped");
                return true;
            }
            // Also check by partial name for items with charges (e.g., "Ring of dueling(8)")
            if (Rs2Equipment.isWearing(it -> it != null && it.getName().contains(itemName.split("\\(")[0]))) {
                Microbot.log("Smart check: " + itemName + " - Similar item equipped");
                return true;
            }
        } catch (Exception e) {
            Microbot.log("Warning: Equipment check failed for " + itemName + ": " + e.getMessage());
        }
        
        // Check bank with detailed logging
        if (hasItemInBank(itemName, requiredQuantity)) {
            return true;
        }
        
        Microbot.log("Smart check: " + itemName + " - Not available (need " + requiredQuantity + "), will purchase");
        return false;
    }
    
    private int getItemQuantity(String itemName) {
        // Define quantities for different items
        switch (itemName) {
            case "Feather":
                return 1000;
            case "Thread":
                return 200;
            case "Tinderbox":
            case "Knife":
            case "Needle":
            case "Necklace mould":
            case "Small fishing net":
            case "Fly fishing rod":
            case "Amulet of glory(6)":
            case "Ring of dueling(8)":
            case "Combat bracelet(6)":
                return 1;
            default:
                // Tools (axes, pickaxes)
                return 1;
        }
    }
    
    /**
     * Robust GE buying method with retry logic to handle item clicking and price adjustment issues
     */
    private boolean buyItemWithRetry(String itemName, int quantity, int markupPercent) {
        final int MAX_RETRIES = 3;
        
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            Microbot.log("Attempting to buy " + quantity + "x " + itemName + " (attempt " + attempt + "/" + MAX_RETRIES + ")");
            
            try {
                // Ensure GE is open
                if (!Rs2GrandExchange.isOpen()) {
                    if (!Rs2GrandExchange.openExchange()) {
                        Microbot.log("Failed to open Grand Exchange on attempt " + attempt);
                        if (attempt < MAX_RETRIES) {
                            sleep(2000);
                            continue;
                        }
                        return false;
                    }
                    sleepUntil(() -> Rs2GrandExchange.isOpen(), 5000);
                }
                
                // Check for available GE slots
                if (Rs2GrandExchange.getAvailableSlots().length == 0) {
                    Microbot.log("No available GE slots");
                    return false;
                }
                
                // Create buy request with proper settings
                GrandExchangeRequest buyRequest = GrandExchangeRequest.builder()
                        .itemName(itemName)
                        .exact(true)
                        .action(GrandExchangeAction.BUY)
                        .percent(markupPercent) // Use +5% button clicking for reliable price adjustment
                        .quantity(quantity)
                        .build();
                
                // Attempt the purchase
                boolean success = Rs2GrandExchange.processOffer(buyRequest);
                
                if (success) {
                    Microbot.log("Successfully placed buy order for " + quantity + "x " + itemName + " at " + markupPercent + "% markup");
                    
                    // Wait for offer to complete with reasonable timeout
                    int timeout = quantity > 100 ? 60000 : 30000;
                    boolean completed = sleepUntil(() -> Rs2GrandExchange.hasFinishedBuyingOffers(), timeout);
                    
                    if (completed) {
                        Rs2GrandExchange.collectAllToInventory();
                        sleepUntil(() -> Rs2Inventory.contains(itemName), 5000);
                        Microbot.log("Successfully purchased and collected " + itemName);
                        return true;
                    } else {
                        Microbot.log("Buy order placed but not completed within timeout - may still be processing");
                        return true; // Order was placed successfully even if not completed yet
                    }
                } else {
                    Microbot.log("Failed to place buy order for " + itemName + " on attempt " + attempt);
                    
                    if (attempt < MAX_RETRIES) {
                        // Add delay before retry and try to clear any stuck states
                        sleep(3000);
                        
                        // Try to close and reopen GE to reset state
                        if (Rs2GrandExchange.isOpen()) {
                            Rs2GrandExchange.closeExchange();
                            sleepUntil(() -> !Rs2GrandExchange.isOpen(), 3000);
                        }
                    }
                }
                
            } catch (Exception e) {
                Microbot.log("Exception during GE purchase attempt " + attempt + ": " + e.getMessage());
                if (attempt < MAX_RETRIES) {
                    sleep(2000);
                } else {
                    Microbot.log("All retry attempts failed for " + itemName);
                    return false;
                }
            }
        }
        
        return false;
    }
    
    private void buyItemBulk(String itemName, int quantity) {
        // Check affordability first
        Rs2ItemManager itemManager = new Rs2ItemManager();
        int itemsID = itemManager.getItemId(itemName);
        if (itemName.equals("Leather")) itemsID = ItemID.LEATHER;
        
        int itemsPrice = itemManager.getGEPrice(itemsID);
        int totalCost = (int)(itemsPrice * quantity * 1.30);
        
        if (totalCost > totalGP) {
            Microbot.log("Can't afford " + itemName + ": " + totalCost + " > " + totalGP);
            return;
        }
        
        // Use the robust buying method with 30% markup and retry logic
        boolean success = buyItemWithRetry(itemName, quantity, 30);
        
        if (!success) {
            Microbot.log("ERROR: Failed to purchase " + itemName + " after all retry attempts");
        }
    }

    private void ensureRequiredEquipment() {
        if (!Rs2Bank.isOpen()) {
            walkToBankAndOpenIt();
            if (!Rs2Bank.isOpen()) return;
        }
        
        String requiredTool = null;
        
        if (shouldWoodcut) {
            requiredTool = getWoodcuttingAxe();
        } else if (shouldMine) {
            requiredTool = getMiningPickaxe();
        } else if (shouldFish) {
            requiredTool = getFishingGear();
        } else if (shouldFiremake && !Rs2Inventory.contains(ItemID.TINDERBOX) && !Rs2Equipment.isWearing(ItemID.TINDERBOX)) {
            requiredTool = "Tinderbox";
        } else if (shouldFletching && !Rs2Inventory.contains("Knife")) {
            requiredTool = "Knife";
        } else if (shouldCraft) {
            int craftingLvl = Rs2Player.getRealSkillLevel(Skill.CRAFTING);
            if (craftingLvl < 22 && !Rs2Inventory.contains("Needle")) {
                requiredTool = "Needle";
            } else if (craftingLvl >= 22 && !Rs2Inventory.contains("Necklace mould")) {
                requiredTool = "Necklace mould";
            }
        }
        
        if (requiredTool != null) {
            // Make requiredTool effectively final for lambda expressions
            final String finalRequiredTool = requiredTool;
            
            // Check if we have the tool in inventory or equipped
            boolean hasTool = Rs2Inventory.contains(finalRequiredTool) || 
                            Rs2Equipment.isWearing(it -> it != null && it.getName().equals(finalRequiredTool));
            
            if (!hasTool) {
                // SMART CHECK: First check if we have it in bank before buying
                if (hasItemInBank(finalRequiredTool, 1)) {
                    Microbot.log("Found " + finalRequiredTool + " in bank, withdrawing instead of buying");
                    Rs2Bank.withdrawOne(finalRequiredTool, true);
                    sleepUntil(() -> Rs2Inventory.contains(finalRequiredTool), Rs2Random.between(2000, 5000));
                    sleepHumanReaction();
                } else {
                    // Need to buy the tool - it's not in bank
                    Microbot.log("Tool " + finalRequiredTool + " not found in bank, purchasing from GE");
                    goToBankandGrabAnItem(finalRequiredTool, 1);
                }
            }
        }
        
        // Special handling for fishing feathers with smart checking
        if (shouldFish && getFishingGear().equals("Fly fishing rod")) {
            if (!Rs2Inventory.contains("Feather") || Rs2Inventory.count("Feather") < 100) {
                if (hasItemInBank("Feather", 100)) {
                    Microbot.log("Found sufficient feathers in bank, withdrawing instead of buying");
                    Rs2Bank.withdrawX("Feather", 1000);
                    sleepUntil(() -> Rs2Inventory.contains("Feather"), Rs2Random.between(2000, 5000));
                    sleepHumanReaction();
                } else {
                    Microbot.log("Insufficient feathers in bank, purchasing from GE");
                    goToBankandGrabAnItem("Feather", 1000);
                }
            }
        }
    }
    
    /**
     * Smart helper method to check if we have an item in bank with sufficient quantity
     * @param itemName The name of the item to check
     * @param requiredQuantity The minimum quantity needed
     * @return true if we have enough in bank, false otherwise
     */
    private boolean hasItemInBank(String itemName, int requiredQuantity) {
        if (!Rs2Bank.isOpen()) {
            Microbot.log("Bank check failed: Bank not open for " + itemName);
            return false;
        }
        
        try {
            // First try exact match
            var bankItem = Rs2Bank.getBankItem(itemName, true);
            if (bankItem != null) {
                int availableQuantity = bankItem.getQuantity();
                Microbot.log("Bank check: " + itemName + " - Available: " + availableQuantity + ", Required: " + requiredQuantity);
                return availableQuantity >= requiredQuantity;
            }
            
            // Try partial match for items with charges
            String baseItemName = itemName.split("\\(")[0];
            if (!baseItemName.equals(itemName)) {
                bankItem = Rs2Bank.getBankItem(baseItemName, false);
                if (bankItem != null) {
                    int availableQuantity = bankItem.getQuantity();
                    Microbot.log("Bank check: " + itemName + " (partial match) - Available: " + availableQuantity + ", Required: " + requiredQuantity);
                    return availableQuantity >= requiredQuantity;
                }
            }
        } catch (Exception e) {
            Microbot.log("ERROR: Bank check exception for " + itemName + ": " + e.getMessage());
            return false;
        }
        
        Microbot.log("Bank check: " + itemName + " - Not found in bank");
        return false;
    }

    private boolean needsToBuyItems() {
        // SMART CHECK: Use enhanced checking for current activity requirements
        if (shouldWoodcut) {
            String axeToUse = getWoodcuttingAxe();
            if (!Rs2Inventory.contains(axeToUse) && !Rs2Equipment.isWearing(it -> it != null && it.getName().equals(axeToUse))) {
                if (!hasItemInBank(axeToUse, 1)) return true;
            }
        }
        
        if (shouldMine) {
            String pickaxeToUse = getMiningPickaxe();
            if (!Rs2Inventory.contains(pickaxeToUse) && !Rs2Equipment.isWearing(it -> it != null && it.getName().equals(pickaxeToUse))) {
                if (!hasItemInBank(pickaxeToUse, 1)) return true;
            }
        }
        
        if (shouldFish) {
            String fishingGear = getFishingGear();
            if (!Rs2Inventory.contains(it -> it != null && it.getName().contains(fishingGear))) {
                if (!hasItemInBank(fishingGear, 1)) return true;
            }
            if (fishingGear.equals("Fly fishing rod") && !Rs2Inventory.contains("Feather")) {
                if (!hasItemInBank("Feather", 100)) return true;
            }
        }
        
        if (shouldFiremake && !Rs2Inventory.contains(ItemID.TINDERBOX)) {
            if (!hasItemInBank("Tinderbox", 1)) return true;
        }
        
        if (shouldCraft) {
            // Check crafting materials with smart checking
            int craftingLvl = Rs2Player.getRealSkillLevel(Skill.CRAFTING);
            if (craftingLvl < 22) {
                if (!hasItemInBank("Thread", 10)) return true;
                if (!hasItemInBank("Needle", 1)) return true;
            } else {
                if (!hasItemInBank("Necklace mould", 1)) return true;
            }
        }
        
        if (shouldFletching) {
            if (!hasItemInBank("Knife", 1)) return true;
        }
        
        return false;
    }
    
    private String getWoodcuttingAxe() {
        int wcLvl = Rs2Player.getRealSkillLevel(Skill.WOODCUTTING);
        if (wcLvl < 15) return "Iron axe";
        if (wcLvl < 30) return "Steel axe";
        if (wcLvl == 30) return "Mithril axe";
        if (wcLvl < 41) return "Adamant axe";
        return "Rune axe";
    }
    
    private String getMiningPickaxe() {
        int miningLvl = Rs2Player.getRealSkillLevel(Skill.MINING);
        if (miningLvl < 21) return "Iron pickaxe";
        if (miningLvl < 31) return "Mithril pickaxe";
        if (miningLvl < 41) return "Adamant pickaxe";
        return "Rune pickaxe";
    }
    
    private String getFishingGear() {
        int fishingLvl = Rs2Player.getRealSkillLevel(Skill.FISHING);
        if (fishingLvl < 21) return "Small fishing net";
        return "Fly fishing rod";
    }

    public void depositAllIfWeChangeActivity(){
        if(weChangeActivity){
            if(Rs2Bank.isOpen()) {
                if (!Rs2Inventory.isEmpty()) {
                    BreakHandlerScript.lockState.set(true);
                    while (!Rs2Inventory.isEmpty()) {
                        if (!super.isRunning()) {
                            break;
                        }
                        if (BreakHandlerScript.breakIn != -1 && BreakHandlerScript.breakIn < 30 || BreakHandlerScript.isBreakActive()) {
                            Rs2Bank.closeBank();
                            Microbot.log("We're going on break");
                            break;
                        }
                        // Only deposit if we're not in a buying session (prevents depositing coins just to take them back out)
                        if (!isBuyingSession) {
                            Rs2Bank.depositAll();
                            sleepUntil(() -> Rs2Inventory.isEmpty(), Rs2Random.between(2000, 5000));
                            sleepHumanReaction();
                        } else {
                            // During buying session, deposit everything except coins
                            Rs2Bank.depositAllExcept("Coins");
                            sleepUntil(() -> Rs2Inventory.onlyContains("Coins"), Rs2Random.between(2000, 5000));
                            sleepHumanReaction();
                        }
                    }
                    BreakHandlerScript.lockState.set(false);
                }
                if(Rs2Inventory.isEmpty() || (isBuyingSession && Rs2Inventory.onlyContains("Coins"))){
                    weChangeActivity = false;
                }
            }
        }
    }

    public void goToBankandGrabAnItem(String item, int howMany){
        if(!Rs2Bank.isOpen()){
            this.walkToBankAndOpenIt();
            sleepUntil(()-> Rs2Bank.isOpen(), Rs2Random.between(2000,5000));
            sleepHumanReaction();
        }
        if(Rs2Bank.isOpen()){
            chosenSpot = null;

            if(Rs2Bank.getBankItem("Coins") != null){totalGP = Rs2Bank.getBankItem("Coins").getQuantity();}

            // Check affordability first - stop plugin if insufficient funds
            if (!checkAffordability()) {
                return; // Plugin will be stopped
            }

            // Start buying session to prevent depositing coins
            isBuyingSession = true;

            depositAllIfWeChangeActivity();

            // Ensure we have coins in inventory for purchases
            ensureCoinsInInventory();

            if(Rs2Bank.getBankItem(item, true) != null && Rs2Bank.getBankItem(item, true).getQuantity() >= howMany){
                if(!Rs2Inventory.contains(item)){
                    Rs2Bank.withdrawX(item, howMany, true);
                    sleepUntil(() -> Rs2Inventory.contains(item), Rs2Random.between(2000, 5000));
                    sleepHumanReaction();
                }
            } else {
                Rs2ItemManager itemManager = new Rs2ItemManager();
                int itemsID = itemManager.getItemId(item); // Get our Items ID
                if(item.equals("Leather")){itemsID = ItemID.LEATHER;} //Needed because getItemID returns the wrong itemID for Leather
                int itemsPrice = itemManager.getGEPrice(itemsID); // Get our items price
                int totalCost = itemsPrice * howMany; // Get how much it'll cost all together
                double getTwentyPercent = totalCost * 0.30; // Get 20 percent of the total cost
                double addTwentyPercent = getTwentyPercent + totalCost; // add the 20% we calculated to the total cost
                totalCost = (int) addTwentyPercent; // convert it back to an int

                Microbot.log("This will cost "+totalCost+" and we have "+totalGP);

                if(totalCost > totalGP){
                    Microbot.log("We don't have enough GP :( re-rolling");
                    this.shouldThink = true;
                    return;
                }

                if(!Rs2Inventory.contains(item) && totalCost < totalGP){
                    openGEandBuyItem(item, howMany);
                }
            }
            
            // End buying session - allow full deposits now
            isBuyingSession = false;
            
            // Now deposit everything including coins after all buying is complete
            if (Rs2Bank.isOpen() && Rs2Inventory.contains("Coins") && weChangeActivity) {
                Rs2Bank.depositAll();
                sleepUntil(() -> Rs2Inventory.isEmpty(), Rs2Random.between(2000, 5000));
                sleepHumanReaction();
            }
        }
    }

    public void walkToBankAndOpenIt() {
        if (!Rs2Bank.isOpen()) {

            if (Rs2Bank.walkToBank()) {
                Microbot.log("Walking to the bank");
            }
            BreakHandlerScript.lockState.set(true);
            while(!Rs2Bank.isOpen()) {
                if(!super.isRunning()){break;}
                if (BreakHandlerScript.breakIn != -1 && BreakHandlerScript.breakIn < 30 || BreakHandlerScript.isBreakActive()) {
                    Microbot.log("We're going on break");
                    break;
                }
                if (Rs2Bank.openBank()) {
                    Microbot.log("We opened the bank");
                    sleepUntil(()-> Rs2Bank.isOpen(), Rs2Random.between(10000,15000));
                    sleepHumanReaction();
                }
            }
            BreakHandlerScript.lockState.set(false);
            if (Rs2Bank.isOpen()) {
                if (Rs2Bank.getBankItem("Coins") != null) {
                    totalGP = Rs2Bank.getBankItem("Coins").getQuantity();
                }
                // Check affordability first - stop plugin if insufficient funds  
                if (!checkAffordability()) {
                    return; // Plugin will be stopped
                }
                // Ensure we have coins for upcoming purchases
                ensureCoinsInInventory();
            }
        }
    }

    public void openGEandBuyItem(String item, int howMany){
        closeTheBank();

        // Walk to GE if needed
        if(Rs2Player.getWorldLocation().distanceTo(BankLocation.GRAND_EXCHANGE.getWorldPoint()) > 7){
            Rs2Walker.walkTo(BankLocation.GRAND_EXCHANGE.getWorldPoint());
        }
        
        // Set activity state
        chosenSpot = null;
        weChangeActivity = true;

        // Check affordability first
        Rs2ItemManager itemManager = new Rs2ItemManager();
        int itemsID = itemManager.getItemId(item);
        if(item.equals("Leather")){itemsID = ItemID.LEATHER;} //Needed because getItemID returns the wrong itemID for Leather
        int itemsPrice = itemManager.getGEPrice(itemsID);
        int totalCost = (int)(itemsPrice * howMany * 1.30); // 30% markup

        Microbot.log("This will cost "+totalCost+" and we have "+totalGP);

        if(totalCost > totalGP){
            Microbot.log("We don't have enough GP :( re-rolling");
            this.shouldThink = true;
            return;
        }

        // Use the robust buying method with retry logic
        boolean success = buyItemWithRetry(item, howMany, 30);
        
        if (!success) {
            Microbot.log("ERROR: Failed to purchase " + item + " after all retry attempts, re-rolling activity");
            this.shouldThink = true;
        }
    }

    public void sellItems(){
        if(shouldSellItems){
            String[] items = {"Bronze bar", "Silver bar", "Diamond necklace", "Sapphire necklace", "Emerald necklace", "Ruby necklace", "Cooked chicken", "Leather", "Sapphire",
            "Emerald", "Ruby", "Diamond", "Raw chicken", "Bronze arrow"};

            if(!Rs2Bank.isOpen()){
                walkToBankAndOpenIt();
            }

            if(Rs2Bank.isOpen()){
                depositAllIfWeChangeActivity();

                Rs2Bank.setWithdrawAsNote();

                for (String item : items) {
                    if(Rs2Bank.getBankItem(item) != null){
                        Rs2Bank.withdrawAll(item);
                        sleepUntil(()-> Rs2Inventory.contains(item), Rs2Random.between(2000,5000));
                        sleepHumanReaction();
                    }
                }

            }

            closeTheBank();

            if(!Rs2Inventory.isEmpty()) {

                if(Rs2Player.getWorldLocation().distanceTo(BankLocation.GRAND_EXCHANGE.getWorldPoint()) > 12){
                    Rs2Walker.walkTo(BankLocation.GRAND_EXCHANGE.getWorldPoint());
                }

                if(Rs2GrandExchange.openExchange()){
                    sleepUntil(() -> Rs2GrandExchange.isOpen(), Rs2Random.between(5000, 15000));
                    sleepHumanReaction();
                }

                if (Rs2GrandExchange.isOpen()) {
                    for (String item : items) {
                        if (Rs2Inventory.get(item) != null) {
                            Rs2GrandExchange.sellItem(item, Rs2Inventory.get(item).getQuantity(), 1);
                            sleepUntil(() -> Rs2GrandExchange.hasFinishedSellingOffers(), Rs2Random.between(20000,60000));
                            sleepHumanReaction();
                        }
                    }
                    if (Rs2GrandExchange.hasFinishedBuyingOffers() || Rs2GrandExchange.hasFinishedSellingOffers()) {
                        Rs2GrandExchange.collectAllToBank();
                    }
                }
            }

            shouldThink = true;
        }
    }

    //skilling

    public void craft(){
        if(shouldCraft){
            String craftingMaterial = "Unknown";
            String craftingProduct = "Unknown";
            String mould = "Unknown";
            String gem = "Unknown";
            String bar = "Unknown";

            int craftingLvl = Rs2Player.getRealSkillLevel(Skill.CRAFTING);
            if(craftingLvl < 7){craftingMaterial = "Leather"; craftingProduct = "Leather gloves";}
            if(craftingLvl >= 7 && craftingLvl < 9){craftingMaterial = "Leather"; craftingProduct = "Leather boots";}
            if(craftingLvl >= 9 && craftingLvl < 11){craftingMaterial = "Leather"; craftingProduct = "Leather cowl";}
            if(craftingLvl >= 11 && craftingLvl < 14){craftingMaterial = "Leather"; craftingProduct = "Leather vambraces";}
            if(craftingLvl >= 14 && craftingLvl < 18){craftingMaterial = "Leather"; craftingProduct = "Leather body";}
            if(craftingLvl >= 18 && craftingLvl < 22){craftingMaterial = "Leather"; craftingProduct = "Leather chaps";}
            if(craftingLvl >= 22 && craftingLvl < 29){mould = "Necklace mould"; gem = "Sapphire"; bar = "Gold bar"; craftingProduct ="Sapphire necklace";}
            if(craftingLvl >= 29 && craftingLvl < 40){mould = "Necklace mould"; gem = "Emerald"; bar = "Gold bar"; craftingProduct ="Emerald necklace";}
            if(craftingLvl >= 40 && craftingLvl < 56){mould = "Necklace mould"; gem = "Ruby"; bar = "Gold bar"; craftingProduct ="Ruby necklace";}
            if(craftingLvl >= 56){mould = "Necklace mould"; gem = "Diamond"; bar = "Gold bar"; craftingProduct ="Diamond necklace";}




            if(bar.equals("Gold bar")) {
                chosenSpot = new WorldPoint(3106, 3498, 0); // edgeville smelter
            }

            if(craftingMaterial.equals("Leather")) {
                chosenSpot = BankLocation.GRAND_EXCHANGE.getWorldPoint();
                // SMART PREREQUISITE CHECK: Since we're going to GE anyway, we can buy materials there
                Microbot.log("LEATHER CRAFTING: Proceeding to GE (destination) - will purchase missing materials there if needed");
            }


            if(chosenSpot != null){
                if(Rs2Player.getWorldLocation().distanceTo(chosenSpot) > 12){
                    Rs2Walker.walkTo(chosenSpot);
                } else {
                    if(bar.equals("Gold bar")) {
                        if(!Rs2Inventory.contains(mould) || !Rs2Inventory.contains(gem) || !Rs2Inventory.contains(bar) || Rs2Inventory.contains(craftingProduct) || Rs2Inventory.contains(it -> it != null && it.isNoted()) || weChangeActivity){
                            walkToBankAndOpenIt();

                            if(weChangeActivity || Rs2Inventory.contains(it -> it != null && it.isNoted())){
                                Rs2Bank.depositAll();
                                sleepUntil(() -> Rs2Inventory.isEmpty(), Rs2Random.between(2000, 5000));
                                sleepHumanReaction();
                                if (Rs2Inventory.isEmpty()) {
                                    weChangeActivity = false;
                                }
                            }
                            if(Rs2Inventory.contains(craftingProduct) || Rs2Inventory.isFull()){
                                int random = Rs2Random.between(0,100);
                                if(random <= 75){
                                    Rs2Bank.depositAll(craftingProduct);
                                    String finalCraftingProduct = craftingProduct;
                                    sleepUntil(() -> !Rs2Inventory.contains(finalCraftingProduct), Rs2Random.between(2000, 5000));
                                    sleepHumanReaction();
                                } else {
                                    Rs2Bank.depositAll();
                                    sleepUntil(() -> Rs2Inventory.isEmpty(), Rs2Random.between(2000, 5000));
                                    sleepHumanReaction();
                                }

                            }

                            if(Rs2Bank.getBankItem(mould) == null){
                                goToBankandGrabAnItem(mould, 1);
                            }

                            int amt = Rs2Random.between(100,200);
                            if(Rs2Bank.getBankItem(gem, true) == null || Rs2Bank.getBankItem(bar, true) == null || Rs2Bank.getBankItem(gem, true).getQuantity() < 13 ||  Rs2Bank.getBankItem(bar, true).getQuantity() < 13){
                                goToBankandGrabAnItem(gem, amt);
                                goToBankandGrabAnItem(bar, amt);
                                return;
                            }
                            BreakHandlerScript.lockState.set(true);
                            while(Rs2Inventory.count(mould) < 1 || Rs2Inventory.count(gem) < 13 || Rs2Inventory.count(bar) < 13){
                                if(!super.isRunning()){break;}

                                if (BreakHandlerScript.breakIn != -1 && BreakHandlerScript.breakIn < 30 || BreakHandlerScript.isBreakActive()) {
                                    Rs2Bank.closeBank();
                                    Microbot.log("We're going on break");
                                    break;
                                }

                                if(Rs2Inventory.isFull()){Rs2Bank.depositAll();}

                                if(!Rs2Inventory.contains(mould) && Rs2Random.between(0,100) < 60){
                                    Rs2Bank.withdrawOne(mould);
                                    String finalMould = mould;
                                    sleepUntil(() -> Rs2Inventory.contains(finalMould), Rs2Random.between(2000, 5000));
                                    sleepHumanReaction();
                                }
                                if(Rs2Inventory.count(gem) < 13 && Rs2Random.between(0,100) < 60){
                                    Rs2Bank.withdrawX(gem, 13);
                                    String finalGem = gem;
                                    sleepUntil(() -> Rs2Inventory.contains(finalGem), Rs2Random.between(2000, 5000));
                                    sleepHumanReaction();
                                }
                                if(Rs2Inventory.count(bar) < 13 && Rs2Random.between(0,100) < 60){
                                    Rs2Bank.withdrawX(bar, 13);
                                    String finalBar = bar;
                                    sleepUntil(() -> Rs2Inventory.contains(finalBar), Rs2Random.between(2000, 5000));
                                    sleepHumanReaction();
                                }
                            }
                            BreakHandlerScript.lockState.set(false);
                        }

                        if(Rs2Inventory.contains(mould) && Rs2Inventory.contains(gem) && Rs2Inventory.contains(bar) && !Rs2Inventory.contains(it->it!=null&&it.isNoted())){
                            closeTheBank();

                            GameObject furnace = Rs2GameObject.findObject("furnace", true, 10, false, chosenSpot);

                            if (furnace == null) {
                                Rs2Walker.walkTo(chosenSpot);
                                return;
                            }

                            if (!Rs2Camera.isTileOnScreen(furnace.getLocalLocation())) {
                                Rs2Camera.turnTo(furnace.getLocalLocation());
                                return;
                            }

                            Rs2GameObject.interact(furnace, "smelt");
                            sleepUntilTrue(() -> Rs2Widget.isGoldCraftingWidgetOpen() || Rs2Widget.isSilverCraftingWidgetOpen(), 500, 20000);
                            sleepHumanReaction();
                            Rs2Widget.clickWidget(craftingProduct);
                            sleepThroughMulipleAnimations();
                        }

                    }

                    if(craftingMaterial.equals("Leather")) {
                        if (Rs2Inventory.contains(craftingMaterial) && Rs2Inventory.contains("Thread") && Rs2Inventory.contains("Needle") && !Rs2Inventory.contains(it->it!=null&&it.isNoted())) {
                            closeTheBank();

                            Rs2Inventory.combine(ItemID.NEEDLE, ItemID.LEATHER);

                            String whatWereCrafting = craftingProduct;
                            sleepUntil(() -> Rs2Widget.hasWidget(whatWereCrafting), Rs2Random.between(2000, 5000));
                            sleepHumanReaction();
                            Widget craftingWidget = Rs2Widget.findWidget(craftingProduct);
                            if (craftingWidget != null) {
                                Rs2Widget.clickWidget(craftingWidget);
                            } else {
                                Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                            }

                            sleepThroughMulipleAnimations();
                        }
                        if (!Rs2Inventory.contains(craftingMaterial) || Rs2Inventory.count(craftingMaterial) < 3 || !Rs2Inventory.contains("Thread") || !Rs2Inventory.contains("Needle") || Rs2Inventory.contains(it -> it != null && it.isNoted())) {
                            walkToBankAndOpenIt();
                            if (Rs2Inventory.contains(craftingProduct) || Rs2Inventory.isFull() || Rs2Inventory.contains(it -> it != null && it.isNoted()) || weChangeActivity) {
                                Rs2Bank.depositAll();
                                sleepUntil(() -> Rs2Inventory.isEmpty(), Rs2Random.between(2000, 5000));
                                sleepHumanReaction();
                                if (Rs2Inventory.isEmpty()) {
                                    weChangeActivity = false;
                                }
                            }
                            if (!Rs2Inventory.contains("Thread")) {
                                if (Rs2Bank.isOpen()) {
                                    if (Rs2Bank.getBankItem("Thread", true) != null && Rs2Bank.getBankItem("Thread", true).getQuantity() > 10) {
                                        Rs2Bank.withdrawAll("Thread", true);
                                        sleepUntil(() -> Rs2Inventory.contains("Thread"), Rs2Random.between(2000, 5000));
                                        sleepHumanReaction();
                                    } else {
                                        openGEandBuyItem("Thread", Rs2Random.between(100, 200));
                                    }
                                }
                            }
                            if (!Rs2Inventory.contains("Needle")) {
                                if (Rs2Bank.isOpen()) {
                                    if (Rs2Bank.getBankItem("Needle", true) != null) {
                                        Rs2Bank.withdrawOne("Needle", true);
                                        sleepUntil(() -> Rs2Inventory.contains("Needle"), Rs2Random.between(2000, 5000));
                                        sleepHumanReaction();
                                    } else {
                                        openGEandBuyItem("Needle", 1);
                                    }
                                }
                            }
                            if (Rs2Inventory.contains("Needle") && Rs2Inventory.contains("Thread") && !Rs2Inventory.isFull() && !Rs2Inventory.contains(craftingMaterial) || Rs2Inventory.count(craftingMaterial) < 3) {
                                if (Rs2Bank.isOpen()) {
                                    if (Rs2Bank.getBankItem(craftingMaterial, true) != null) {
                                        Rs2Bank.withdrawAll(craftingMaterial, true);
                                        Rs2Inventory.waitForInventoryChanges(5000);
                                        sleepHumanReaction();
                                    } else {
                                        openGEandBuyItem(craftingMaterial, Rs2Random.between(100, 300));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void fletching() {
        if(shouldFletching) {
            // ALWAYS prioritize bronze arrows
            String material1 = "Headless arrow";
            String material2 = "Bronze arrowtips"; 
            String product = "Bronze arrow";
            int buyQuantity = 1000; // Fixed quantity as requested
            
            // Always use Grand Exchange as the fletching location (banking hub)
            if(chosenSpot == null) {
                chosenSpot = BankLocation.GRAND_EXCHANGE.getWorldPoint();
            }
            
            // SMART PREREQUISITE CHECK: Since we're going to GE anyway, we can buy materials there
            boolean hasMaterial1 = Rs2Inventory.contains(material1) || 
                                  (Rs2Bank.isOpen() && Rs2Bank.getBankItem(material1, true) != null) ||
                                  (!Rs2Bank.isOpen() && Rs2Bank.hasItem(material1));
            boolean hasMaterial2 = Rs2Inventory.contains(material2) || 
                                  (Rs2Bank.isOpen() && Rs2Bank.getBankItem(material2, true) != null) ||
                                  (!Rs2Bank.isOpen() && Rs2Bank.hasItem(material2));
            
            // Since destination IS the GE, we can proceed there and buy materials if needed
            Microbot.log("FLETCHING: Proceeding to GE (destination) - will purchase missing materials there if needed");
            
            if(Rs2Player.getWorldLocation().distanceTo(chosenSpot) > 12) {
                Rs2Walker.walkTo(chosenSpot);
            } else {
                // Check if we need to go to bank (missing materials or have products)
                if(!Rs2Inventory.contains(material1) || !Rs2Inventory.contains(material2) || 
                   Rs2Inventory.contains(product) || Rs2Inventory.contains(it -> it != null && it.isNoted()) || 
                   weChangeActivity) {
                   
                    walkToBankAndOpenIt();
                    
                    // Handle activity change deposits
                    if(weChangeActivity || Rs2Inventory.contains(it -> it != null && it.isNoted())) {
                        Rs2Bank.depositAll();
                        sleepUntil(() -> Rs2Inventory.isEmpty(), Rs2Random.between(2000, 5000));
                        sleepHumanReaction();
                        if(Rs2Inventory.isEmpty()) {
                            weChangeActivity = false;
                        }
                    }
                    
                    // Deposit finished products
                    if(Rs2Inventory.contains(product)) {
                        Rs2Bank.depositAll(product);
                        sleepUntil(() -> !Rs2Inventory.contains(product), Rs2Random.between(2000, 5000));
                        sleepHumanReaction();
                    }
                    
                    // Check if we have materials in bank, if not - buy them
                    if(!Rs2Inventory.contains(material1)) {
                        if(Rs2Bank.getBankItem(material1, true) != null && Rs2Bank.getBankItem(material1, true).getQuantity() >= 100) {
                            // We have them in bank - withdraw
                            Rs2Bank.withdrawX(material1, buyQuantity);
                            sleepUntil(() -> Rs2Inventory.contains(material1), Rs2Random.between(2000, 5000));
                            sleepHumanReaction();
                        } else {
                            // Need to buy - check affordability first
                            Rs2ItemManager itemManager = new Rs2ItemManager();
                            int itemsPrice = itemManager.getGEPrice(itemManager.getItemId(material1));
                            int totalCost = (int)(itemsPrice * buyQuantity * 1.30); // 30% markup
                            
                            if(totalCost <= totalGP) {
                                goToBankandGrabAnItem(material1, buyQuantity);
                            } else {
                                Microbot.log("Can't afford " + material1 + ", re-rolling activity");
                                shouldThink = true;
                                return;
                            }
                        }
                    }
                    
                    if(!Rs2Inventory.contains(material2) && Rs2Inventory.contains(material1)) {
                        if(Rs2Bank.getBankItem(material2, true) != null && Rs2Bank.getBankItem(material2, true).getQuantity() >= 100) {
                            // We have them in bank - withdraw
                            Rs2Bank.withdrawX(material2, buyQuantity);
                            sleepUntil(() -> Rs2Inventory.contains(material2), Rs2Random.between(2000, 5000));
                            sleepHumanReaction();
                        } else {
                            // Need to buy - check affordability
                            Rs2ItemManager itemManager = new Rs2ItemManager();
                            int itemsPrice = itemManager.getGEPrice(itemManager.getItemId(material2));
                            int totalCost = (int)(itemsPrice * buyQuantity * 1.30); // 30% markup
                            
                            if(totalCost <= totalGP) {
                                goToBankandGrabAnItem(material2, buyQuantity);
                            } else {
                                Microbot.log("Can't afford " + material2 + ", re-rolling activity");
                                shouldThink = true;
                                return;
                            }
                        }
                    }
                    
                    // ONLY NOW check for knife (since we need it for fletching)
                    if(!Rs2Inventory.contains("Knife") && Rs2Inventory.contains(material1) && Rs2Inventory.contains(material2)) {
                        goToBankandGrabAnItem("Knife", 1);
                    }
                }
                
                // Perform fletching ONLY if we have all materials including knife
                if(Rs2Inventory.contains("Knife") && Rs2Inventory.contains(material1) && 
                   Rs2Inventory.contains(material2) && !Rs2Inventory.contains(it->it!=null&&it.isNoted())) {
                   
                    closeTheBank();
                    
                    // Combine arrowtips with headless arrows to make bronze arrows
                    Rs2Inventory.combine(material2, material1);
                    sleepThroughMulipleAnimations();
                }
            }
        }
    }

    public void cook(){
        if(shouldCook){
            String whatToCook = "Unknown";
            int cookingLvl = Rs2Player.getRealSkillLevel(Skill.COOKING);
            if(cookingLvl < 15){whatToCook = "Raw chicken";}
            if(cookingLvl >= 15){whatToCook = "SwitchToFishing";}

            if(whatToCook.equals("SwitchToFishing")){
                Microbot.log("We're going fishing.");
                shouldFish = true;
                shouldCook = false;
                shouldThink = false;
                chosenSpot = null;
                weChangeActivity = true;
                return;
            }

            // PREREQUISITE CHECK: Verify we have required materials
            boolean hasRequiredMaterials = Rs2Inventory.contains(whatToCook) || 
                                          (Rs2Bank.isOpen() && Rs2Bank.getBankItem(whatToCook, true) != null) ||
                                          (!Rs2Bank.isOpen() && Rs2Bank.hasItem(whatToCook));
            
            if (!hasRequiredMaterials) {
                Microbot.log("COOKING: Missing " + whatToCook + ", going to GE to purchase");
                openGEandBuyItem(whatToCook, Rs2Random.between(100, 200));
                return; // Wait for next cycle after purchase attempt
            }

            Microbot.log("COOKING: Prerequisites met, proceeding to cooking location");

                if(chosenSpot == null){
                    chosenSpot = new WorldPoint(3274,3180,0);
                }

                if(chosenSpot != null){
                    if(Rs2Player.getWorldLocation().distanceTo(chosenSpot) > 12){
                        Rs2Walker.walkTo(chosenSpot);
                    } else {
                        if(Rs2Inventory.contains(whatToCook) && !Rs2Inventory.get(whatToCook).isNoted()){
                            closeTheBank();

                            GameObject range = Rs2GameObject.getGameObject("Range");
                            if (range != null) {
                                if (!Rs2Camera.isTileOnScreen(range.getLocalLocation())) {
                                    Rs2Camera.turnTo(range.getLocalLocation());
                                    return;
                                }
                                Rs2Inventory.useItemOnObject(Rs2Inventory.get(whatToCook).getId(), range.getId());
                                sleepUntil(() -> !Rs2Player.isMoving() && Rs2Widget.findWidget("like to cook?", null, false) != null);
                                sleepHumanReaction();
                                Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
                                sleepHumanReaction();
                                sleepThroughMulipleAnimations();
                            }
                        }
                        if(!Rs2Inventory.contains(whatToCook) || Rs2Inventory.get(whatToCook).isNoted() || weChangeActivity){
                            walkToBankAndOpenIt();

                            if (Rs2Bank.isOpen()) {
                                if(Rs2Inventory.contains("Cooked chicken") || weChangeActivity || !Rs2Inventory.onlyContains("Raw chicken") || Rs2Inventory.contains(it->it!=null&&it.isNoted())){
                                    Rs2Bank.depositAll();
                                    sleepUntil(()->!Rs2Inventory.contains("Cooked chicken"), Rs2Random.between(3000, 6000));
                                    sleepHumanReaction();
                                    if(Rs2Inventory.isEmpty() && weChangeActivity){
                                        weChangeActivity = false;
                                    }
                                }
                                if(!Rs2Inventory.contains(whatToCook) && !Rs2Inventory.isFull()){
                                    if(Rs2Bank.getBankItem(whatToCook, true) != null) {
                                        Rs2Bank.withdrawAll(whatToCook, true);
                                        String cooked = whatToCook;
                                        sleepUntil(() -> !Rs2Inventory.contains(cooked), Rs2Random.between(3000, 6000));
                                        sleepHumanReaction();
                                    } else {
                                        openGEandBuyItem(whatToCook, Rs2Random.between(100,200));
                                    }
                                }
                            }
                        }
                    }
                }
        }
    }

    public void woodCutting(){
        if(shouldWoodcut){
            String treeToChop = "Unknown";
            String axeToUse = "Unknown";
            int wcLvl = Rs2Player.getRealSkillLevel(Skill.WOODCUTTING);
            if(wcLvl < 15){axeToUse = "Iron axe"; treeToChop = "Tree";}
            if(wcLvl >= 15 && wcLvl < 30){axeToUse = "Steel axe"; treeToChop = "Oak tree";}
            if(wcLvl == 30){axeToUse = "Mithril axe"; treeToChop = "Willow tree";}
            if(wcLvl >= 31 && wcLvl < 41){axeToUse = "Adamant axe"; treeToChop = "Willow tree";}
            if(wcLvl >= 41){axeToUse = "Rune axe"; treeToChop = "Willow tree";}
            String finalaxe = axeToUse;

            if(Rs2Inventory.contains(axeToUse) || Rs2Equipment.isWearing(it->it!=null&&it.getName().equals(finalaxe))){

                if(chosenSpot == null){
                    WorldPoint spot1 = null;
                    WorldPoint spot2 = null;
                    if(treeToChop.equals("Tree")) {
                        spot1 = new WorldPoint(3157, 3456, 0);
                        spot2 = new WorldPoint(3164, 3406, 0);
                    }
                    if(treeToChop.equals("Oak tree")) {
                        spot1 = new WorldPoint(3164, 3419, 0);
                        spot2 = new WorldPoint(3127, 3433, 0);
                    }
                    if(treeToChop.equals("Willow tree")) {
                        spot1 = new WorldPoint(3087, 3236, 0);
                        spot2 = new WorldPoint(3087, 3236, 0);
                    }
                    if (Rs2Random.between(0, 100) <= 50) {
                        chosenSpot = spot1;
                    } else {
                        chosenSpot = spot2;
                    }
                }

                if(chosenSpot != null){
                    if(Rs2Player.getWorldLocation().distanceTo(chosenSpot) > 12){
                        Rs2Walker.walkTo(chosenSpot);
                    } else {
                        if(Rs2Inventory.isFull()){
                            walkToBankAndOpenIt();

                            if(Rs2Bank.isOpen()){
                                Rs2Bank.depositAllExcept(axeToUse);
                                sleepUntil(()-> !Rs2Inventory.isFull(), Rs2Random.between(2000,5000));
                                sleepHumanReaction();
                            }
                            if(Rs2Bank.isOpen()&&!Rs2Inventory.isFull()){
                                Rs2Bank.closeBank();
                            }
                        } else {
                            closeTheBank();

                            GameObject ourTree;
                            ourTree = Rs2GameObject.getGameObject(treeToChop, true);

                            if(ourTree == null){
                                Microbot.log("Tree is null");
                                return;
                            }

                            if(ourTree.getWorldLocation().distanceTo(chosenSpot) >= 12){
                                Microbot.log("Tree is too far from our spot");
                                Rs2Walker.walkTo(chosenSpot);
                                return;
                            }

                            if(!Rs2Player.isAnimating() && !Rs2Player.isMoving()){
                                ourTree = Rs2GameObject.getGameObject(treeToChop, true);
                                if(Rs2GameObject.interact(ourTree, "Chop down")){
                                    sleepThroughMulipleAnimations();
                                    sleepHumanReaction();
                                }
                            }
                        }
                    }
                }

            } else {
                goToBankandGrabAnItem(axeToUse, 1);
            }
        }
    }
    public void sleepHumanReaction(){
        if(Rs2Random.between(0,100) < 98) {
            sleep(0, 1000);
        }
    }

    public void mining(){
        if(shouldMine){
            String rockToMine = "Unknown";
            String axeToUse = "Unknown";
            int miningLvl = Rs2Player.getRealSkillLevel(Skill.MINING);
            if(miningLvl < 21){axeToUse = "Iron pickaxe"; rockToMine = "Tin rocks";}
            if(miningLvl >= 21 && miningLvl < 31){axeToUse = "Mithril pickaxe"; rockToMine = "Iron rocks";}
            if(miningLvl >= 31 && miningLvl < 41){axeToUse = "Adamant pickaxe"; rockToMine = "Iron rocks";}
            if(miningLvl >= 41){axeToUse = "Rune pickaxe"; rockToMine = "Iron rocks";}
            String finalaxe = axeToUse;

            if(Rs2Inventory.contains(axeToUse) || Rs2Equipment.isWearing(it->it!=null&&it.getName().equals(finalaxe))){

                if(chosenSpot == null){
                    WorldPoint spot1 = null;
                    WorldPoint spot2 = null;
                    if(rockToMine.equals("Tin rocks")) {
                        spot1 = new WorldPoint(3183, 3374, 0);
                        spot2 = new WorldPoint(3283, 3362, 0);
                    }
                    if(rockToMine.equals("Iron rocks")) {
                        spot1 = new WorldPoint(3174, 3366, 0);
                        spot2 = new WorldPoint(3296, 3309, 0);
                    }
                    if (Rs2Random.between(0, 100) <= 50) {
                        chosenSpot = spot1;
                    } else {
                        chosenSpot = spot2;
                        if(rockToMine.equals("Iron rocks")) {
                            if (Rs2Player.getCombatLevel() < 30) {
                                Microbot.log("We can't mine at Al Kharid until we get 30 combat");
                                chosenSpot = spot1;
                            }
                        }
                    }

                }

                if(chosenSpot != null){
                    if(Rs2Player.getWorldLocation().distanceTo(chosenSpot) > 15){
                        Rs2Walker.walkTo(chosenSpot);
                    } else {
                        if(Rs2Inventory.isFull()){
                            if(rockToMine.equals("Tin rocks")) {
                                walkToBankAndOpenIt();

                                if (Rs2Bank.isOpen()) {
                                    Rs2Bank.depositAllExcept(axeToUse);
                                    sleepUntil(() -> !Rs2Inventory.isFull(), Rs2Random.between(2000, 5000));
                                    sleepHumanReaction();
                                }
                                if (Rs2Bank.isOpen() && !Rs2Inventory.isFull()) {
                                    Rs2Bank.closeBank();
                                }
                            }
                            if(rockToMine.equals("Iron rocks")) {
                                Rs2Inventory.dropAllExcept(axeToUse);
                            }
                        } else {
                            closeTheBank();

                            GameObject ourRock = Rs2GameObject.getGameObject(rockToMine);
                            if(ourRock!=null){
                                if(!Rs2Player.isAnimating()){
                                    if(Rs2GameObject.interact(ourRock, "Mine")){
                                        sleepUntil(()-> !Rs2Player.isAnimating() || ourRock == null, Rs2Random.between(20000,50000));
                                        sleepHumanReaction();
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                goToBankandGrabAnItem(axeToUse,1);
            }
        }
    }

    public void fishing(){
        String fishingAction = "Unknown";
        String fishingGear = "Unknown";
        int fishingLvl = Rs2Player.getRealSkillLevel(Skill.FISHING);
        if(fishingLvl < 21){fishingGear = "Small fishing net"; fishingAction = "Net";}
        if(fishingLvl >= 21){fishingGear = "Fly fishing rod"; fishingAction = "Lure";}
        String finalGear = fishingGear;

        if(shouldFish){
            if(Rs2Inventory.contains(it->it!=null&&it.getName().contains(finalGear))){

                if(chosenSpot == null){
                    WorldPoint spot1 = null;
                    WorldPoint spot2 = null;

                    if(fishingGear.equals("Small fishing net")){
                        spot1 = new WorldPoint(3241,3151,0);
                        spot2 = new WorldPoint(3241,3151,0);
                    }

                    if(fishingGear.equals("Fly fishing rod")){
                        spot1 = new WorldPoint(3104,3430,0);
                        spot2 = new WorldPoint(3104,3430,0);
                    }

                    if(Rs2Random.between(0,100) <=50){
                        chosenSpot = spot1;
                    } else {
                        chosenSpot = spot2;
                    }
                }

                if(chosenSpot != null){
                    if(Rs2Player.getWorldLocation().distanceTo(chosenSpot) > 15){
                        Rs2Walker.walkTo(chosenSpot);
                    } else {
                        if(Rs2Inventory.isFull()){
                            if(fishingGear.equals("Small fishing net")){
                                if(Rs2Inventory.dropAllExcept(fishingGear)){
                                    sleepUntil(()-> !Rs2Inventory.isFull(), Rs2Random.between(2000,5000));
                                    sleepHumanReaction();
                                }
                            }
                            if(fishingGear.equals("Fly fishing rod")){

                                int cookingLvl = Rs2Player.getRealSkillLevel(Skill.COOKING);
                                if(cookingLvl < 15){
                                    if(Rs2Inventory.dropAllExcept(fishingGear, "Feather")){
                                        sleepUntil(()-> !Rs2Inventory.isFull(), Rs2Random.between(2000,5000));
                                        sleepHumanReaction();
                                    }
                                }
                                if(cookingLvl >= 15 && cookingLvl < 25){
                                    if(Rs2Inventory.contains("Raw trout")){
                                        cookFish(Rs2Inventory.get("Raw trout").getId());
                                        sleepThroughMulipleAnimations();
                                    }
                                    if(Rs2Inventory.dropAllExcept(fishingGear, "Feather")){
                                        sleepUntil(()-> !Rs2Inventory.isFull(), Rs2Random.between(2000,5000));
                                        sleepHumanReaction();
                                    }
                                }
                                if(cookingLvl >= 25){
                                    if(Rs2Random.between(0,100) < 50){
                                        if(Rs2Inventory.contains("Raw trout")){
                                            cookFish(Rs2Inventory.get("Raw trout").getId());
                                            sleepThroughMulipleAnimations();
                                        }
                                        if(Rs2Inventory.contains("Raw salmon")){
                                            cookFish(Rs2Inventory.get("Raw salmon").getId());
                                            sleepThroughMulipleAnimations();
                                        }
                                    } else {
                                        if(Rs2Inventory.contains("Raw salmon")){
                                            cookFish(Rs2Inventory.get("Raw salmon").getId());
                                            sleepThroughMulipleAnimations();
                                        }
                                        if(Rs2Inventory.contains("Raw trout")){
                                            cookFish(Rs2Inventory.get("Raw trout").getId());
                                            sleepThroughMulipleAnimations();
                                        }
                                    }
                                    if(Rs2Inventory.dropAllExcept(fishingGear, "Feather")){
                                        sleepUntil(()-> !Rs2Inventory.isFull(), Rs2Random.between(2000,5000));
                                        sleepHumanReaction();
                                    }
                                }
                            }

                        } else {
                            if(fishingGear.equals("Fly fishing rod")) {
                                if (!Rs2Inventory.contains("Feather")) {
                                    goToBankandGrabAnItem("Feather", Rs2Random.between(500, 2000));
                                    return;
                                }
                            }

                            closeTheBank();

                            Rs2NpcModel ourFishingSpot = Rs2Npc.getNpc("Fishing spot");
                            if(ourFishingSpot!=null){
                                if(!Rs2Player.isAnimating()){
                                    if(Rs2Npc.interact(ourFishingSpot, fishingAction)){
                                        sleepUntil(()-> !Rs2Player.isAnimating() || ourFishingSpot == null, Rs2Random.between(20000,50000));
                                        sleepHumanReaction();
                                    }
                                }
                            }
                        }
                    }
                }

            } else {
                goToBankandGrabAnItem(fishingGear, 1);
                if(fishingGear.equals("Fly fishing rod")) {
                    if (!Rs2Inventory.contains("Feather")) {
                        goToBankandGrabAnItem("Feather", Rs2Random.between(500, 2000));
                    }
                }
            }
        }
    }

    public void cookFish(int fishesID){
        if (Rs2Inventory.contains(fishesID)) {
            Rs2Inventory.useItemOnObject(fishesID, 43475);
            sleepUntil(() -> !Rs2Player.isMoving() && Rs2Widget.findWidget("How many would you like to cook?", null, false) != null, 10000);
            sleepHumanReaction();
            Rs2Keyboard.keyPress(KeyEvent.VK_SPACE);
        }
    }

    public void smelting(){
        if(shouldSmelt){
            // Determine what we're smelting based on level
            boolean isSilverSmelting = Rs2Player.getRealSkillLevel(Skill.SMITHING) >= 20;
            
            // PREREQUISITE CHECK: Verify we have required materials
            boolean hasRequiredMaterials = false;
            if (isSilverSmelting) {
                // Check for silver ore in inventory or bank
                hasRequiredMaterials = Rs2Inventory.contains("Silver ore") || 
                                     (Rs2Bank.isOpen() && Rs2Bank.getBankItem("Silver ore", true) != null) ||
                                     (!Rs2Bank.isOpen() && Rs2Bank.hasItem("Silver ore"));
                
                if (!hasRequiredMaterials) {
                    Microbot.log("SMELTING: Missing Silver ore, going to GE to purchase");
                    openGEandBuyItem("Silver ore", Rs2Random.between(100, 200));
                    return; // Wait for next cycle after purchase attempt
                }
            } else {
                // Check for both copper and tin ore
                boolean hasCopper = Rs2Inventory.contains("Copper ore") || 
                                   (Rs2Bank.isOpen() && Rs2Bank.getBankItem("Copper ore") != null) ||
                                   (!Rs2Bank.isOpen() && Rs2Bank.hasItem("Copper ore"));
                boolean hasTin = Rs2Inventory.contains("Tin ore") || 
                                (Rs2Bank.isOpen() && Rs2Bank.getBankItem("Tin ore") != null) ||
                                (!Rs2Bank.isOpen() && Rs2Bank.hasItem("Tin ore"));
                
                if (!hasCopper) {
                    Microbot.log("SMELTING: Missing Copper ore, going to GE to purchase");
                    openGEandBuyItem("Copper ore", Rs2Random.between(100, 200));
                    return;
                }
                if (!hasTin) {
                    Microbot.log("SMELTING: Missing Tin ore, going to GE to purchase");
                    openGEandBuyItem("Tin ore", Rs2Random.between(100, 200));
                    return;
                }
                hasRequiredMaterials = hasCopper && hasTin;
            }

            Microbot.log("SMELTING: Prerequisites met, proceeding to furnace location");

                if(chosenSpot == null){
                    WorldPoint spot1 = new WorldPoint(3106,3498,0);
                    WorldPoint spot2 = new WorldPoint(3106,3498,0);
                    if(Rs2Random.between(0,100) <=50){
                        chosenSpot = spot1;
                    } else {
                        chosenSpot = spot2;
                    }
                }

                if(chosenSpot != null){
                    if(Rs2Player.getWorldLocation().distanceTo(chosenSpot) > 15){
                        Rs2Walker.walkTo(chosenSpot);
                    } else {
                        //smelting bronze or silver
                        boolean smeltingBronze = false;
                        boolean smeltingSilver = false;

                        if (Rs2Player.getRealSkillLevel(Skill.SMITHING) >= 20) {
                            smeltingSilver = true;
                        } else {
                            smeltingBronze = true;
                        }

                        if (smeltingSilver) {
                            if (Rs2Inventory.contains("Silver bar") || !Rs2Inventory.contains("Silver ore") || Rs2Inventory.contains(it->it!=null&&it.isNoted())) {
                                walkToBankAndOpenIt();

                                if (Rs2Bank.isOpen()) {
                                    if (Rs2Inventory.contains("Silver bar") || Rs2Inventory.contains(it->it!=null&&it.isNoted()) || weChangeActivity) {
                                        int random = Rs2Random.between(0, 100);
                                        if (random <= 75) {
                                            Rs2Bank.depositAll();
                                            sleepUntil(() -> !Rs2Inventory.isFull(), Rs2Random.between(2000, 5000));
                                            sleepHumanReaction();
                                        } else {
                                            Rs2Bank.depositAll("Silver bar", true);
                                            sleepUntil(() -> !Rs2Inventory.isFull(), Rs2Random.between(2000, 5000));
                                            sleepHumanReaction();
                                        }
                                        if(Rs2Inventory.isEmpty()){
                                            weChangeActivity = false;
                                        }
                                    }
                                    if (!Rs2Inventory.contains("Silver bar") && !Rs2Inventory.isFull()) {
                                        if (Rs2Bank.getBankItem("Silver ore", true) != null) {
                                            Rs2Bank.withdrawAll("Silver ore", true);
                                            sleepUntil(() -> Rs2Inventory.isFull(), Rs2Random.between(2000, 5000));
                                            sleepHumanReaction();
                                        } else {
                                            //we need to buy silver ore
                                            openGEandBuyItem("Silver ore", Rs2Random.between(100,200));
                                        }
                                    }
                                }
                            }
                            if (Rs2Inventory.contains("Silver ore") && !Rs2Inventory.contains(it->it!=null&&it.isNoted())) {
                                smeltTheBar(Bars.SILVER);
                            }
                        }

                        if (smeltingBronze) {
                            if (Rs2Inventory.contains("Bronze bar") || !Rs2Inventory.contains("Copper ore") || !Rs2Inventory.contains("Tin ore") || Rs2Inventory.contains(it->it!=null&&it.isNoted())) {
                                walkToBankAndOpenIt();

                                if (Rs2Bank.isOpen()) {
                                    if (Rs2Inventory.contains("Bronze bar") || Rs2Inventory.isFull() || Rs2Inventory.contains(it->it!=null&&it.isNoted()) || weChangeActivity) {
                                        int random = Rs2Random.between(0, 100);
                                        if (random <= 75) {
                                            Rs2Bank.depositAll();
                                            sleepUntil(() -> !Rs2Inventory.isFull(), Rs2Random.between(2000, 5000));
                                            sleepHumanReaction();
                                        } else {
                                            Rs2Bank.depositAll("Bronze bar", true);
                                            sleepUntil(() -> !Rs2Inventory.isFull(), Rs2Random.between(2000, 5000));
                                            sleepHumanReaction();
                                        }
                                        if(Rs2Inventory.isEmpty()){
                                            weChangeActivity = false;
                                        }
                                    }
                                    if ((!Rs2Inventory.contains("Copper ore") && !Rs2Inventory.contains("Tin ore")) && !Rs2Inventory.isFull()) {
                                        if (Rs2Bank.getBankItem("Copper ore") != null && Rs2Bank.getBankItem("Tin ore") != null) {
                                            if(Rs2Bank.getBankItem("Copper ore").getQuantity() < 14 || Rs2Bank.getBankItem("Tin ore").getQuantity() < 14){
                                                outOfOre();
                                                return;
                                            }
                                            int random = Rs2Random.between(0, 100);
                                            if (random <= 50) {
                                                if (Rs2Inventory.count("Copper ore") < 14) {
                                                    Rs2Bank.withdrawX("Copper ore", 14);
                                                    sleepUntil(() -> Rs2Inventory.count("Copper ore") >= 14, Rs2Random.between(2000, 5000));
                                                    sleepHumanReaction();
                                                }
                                                if (Rs2Inventory.count("Tin ore") < 14) {
                                                    Rs2Bank.withdrawX("Tin ore", 14);
                                                    sleepUntil(() -> Rs2Inventory.count("Tin ore") >= 14, Rs2Random.between(2000, 5000));
                                                    sleepHumanReaction();
                                                }
                                            } else {
                                                if (Rs2Inventory.count("Tin ore") < 14) {
                                                    Rs2Bank.withdrawX("Tin ore", 14);
                                                    sleepUntil(() -> Rs2Inventory.count("Tin ore") >= 14, Rs2Random.between(2000, 5000));
                                                    sleepHumanReaction();
                                                }
                                                if (Rs2Inventory.count("Copper ore") < 14) {
                                                    Rs2Bank.withdrawX("Copper ore", 14);
                                                    sleepUntil(() -> Rs2Inventory.count("Copper ore") >= 14, Rs2Random.between(2000, 5000));
                                                    sleepHumanReaction();
                                                }
                                            }
                                        } else {
                                            //we need to buy copper ore
                                            outOfOre();
                                        }
                                    }
                                }
                            }
                            if ((Rs2Inventory.contains("Copper ore") && Rs2Inventory.contains("Tin ore")) && !Rs2Inventory.contains(it->it!=null&&it.isNoted())) {
                                smeltTheBar(Bars.BRONZE);
                            }

                        }
                    }
                }

        }
    }

    public void outOfOre(){
        //we need to buy
        int amt = Rs2Random.between(100,200);
        if (Rs2Bank.getBankItem("Tin ore") == null || Rs2Bank.getBankItem("Tin ore").getQuantity() < 14) {
            openGEandBuyItem("Tin ore", amt);
        }
        if (Rs2Bank.getBankItem("Copper ore") == null || Rs2Bank.getBankItem("Copper ore").getQuantity() < 14) {
            openGEandBuyItem("Copper ore", amt);
        }
    }

    public void smeltTheBar(Bars bar){
        // interact with the furnace until the smelting dialogue opens in chat, click the selected bar icon
        GameObject furnace = Rs2GameObject.findObject("furnace", true, 10, false, chosenSpot);
        if(furnace == null){
            if (Rs2Bank.isOpen())
                Rs2Bank.closeBank();
            Rs2Walker.walkTo(chosenSpot, 4);
            return;
        }
        if (furnace != null) {
            Rs2GameObject.interact(furnace, "smelt");
            Rs2Widget.sleepUntilHasWidgetText("What would you like to smelt?", 270, 5, false, 20000);
            sleepHumanReaction();
            Rs2Widget.clickWidget(bar.getName());
            Rs2Widget.sleepUntilHasNotWidgetText("What would you like to smelt?", 270, 5, false, 5000);
        }

        sleepThroughMulipleAnimations();
    }

    public void firemake(){
        if(shouldFiremake){
            String logsToBurn = "Unknown";
            int fireMakingLvl = Rs2Player.getRealSkillLevel(Skill.FIREMAKING);
            if(fireMakingLvl < 15){ logsToBurn = "Logs";}
            if(fireMakingLvl >= 15 && fireMakingLvl < 30){ logsToBurn = "Oak logs";}
            if(fireMakingLvl >= 30){ logsToBurn = "Willow logs";}

            // PREREQUISITE CHECK: Verify we have required materials
            boolean hasLogs = Rs2Inventory.contains(logsToBurn) || 
                             (Rs2Bank.isOpen() && Rs2Bank.getBankItem(logsToBurn, true) != null) ||
                             (!Rs2Bank.isOpen() && Rs2Bank.hasItem(logsToBurn));
            boolean hasTinderbox = Rs2Inventory.contains(ItemID.TINDERBOX) || 
                                  Rs2Equipment.isWearing(ItemID.TINDERBOX) ||
                                  (Rs2Bank.isOpen() && Rs2Bank.getBankItem(ItemID.TINDERBOX) != null) ||
                                  (!Rs2Bank.isOpen() && Rs2Bank.hasItem(ItemID.TINDERBOX));
            
            if (!hasLogs) {
                Microbot.log("FIREMAKING: Missing " + logsToBurn + ", going to GE to purchase");
                openGEandBuyItem(logsToBurn, Rs2Random.between(100, 300));
                return; // Wait for next cycle after purchase attempt
            }
            if (!hasTinderbox) {
                Microbot.log("FIREMAKING: Missing Tinderbox, going to GE to purchase");
                openGEandBuyItem("Tinderbox", 1);
                return; // Wait for next cycle after purchase attempt
            }

            Microbot.log("FIREMAKING: Prerequisites met, proceeding to firemaking location");

                if(chosenSpot == null){
                    WorldPoint spot1 = new WorldPoint(3171,3495,0);
                    WorldPoint spot2 = new WorldPoint(3171,3484,0);
                    if(Rs2Random.between(0,100) <=50){
                        chosenSpot = spot1;
                    } else {
                        chosenSpot = spot2;
                    }
                }

                if(chosenSpot != null){
                    if(Rs2Player.getWorldLocation().distanceTo(chosenSpot) > 15){
                        Rs2Walker.walkTo(chosenSpot);
                    } else {
                        String fixedLogstoBurn = logsToBurn;
                        if(!Rs2Inventory.contains(logsToBurn) || !Rs2Inventory.contains(ItemID.TINDERBOX) || Rs2Inventory.contains(it->it!=null&&it.isNoted()) || weChangeActivity){
                            walkToBankAndOpenIt();

                            if(Rs2Bank.isOpen()){
                                if(Rs2Inventory.contains(it->it!=null&&it.isNoted()) || weChangeActivity){
                                    Rs2Bank.depositAll();
                                    sleepUntil(()-> Rs2Inventory.isEmpty(), Rs2Random.between(2000,5000));
                                    sleepHumanReaction();
                                    if(Rs2Inventory.isEmpty()){
                                        weChangeActivity = false;
                                    }
                                }
                                if(!Rs2Inventory.contains(ItemID.TINDERBOX)){
                                    if(Rs2Bank.getBankItem(ItemID.TINDERBOX) != null){
                                        Rs2Bank.withdrawOne(ItemID.TINDERBOX);
                                        sleepUntil(()-> Rs2Inventory.contains(ItemID.TINDERBOX), Rs2Random.between(2000,5000));
                                        sleepHumanReaction();
                                    } else {
                                        openGEandBuyItem("Tinderbox", 1);
                                    }
                                }
                                if(!Rs2Inventory.contains(logsToBurn)){
                                    if(Rs2Bank.getBankItem(logsToBurn, true) != null){
                                        Rs2Bank.withdrawAll(logsToBurn, true);
                                        String logs = logsToBurn;
                                        sleepUntil(()-> Rs2Inventory.contains(logs), Rs2Random.between(2000,5000));
                                        sleepHumanReaction();
                                    } else {
                                        openGEandBuyItem(logsToBurn, Rs2Random.between(100,300));
                                    }
                                }
                            }
                        }
                        if(Rs2Inventory.contains(logsToBurn) && Rs2Inventory.contains(ItemID.TINDERBOX) && !Rs2Inventory.contains(it->it!=null&&it.isNoted())){
                            closeTheBank();

                            GameObject fire = Rs2GameObject.getGameObject(it->it!=null&&it.getId()==ObjectID.FIRE&&it.getWorldLocation().equals(Rs2Player.getWorldLocation()));

                            if(Rs2Player.isStandingOnGameObject() || fire != null){
                                Microbot.log("We're standing on an object, moving.");
                                if(Rs2Player.getWorldLocation().equals(chosenSpot)){
                                    //we're standing on the starting tile and there's already a fire here. Grab a new starting tile.
                                    chosenSpot = null;
                                }
                                if(Rs2Player.distanceTo(chosenSpot) > 4){
                                    Rs2Walker.walkTo(chosenSpot);
                                } else {
                                    Rs2Walker.walkCanvas(chosenSpot);
                                }
                                return;
                            }

                            NPC banker = Rs2Npc.getNearestNpcWithAction("Bank");
                            NPC geClerk = Rs2Npc.getNearestNpcWithAction("Exchange");
                            if(banker == null || geClerk == null){
                                Microbot.log("Couldn't find GE Clerk or Banker, walking to the GE");
                                Rs2Walker.walkTo(BankLocation.GRAND_EXCHANGE.getWorldPoint());
                                return;
                            }

                            if (banker.getWorldLocation().distanceTo(Rs2Player.getWorldLocation()) <= 2 || geClerk.getWorldLocation().distanceTo(Rs2Player.getWorldLocation()) <= 2) {
                                Microbot.log("We're too close to the GE, moving.");
                                if (Rs2Player.distanceTo(chosenSpot) > 4) {
                                    Rs2Walker.walkTo(chosenSpot);
                                } else {
                                    Rs2Walker.walkCanvas(chosenSpot);
                                }
                                return;
                            }

                            if(Rs2Inventory.contains("Ashes")){
                                Rs2Inventory.dropAll("Ashes");
                                sleepHumanReaction();
                            }

                            Rs2Inventory.use("tinderbox");
                            sleepHumanReaction();
                            int id = Rs2Inventory.get(logsToBurn).getId();
                            Rs2Inventory.useLast(id);
                            sleepThroughMulipleAnimations();
                        }
                    }
                }

        }
    }

    //skilling

    public void sleepThroughMulipleAnimations(){
        BreakHandlerScript.lockState.set(true);
        if(Rs2Player.isMoving()){
            sleepUntil(()-> !Rs2Player.isMoving(), Rs2Random.between(10000,15000));
        }
        if(!Rs2Player.isAnimating()){
            sleepUntil(()-> Rs2Player.isAnimating(), Rs2Random.between(10000,15000));
        }
        if(!this.shouldFiremake) {
            int timeoutMs = Rs2Random.between(3000, 5000);
            while (Rs2Player.isAnimating() || Rs2Player.isAnimating(timeoutMs)) {
                if (!super.isRunning()) {
                    break;
                }
                sleepHumanReaction();
            }
        } else {
            int timeoutMs = Rs2Random.between(1500, 3000);
            while (Rs2Player.isAnimating() || Rs2Player.isAnimating(timeoutMs)) {
                if (!super.isRunning()) {
                    break;
                }
                sleepHumanReaction();
            }
        }
        BreakHandlerScript.lockState.set(false);
    }

    @Override
    public void shutdown() {
        // Cancel the scheduled task to ensure plugin stops completely
        if (mainScheduledFuture != null) {
            mainScheduledFuture.cancel(false);
            Microbot.log("SPH Account Builder: Shutdown complete, scheduled task cancelled");
        }
        super.shutdown();
    }
}