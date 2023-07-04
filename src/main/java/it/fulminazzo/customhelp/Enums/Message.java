package it.fulminazzo.customhelp.Enums;

import it.angrybear.Objects.Configurations.Configuration;
import it.angrybear.Utils.StringUtils;
import it.fulminazzo.customhelp.CustomHelp;

public enum Message {
    PREFIX("prefix"),

    GO_TO_PREVIOUS_PAGE("general.go-to-previous-page"),
    GO_TO_NEXT_PAGE("general.go-to-next-page"),
    COMMAND_FORMAT("general.command-format"),
    CLICK_TO_TRY("general.click-to-try"),

    COMMAND_NOT_FOUND("errors.command-not-found"),
    NO_PERMISSIONS("errors.no-permissions"),

    HELP_FORMAT("help.format"),
    HELP_PAGE("help.page"),
    HELP_PAGE_FORMAT("help.page-format"),
    HELP_PAGE_SEPARATOR("help.page-separator"),
    HELP_PAGE_PREVIOUS("help.page-previous"),
    HELP_PAGE_NEXT("help.page-next");

    private final String path;
    
    Message(String path) {
        this.path = path;
    }

    public String getMessage(boolean showPrefix) {
        String message = null;
        Configuration lang = CustomHelp.getPlugin().getLang();
        if (lang != null) message = lang.getString(path);
        message = StringUtils.parseMessage(message);
        if (showPrefix) message = Message.PREFIX.getMessage(false) + message;
        return message;
    }
}