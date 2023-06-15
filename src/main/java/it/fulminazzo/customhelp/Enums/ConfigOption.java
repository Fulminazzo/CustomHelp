package it.fulminazzo.customhelp.Enums;

import it.angrybear.Enums.BearConfigOption;
import it.fulminazzo.customhelp.CustomHelp;

public class ConfigOption extends BearConfigOption {
    public static final ConfigOption COMMAND_NAME = new ConfigOption("command-settings.name");
    public static final ConfigOption COMMAND_ALIASES = new ConfigOption("command-settings.aliases");
    public static final ConfigOption COMMAND_DESCRIPTION = new ConfigOption("command-settings.description");

    public static final ConfigOption HIDE_COMMANDS = new ConfigOption("hide-commands");

    public static final ConfigOption COMMANDS = new ConfigOption("commands");

    public ConfigOption(String permission) {
        super(CustomHelp.getPlugin(), permission);
    }
}
