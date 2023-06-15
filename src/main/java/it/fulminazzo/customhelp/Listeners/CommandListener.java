package it.fulminazzo.customhelp.Listeners;

import it.fulminazzo.customhelp.CustomHelp;
import it.fulminazzo.customhelp.Enums.ConfigOption;
import it.fulminazzo.customhelp.Enums.Message;
import it.fulminazzo.customhelp.Enums.Permission;
import it.fulminazzo.customhelp.Objects.ConfigCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.PluginEnableEvent;

public class CommandListener implements Listener {
    private final CustomHelp plugin;

    public CommandListener(CustomHelp plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPluginLoad(PluginEnableEvent event) {
        plugin.getCommandsManager().reloadCommands();
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) return;
        Player player = event.getPlayer();
        String command = event.getMessage().split(" ")[0];
        if (command.startsWith("/")) command = command.substring(1);
        ConfigCommand configCommand = plugin.getCommandsManager().getCommand(command);
        if (player.hasPermission(Permission.BYPASS.getPermission()) || player.hasPermission(Permission.BYPASS.getPermission(command))) return;
        if (configCommand == null || !configCommand.canSee(player)) {
            player.sendMessage(Message.COMMAND_NOT_FOUND.getMessage(false).replace("%command%", command));
            event.setCancelled(true);
            return;
        }
        if (!configCommand.canExecute(player)) {
            if (ConfigOption.HIDE_COMMANDS.getBoolean())
                player.sendMessage(Message.COMMAND_NOT_FOUND.getMessage(false).replace("%command%", command));
            else
                player.sendMessage(Message.NO_PERMISSIONS.getMessage(false).replace("%command%", command));
            event.setCancelled(true);
            return;
        }
        event.setMessage(configCommand.replaceCommand(event.getMessage()));
    }
}
