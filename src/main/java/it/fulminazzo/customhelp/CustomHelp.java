package it.fulminazzo.customhelp;

import it.angrybear.BearPlugin;
import it.angrybear.Objects.BearPlayer;
import it.fulminazzo.customhelp.Commands.HelpCommand;
import it.fulminazzo.customhelp.Enums.ConfigOptions;
import it.fulminazzo.customhelp.Listeners.CommandListener;
import it.fulminazzo.customhelp.Managers.CommandsManager;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CustomHelp extends BearPlugin<BearPlayer, BearPlayer> {
    private static CustomHelp plugin;
    private HelpCommand helpCommand;
    private CommandsManager commandsManager;

    @Override
    public void onEnable() {
        plugin = this;
        super.onEnable();
        if (isEnabled())
            Bukkit.getPluginManager().registerEvents(new CommandListener(this), this);
    }

    @Override
    public void loadAll() throws Exception {
        super.loadAll();
        loadCommand();
        this.commandsManager = new CommandsManager();
    }

    public void unloadAll() throws Exception {
        super.unloadAll();
        unloadCommand();
    }

    public void loadCommand() throws Exception {
        unloadCommand();
        String name = ConfigOptions.COMMAND_NAME.getString();
        if (name == null) throw new Exception("Command name cannot be null!");
        List<String> aliases = ConfigOptions.COMMAND_ALIASES.getStringList();
        if (aliases == null) aliases = new ArrayList<>();
        helpCommand = new HelpCommand(this, name, aliases);
        helpCommand.loadCommand();
    }

    public void unloadCommand() {
        if (helpCommand != null) helpCommand.unloadCommand();
    }

    @Override
    public void loadConfig() throws Exception {
        super.loadConfig();
        // HalpMePls retro-compatibility.
        List<String> ignoredCommands = getConfig().getStringList("ignored-commands");
        if (ignoredCommands == null) return;
        logInfo(String.format("Found ignored-commands list (%s). Converting to new format...", ignoredCommands.size()));
        ignoredCommands.forEach(c -> getConfig().set(String.format("commands.%s.blacklist", c), true));
        getConfig().set("ignored-commands", null);
        File configFile = new File(getDataFolder(), "config.yml");
        getConfig().save(configFile);
        logInfo(String.format("Saving to file %s", configFile.getPath()));
    }

    public CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public static CustomHelp getPlugin() {
        return plugin;
    }
}
