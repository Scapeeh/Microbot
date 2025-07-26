package com.yourname.pluginname;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("yourpluginname")
public interface YourPluginNameConfig extends Config {

    @ConfigSection(
            name = "General Settings",
            description = "General plugin configuration",
            position = 0
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "enablePlugin",
            name = "Enable Plugin",
            description = "Enable/disable the plugin functionality",
            position = 1,
            section = generalSection
    )
    default boolean enablePlugin() {
        return true;
    }

    @ConfigItem(
            keyName = "exampleDelay",
            name = "Action Delay (ms)",
            description = "Delay between actions in milliseconds",
            position = 2,
            section = generalSection
    )
    default int exampleDelay() {
        return 1000;
    }

    @ConfigItem(
            keyName = "exampleText",
            name = "Example Text",
            description = "Example text configuration",
            position = 3,
            section = generalSection
    )
    default String exampleText() {
        return "Hello World";
    }

    @ConfigSection(
            name = "Advanced Settings",
            description = "Advanced plugin configuration",
            position = 10
    )
    String advancedSection = "advanced";

    @ConfigItem(
            keyName = "debugMode",
            name = "Debug Mode",
            description = "Enable debug logging",
            position = 11,
            section = advancedSection
    )
    default boolean debugMode() {
        return false;
    }
}