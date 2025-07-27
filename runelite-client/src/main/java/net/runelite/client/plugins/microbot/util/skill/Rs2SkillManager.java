package net.runelite.client.plugins.microbot.util.skill;

import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.microbot.MicrobotConfig;

/**
 * Utility class for managing skill training permissions and checks
 */
public class Rs2SkillManager {

    private static MicrobotConfig getConfig() {
        return Microbot.getMicrobotConfigManager().getConfig(MicrobotConfig.class);
    }

    /**
     * Check if woodcutting training is enabled
     * @return true if woodcutting training is allowed
     */
    public static boolean isWoodcuttingEnabled() {
        return getConfig().enableWoodcutting();
    }

    /**
     * Check if mining training is enabled
     * @return true if mining training is allowed
     */
    public static boolean isMiningEnabled() {
        return getConfig().enableMining();
    }

    /**
     * Check if fishing training is enabled
     * @return true if fishing training is allowed
     */
    public static boolean isFishingEnabled() {
        return getConfig().enableFishing();
    }

    /**
     * Check if cooking training is enabled
     * @return true if cooking training is allowed
     */
    public static boolean isCookingEnabled() {
        return getConfig().enableCooking();
    }

    /**
     * Check if firemaking training is enabled
     * @return true if firemaking training is allowed
     */
    public static boolean isFiremakingEnabled() {
        return getConfig().enableFiremaking();
    }

    /**
     * Check if crafting training is enabled
     * @return true if crafting training is allowed
     */
    public static boolean isCraftingEnabled() {
        return getConfig().enableCrafting();
    }

    /**
     * Check if smithing training is enabled
     * @return true if smithing training is allowed
     */
    public static boolean isSmithingEnabled() {
        return getConfig().enableSmithing();
    }

    /**
     * Check if fletching training is enabled
     * @return true if fletching training is allowed
     */
    public static boolean isFletchingEnabled() {
        return getConfig().enableFletching();
    }

    /**
     * Check if runecrafting training is enabled
     * @return true if runecrafting training is allowed
     */
    public static boolean isRunecraftingEnabled() {
        return getConfig().enableRunecrafting();
    }

    /**
     * Check if construction training is enabled
     * @return true if construction training is allowed
     */
    public static boolean isConstructionEnabled() {
        return getConfig().enableConstruction();
    }

    /**
     * Check if hunter training is enabled
     * @return true if hunter training is allowed
     */
    public static boolean isHunterEnabled() {
        return getConfig().enableHunter();
    }

    /**
     * Check if farming training is enabled
     * @return true if farming training is allowed
     */
    public static boolean isFarmingEnabled() {
        return getConfig().enableFarming();
    }

    /**
     * Check if herblore training is enabled
     * @return true if herblore training is allowed
     */
    public static boolean isHerbloreEnabled() {
        return getConfig().enableHerblore();
    }

    /**
     * Check if thieving training is enabled
     * @return true if thieving training is allowed
     */
    public static boolean isThievingEnabled() {
        return getConfig().enableThieving();
    }

    /**
     * Generic method to check if a skill training is enabled by skill name
     * @param skillName the name of the skill (case-insensitive)
     * @return true if the skill training is enabled, false otherwise
     */
    public static boolean isSkillEnabled(String skillName) {
        if (skillName == null) return false;
        
        switch (skillName.toLowerCase()) {
            case "woodcutting":
                return isWoodcuttingEnabled();
            case "mining":
                return isMiningEnabled();
            case "fishing":
                return isFishingEnabled();
            case "cooking":
                return isCookingEnabled();
            case "firemaking":
                return isFiremakingEnabled();
            case "crafting":
                return isCraftingEnabled();
            case "smithing":
                return isSmithingEnabled();
            case "fletching":
                return isFletchingEnabled();
            case "runecrafting":
                return isRunecraftingEnabled();
            case "construction":
                return isConstructionEnabled();
            case "hunter":
                return isHunterEnabled();
            case "farming":
                return isFarmingEnabled();
            case "herblore":
                return isHerbloreEnabled();
            case "thieving":
                return isThievingEnabled();
            default:
                return false;
        }
    }

    /**
     * Check if any skill training is globally disabled
     * @return true if at least one skill is disabled
     */
    public static boolean hasAnySkillDisabled() {
        return !isWoodcuttingEnabled() || !isMiningEnabled() || !isFishingEnabled() ||
               !isCookingEnabled() || !isFiremakingEnabled() || !isCraftingEnabled() ||
               !isSmithingEnabled() || !isFletchingEnabled() || !isRunecraftingEnabled() ||
               !isConstructionEnabled() || !isHunterEnabled() || !isFarmingEnabled() ||
               !isHerbloreEnabled() || !isThievingEnabled();
    }
}