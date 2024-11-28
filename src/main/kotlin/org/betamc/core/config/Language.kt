package org.betamc.core.config

enum class Language(var msg: String) {

    NO_PERMISSION("&cYou don't have permission to do that."),
    PLAYER_ONLY("&cOnly players can run this command."),
    PLAYER_NOT_FOUND("&cError: Could not find player &6%player%"),

    HEAL_SELF("&aYou have been healed"),
    HEAL_PLAYER("&aHealed %player%"),
    HELP_HEADER("&eHelp: Page &6%page% &eof &6%pages%"),
    HELP_COMMAND("&6%command%: &7%description%"),
    HELP_NO_RESULTS("&cError: No matching results"),
    HELP_PAGE_TOO_HIGH("&cError: Page number too high"),
    KICK_MESSAGE_BROADCAST("&c%sender% kicked %player%: &f%message%"),
    KICK_DEFAULT_MESSAGE("Kicked from server"),
    LIST_HEADER("&eThere are &6%count% &eout of &6%max% &eplayers online."),
    LIST_PLAYERS("&7Online players: &f%list%");

    override fun toString(): String = msg
}