package org.betamc.core.config

import org.betamc.core.BMCCore

enum class Language(val default: String) {

    NO_PERMISSION("&cYou don't have permission to do that."),
    PLAYER_ONLY("&cOnly players can run this command."),
    PLAYER_NOT_FOUND("&cError: Could not find player {0}"),

    BAN_SUCCESS("&c{0} has been banned {1}{2}"),
    BANIP_SUCCESS("&cThe IP {0} has been banned {1}{2}"),
    DELHOME_SUCCESS("&6{0} home &b{1} &6has been deleted"),
    GOD_TOGGLE("&b{0} god mode has been {1}"),
    HEAL_SUCCESS("&a{0} been healed"),
    HELP_HEADER("&eHelp: Page &6{0} &eof &6{1}"),
    HELP_COMMAND("&6{0}: &7{1}"),
    HOME_NOT_SPECIFIED("&cError: No home specified"),
    HOME_DOES_NOT_EXIST("&cError: That home does not exist"),
    HOME_SUCCESS("&6You have been teleported to {0} home &b{1}"),
    HOMES_HEADER("&6Homes: Page &b{0} &6of &b{1}"),
    HOMES_ENTRY("&b{0}, "),
    INVSEE_SUCCESS("&6You are now looking at {0}''s inventory"),
    INVSEE_RESTORED("&6Your inventory has been restored"),
    KICK_SUCCESS("&c{0} has been kicked, reason: {1}"),
    KICKALL_SUCCESS("&cAll players have been kicked, reason: {0}"),
    LIST_HEADER("&eList: &6{0} &eof max. &6{1} &eplayers online"),
    MOTD_NOT_SET("&cError: Message of the day has not been set"),
    NO_MATCHING_RESULTS("&cError: No matching results"),
    PAGE_TOO_HIGH("&cError: Page number too high"),
    RELOAD_SUCCESS("&e{0} {1} has been reloaded."),
    SEEN_ONLINE("&e{0} been online for {1}"),
    SEEN_OFFLINE("&e{0} was last online {1} ago"),
    SETHOME_INVALID_NAME("&cError: Home name must only contains characters A-Z,0-9,_,-"),
    SETHOME_MAXIMUM("&cError: You cannot set more than {0} {1}"),
    SETHOME_HOME_EXISTS("&cError: There is already a home with this name"),
    SETHOME_SUCCESS("&6Your home &b{0} &6has been set"),
    SETSPAWN_SUCCESS("&bThe spawn point of &e{0} &bhas been set to &e{1}"),
    SETSPAWN_RESET("&bThe spawn point of &e{0} &bhas been reset"),
    SPAWN_SUCCESS("&bYou have been teleported to the spawn point of &e{0}"),
    TP_SUCCESS("&d{0} been teleported to {1}"),
    TP_PARSE_ERROR("&cError: Could not parse \"{0}\" into coordinates"),
    UNBAN_NOT_BANNED("&cError: This user is not banned"),
    UNBAN_SUCCESS("&aYou have unbanned {0}"),
    UNBANIP_NOT_BANNED("&cError: This IP is not banned"),
    UNBANIP_SUCCESS("&aYou have unbanned the IP {0}"),
    UNSAFE_DESTINATION("&cError: The teleport destination is unsafe"),
    VANISH_TOGGLE("&7{0} been {1}");

    override fun toString(): String =
        (BMCCore.language.getProperty(name) ?: default).toString()
}