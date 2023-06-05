package it.fulminazzo.customhelp.Enums;

import it.angrybear.Enums.BearConfigOptions;
import it.fulminazzo.customhelp.CustomHelp;

public class ConfigOptions extends BearConfigOptions {
    public static final ConfigOptions COMMAND_NAME = new ConfigOptions("command-settings.name");
    public static final ConfigOptions COMMAND_ALIASES = new ConfigOptions("command-settings.aliases");
    public static final ConfigOptions COMMAND_DESCRIPTION = new ConfigOptions("command-settings.description");

    public static final ConfigOptions HIDE_COMMANDS = new ConfigOptions("hide-commands");

    public static final ConfigOptions COMMANDS = new ConfigOptions("commands");

    public ConfigOptions(String permission) {
        super(CustomHelp.getPlugin(), permission);
    }
}
