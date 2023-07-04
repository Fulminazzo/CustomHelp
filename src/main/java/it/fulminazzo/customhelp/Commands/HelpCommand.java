package it.fulminazzo.customhelp.Commands;

import it.angrybear.Bukkit.Commands.BearCommand;
import it.angrybear.Utils.NumberUtils;
import it.angrybear.Utils.StringUtils;
import it.angrybear.Utils.TextComponentUtils;
import it.fulminazzo.customhelp.CustomHelp;
import it.fulminazzo.customhelp.Enums.ConfigOption;
import it.fulminazzo.customhelp.Enums.Message;
import it.fulminazzo.customhelp.Enums.Permission;
import it.fulminazzo.customhelp.Managers.CommandsManager;
import it.fulminazzo.customhelp.Objects.ConfigCommand;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class HelpCommand extends BearCommand<CustomHelp> {

    public HelpCommand(CustomHelp plugin, String commandName, List<String> aliases) {
        super(plugin, commandName, Permission.HELP, ConfigOption.COMMAND_DESCRIPTION.getString(),
                String.format("/%s <command>", commandName), aliases.toArray(new String[0]));
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        int chatSize = 50;
        CommandsManager commandsManager = getPlugin().getCommandsManager();
        List<ConfigCommand> commandsDescriptions;
        int pageIndex;
        if (args.length == 0 || NumberUtils.isNatural(args[0])) {
            commandsDescriptions = commandsManager.getExecutableCommands(sender);
            pageIndex = args.length > 0 ? 0 : -1;

            if (commandsDescriptions.isEmpty()) {
                sender.sendMessage(Message.NO_PERMISSIONS.getMessage(true));
                return true;
            }
        } else {
            String argument = args[0];
            commandsDescriptions = commandsManager.getExecutableCommands(sender, args[0], true);
            if (commandsDescriptions.isEmpty())
                commandsDescriptions = commandsManager.getExecutableCommands(sender, args[0]);
            pageIndex = (args.length > 1 && !NumberUtils.isNatural(argument)) ? 1 : -1;

            if (commandsDescriptions.isEmpty()) {
                sender.sendMessage(Message.COMMAND_NOT_FOUND.getMessage(true)
                        .replace("%command%", argument));
                return true;
            }
        }

        int helpPerPage = 7;
        int page = 0;
        if (pageIndex != -1) page = Math.max(Integer.parseInt(args[pageIndex]) - 1, 0);
        int maxPages = commandsDescriptions.size() / helpPerPage + (commandsDescriptions.size() % helpPerPage == 0 ? 0 : 1);
        page = Math.min(maxPages - 1, page);

        String helpPageMessage = Message.HELP_PAGE.getMessage(false)
                .replace("%plugin-name%", getPlugin().getName());
        if (Message.HELP_PAGE_SEPARATOR.getMessage(false).contains(String.valueOf(ChatColor.BOLD))) chatSize = 48;
        
        String separator = StringUtils.repeatChar(Message.HELP_PAGE_SEPARATOR.getMessage(false),
                (chatSize - ChatColor.stripColor(helpPageMessage).length()) / 2 - 1);
        
        // Send header
        sender.sendMessage(separator + helpPageMessage + separator);

        if (commandsDescriptions.size() == 1) {
            sender.spigot().sendMessage(commandsDescriptions.get(0).getListElement());
            return true;
        }

        // Send Commands
        commandsDescriptions.subList(page * helpPerPage, Math.min((page + 1) * helpPerPage, commandsDescriptions.size()))
                .stream()
                .sorted(Comparator.comparing(ConfigCommand::getName))
                .forEach(c -> sender.spigot().sendMessage(c.getHelp()));

        int leftSize = 0;
        int rightSize = 0;
        String singleSeparator = Message.HELP_PAGE_SEPARATOR.getMessage(false);
        String helpPageFormat = Message.HELP_PAGE_FORMAT.getMessage(false)
                .replace("%page%", String.valueOf(page + 1))
                .replace("%max-page%", String.valueOf(maxPages))
                .replace("%page-separator%", singleSeparator);
        String previous = Message.HELP_PAGE_PREVIOUS.getMessage(false)
                .replace("%page-separator%", singleSeparator);
        String next = Message.HELP_PAGE_NEXT.getMessage(false)
                .replace("%page-separator%", singleSeparator);
        String tempHelpPageFormat = helpPageFormat;
        if (page > 0) {
            helpPageFormat = previous.concat(helpPageFormat);
            leftSize = ChatColor.stripColor(previous).length();
        }
        if (page < maxPages - 1) {
            helpPageFormat = helpPageFormat.concat(next);
            rightSize = ChatColor.stripColor(next).length();
        }
        chatSize = (chatSize - ChatColor.stripColor(helpPageFormat).length()) / 2;
        String leftSeparator = StringUtils.repeatChar(Message.HELP_PAGE_SEPARATOR.getMessage(false),
                chatSize - (leftSize / 2) + (rightSize / 2));
        String rightSeparator = StringUtils.repeatChar(Message.HELP_PAGE_SEPARATOR.getMessage(false),
                chatSize - (rightSize / 2) + (leftSize / 2));
        TextComponent component = new TextComponent(leftSeparator);

        // Previous Page
        if (page > 0) {
            TextComponent previousComponent = new TextComponent(previous);
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/%s %d", label, page));
            previousComponent.setClickEvent(clickEvent);
            previousComponent.setHoverEvent(TextComponentUtils.getTextHoverEvent(Message.GO_TO_PREVIOUS_PAGE.getMessage(false)));
            component.addExtra(previousComponent);
        }
        component.addExtra(tempHelpPageFormat);

        // NextPage
        if (page < maxPages - 1) {
            TextComponent afterComponent = new TextComponent(next);
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/%s %d", label, page + 2));
            afterComponent.setClickEvent(clickEvent);
            afterComponent.setHoverEvent(TextComponentUtils.getTextHoverEvent(Message.GO_TO_NEXT_PAGE.getMessage(false)));
            component.addExtra(afterComponent);
        }

        component.addExtra(rightSeparator);
        if (sender instanceof Player) ((Player) sender).spigot().sendMessage(component);
        else sender.sendMessage(component.toLegacyText());
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String cmd, String[] args) throws IllegalArgumentException {
        List<String> list = new ArrayList<>();
        if (args.length == 1)
            list = getPlugin().getCommandsManager().getExecutableCommands(sender, args[0]).stream()
                .map(ConfigCommand::getName).collect(Collectors.toList());
        return StringUtil.copyPartialMatches(args[args.length - 1], list, new ArrayList<>());
    }
}
