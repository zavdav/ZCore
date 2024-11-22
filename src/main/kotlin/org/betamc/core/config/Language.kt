package org.betamc.core.config

enum class Language(var msg: String) {

    NO_PERMISSION("&cYou don't have permission to do that."),
    PLAYER_ONLY("&cOnly players can run this command."),

    HELP_HEADER("&eHelp: Page &6%page% &eof &6%pages%"),
    HELP_COMMAND("&6%command%: &7%description%"),
    HELP_NO_RESULTS("&cError: No matching results"),
    HELP_PAGE_TOO_HIGH("&cError: Page number too high"),
    LIST_HEADER("&eThere are &6%count% &eout of &6%max% &eplayers online."),
    LIST_PLAYERS("&7Online players: &f%list%");

    override fun toString(): String = msg
}