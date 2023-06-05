package it.fulminazzo.customhelp.Objects;

import it.angrybear.Utils.StringUtils;
import it.angrybear.Utils.TextComponentUtils;
import it.fulminazzo.customhelp.Enums.Message;
import it.fulminazzo.customhelp.Enums.Permission;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigCommand {
    private final String name;
    private final String description;
    private final String usage;
    private final String permission;
    private final List<String> aliases;
    private final Boolean blacklist;
    private final Boolean whitelist;
    private final String replacement;
    
    public ConfigCommand(ConfigurationSection commandSection, Command command) {
        String description = command.getDescription();
        String usage = command.getUsage();
        Boolean blacklist = null;
        Boolean whitelist = null;
        String replacement = null;
        if (commandSection != null) {
            String tmp = commandSection.getString("description");
            if (tmp != null) description = tmp;
            tmp = commandSection.getString("usage");
            if (tmp != null) usage = tmp;
            if (commandSection.contains("blacklist"))
                blacklist = commandSection.getBoolean("blacklist");
            if (commandSection.contains("whitelist"))
                whitelist = commandSection.getBoolean("whitelist");
            if (commandSection.contains("replacement"))
                replacement = commandSection.getString("replacement");
        }
        this.name = command.getName();
        this.description = description;
        this.usage = usage;
        this.permission = command.getPermission();
        this.aliases = command.getAliases();
        this.blacklist = blacklist;
        this.whitelist = whitelist;
        this.replacement = replacement;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return StringUtils.parseMessage(description);
    }

    public String getUsage() {
        return StringUtils.parseMessage(usage.startsWith("/") ? usage : ("/" + usage));
    }

    public String getPermission() {
        return permission;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getAliasesToString() {
        return String.join(", ", getAliases());
    }

    public Boolean isBlacklisted() {
        return blacklist != null && blacklist;
    }

    public Boolean isWhitelisted() {
        return whitelist != null && whitelist;
    }

    public boolean canSee(CommandSender sender) {
        return !isBlacklisted() || (sender.hasPermission(Permission.BYPASS.getPermission()) ||
                sender.hasPermission(Permission.GENERAL_PERMISSION.getPermission(getName()))) ||
                isWhitelisted();
    }

    public boolean canExecute(CommandSender sender) {
        return canSee(sender) && ((permission == null || sender.hasPermission(permission)) || isWhitelisted());
    }

    public String replaceCommand(String fullCommand) {
        if (fullCommand == null) return null;
        if (replacement == null) return fullCommand;
        String[] tmp = fullCommand.split(" ");
        String command = tmp[0];
        if (command.startsWith("/")) command = command.substring(1);
        if (!command.equals(getName())) return fullCommand;
        tmp[0] = "/" + replacement;
        return String.join(" ", tmp);
    }

    public TextComponent getHelp() {
        return formatString(Message.HELP_FORMAT.getMessage(false));
    }

    public TextComponent getListElement() {
        return formatString(Message.COMMAND_FORMAT.getMessage(false));
    }

    private TextComponent formatString(String string) {
        string = string
                .replace("%description%", getDescription() == null ? "null" : getDescription())
                .replace("%permission%", getPermission() == null ? "null" : getPermission())
                .replace("%aliases%", getAliasesToString());
        String commandTemplate = "%COMMAND%";
        String usageTemplate = "%USAGE%";
        String[] strings = Arrays.stream(string.replace("%command%", "%command%" + commandTemplate + "%command%").split("%command%"))
                .flatMap(s -> Arrays.stream(s.replace("%usage%", "%usage%" + usageTemplate + "%usage%").split("%usage%")))
                .toArray(String[]::new);
        TextComponent textComponent = new TextComponent();
        String prev = null;
        for (String s : strings) {
            String color = "";
            if (prev != null) color = StringUtils.getCleanChatCodesInString(prev, true).stream().collect(Collectors.joining(StringUtils.getColorCode()));
            TextComponent tmp;
            if (s.equals(commandTemplate)) tmp = new TextComponent(color + getName());
            else if (s.equals(usageTemplate)) tmp = new TextComponent(color + (getUsage() == null ? "null" : getUsage()));
            else tmp = new TextComponent(color + s);
            if (s.equals(commandTemplate) || s.equals(usageTemplate)) {
                tmp.setHoverEvent(TextComponentUtils.getTextHoverEvent(Message.CLICK_TO_TRY.getMessage(false)));
                tmp.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + getName()));
            }
            textComponent.addExtra(tmp);
            prev = color + s;
        }
        return textComponent;
    }
}