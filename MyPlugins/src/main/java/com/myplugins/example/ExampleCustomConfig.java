package com.myplugins.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("examplecustom")
public interface ExampleCustomConfig extends Config {
    
    @ConfigItem(
        keyName = "greeting",
        name = "Greeting Message",
        description = "The greeting message to display"
    )
    default String greeting() {
        return "Hello from MyPlugins!";
    }
    
    @ConfigItem(
        keyName = "enabled",
        name = "Enable Plugin",
        description = "Enable or disable the plugin functionality"
    )
    default boolean enabled() {
        return true;
    }
    
    @ConfigItem(
        keyName = "interval",
        name = "Update Interval (ms)",
        description = "How often to update in milliseconds"
    )
    default int interval() {
        return 1000;
    }
}