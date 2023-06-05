package it.fulminazzo.customhelp.Enums;

import it.angrybear.Enums.BearPermission;
import it.fulminazzo.customhelp.CustomHelp;

public class Permission extends BearPermission {
    public static final Permission BYPASS = new Permission("bypass");
    public static final Permission HELP = new Permission("help");
    public static final Permission GENERAL_PERMISSION = new Permission("bypass.%s");

    public Permission(String permission) {
        super(permission);
    }

    @Override
    public String getPermission() {
        return getPermission(CustomHelp.getPlugin());
    }

    public String getPermission(String commandName) {
        return String.format(getPermission(), commandName);
    }
}
