package it.fulminazzo.customhelp.Managers;

import it.angrybear.Utils.CommandUtils;
import it.fulminazzo.customhelp.Enums.ConfigOptions;
import it.fulminazzo.customhelp.Objects.ConfigCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.stream.Collectors;

public class CommandsManager {
    private final List<ConfigCommand> commands;

    public CommandsManager() {
        this.commands = new ArrayList<>();
    }

    public void reloadCommands() {
        this.commands.clear();
        ConfigurationSection commandsSection = ConfigOptions.COMMANDS.getSection();
        // First get plugin commands.
        List<Command> commands = Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .flatMap(p -> p.getDescription().getCommands().keySet().stream())
                .map(Bukkit::getPluginCommand)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        commands.stream()
                .map(c -> new ConfigCommand(commandsSection == null ? null :
                        commandsSection.getConfigurationSection(c.getName()), c))
                .forEach(this.commands::add);

        // Then get missing commands.
        HashMap<String, Command> knownCommands = CommandUtils.getKnownCommands().getObject();
        if (knownCommands != null) {
            knownCommands.values().stream()
                    .filter(c -> this.commands.stream().noneMatch(cc -> cc.getName().equals(c.getName())))
                    .map(c -> new ConfigCommand(commandsSection == null ? null :
                            commandsSection.getConfigurationSection(c.getName()), c))
                    .forEach(this.commands::add);
        }
    }

    public ConfigCommand getCommand(String name) {
        if (name == null) return null;
        if (name.contains(":")) {
            String[] tmp = name.split(":");
            name = String.join(":", Arrays.copyOfRange(tmp, 1, tmp.length));
        }
        String finalName = name;
        return this.commands.stream().filter(c -> c.getName().equalsIgnoreCase(finalName) ||
                c.getAliases().stream().anyMatch(a -> a.equalsIgnoreCase(finalName)))
                .findAny().orElse(null);
    }

    public List<ConfigCommand> getExecutableCommands(CommandSender sender) {
        return getExecutableCommands(sender, "", false);
    }

    public List<ConfigCommand> getExecutableCommands(CommandSender sender, String name) {
        return getExecutableCommands(sender, name, false);
    }

    public List<ConfigCommand> getExecutableCommands(CommandSender sender, String name, boolean strict) {
        if (name == null) return new ArrayList<>();
        return this.commands.stream()
                .filter(c -> strict ?
                        (c.getName().equalsIgnoreCase(name) || c.getAliases().stream().anyMatch(a -> a.equalsIgnoreCase(name)))
                        : (c.getName().toLowerCase().contains(name.toLowerCase()) ||
                                c.getAliases().stream().anyMatch(a -> a.toLowerCase().contains(name.toLowerCase()))))
                .filter(c -> c.canExecute(sender))
                .collect(Collectors.toList());
    }
}