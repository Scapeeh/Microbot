package com.myplugins.SPHSmartAccountBuilder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigInformation;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("accountBuilder")
@ConfigInformation("F2P Account Builder<br></br><br></br> Start your account off with 50-100k <br></br><br></br> Start the script and it'll decide what it wants to do. It'll change activity randomly between the configured time ranges<br></br><br></br>Currently supports:<br></br><br></br>Fishing<br></br>Woodcutting<br></br>Crafting (Leather, Necklace)<br></br>Mining<br></br>Smelting (Bronze, Silver)<br></br>Firemaking<br></br>Cooking<br></br>Fletching (Bronze arrows)<br></br><br></br>Configure your preferred activity change intervals below.")
public interface SPHAccountBuilderConfig extends Config {
    @ConfigItem(
            keyName = "enableFletching",
            name = "Enable Fletching",
            description = "Include fletching in skill rotation",
            position = 0
    )
    default boolean enableFletching() {
        return false;
    }

    @ConfigItem(
            keyName = "minTimeMinutes",
            name = "Min Time (minutes)",
            description = "Minimum time before changing activity",
            position = 1
    )
    default int minTimeMinutes() {
        return 8;
    }

    @ConfigItem(
            keyName = "maxTimeMinutes",
            name = "Max Time (minutes)",
            description = "Maximum time before changing activity",
            position = 2
    )
    default int maxTimeMinutes() {
        return 40;
    }
/*    @ConfigItem(
            keyName = "Ore",
            name = "Ore",
            description = "Choose the ore",
            position = 0
    )
    default List<String> ORE()
    {
        return Rocks.TIN;
    }*/
}
