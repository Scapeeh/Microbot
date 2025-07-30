package com.myplugins.microbot.SPHSmartAccountBuilder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigInformation;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("accountBuilder")
@ConfigInformation("F2P/P2P Account Builder<br></br><br></br> Start your account off with 50-100k <br></br><br></br> Start the script and it'll decide what it wants to do. It'll change activity randomly between the configured time ranges<br></br><br></br>F2P supports: Fishing, Woodcutting, Crafting (Leather, Necklace), Mining, Smelting (Bronze, Silver), Firemaking, Cooking<br></br><br></br>P2P additionally includes: Fletching (Bronze arrows)<br></br><br></br>Configure your preferred activity change intervals below.")
public interface SPHAccountBuilderConfig extends Config {

    @ConfigItem(
            keyName = "minTimeMinutes",
            name = "Min Time (minutes)",
            description = "Minimum time before changing activity",
            position = 0
    )
    default int minTimeMinutes() {
        return 8;
    }

    @ConfigItem(
            keyName = "maxTimeMinutes",
            name = "Max Time (minutes)",
            description = "Maximum time before changing activity",
            position = 1
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
