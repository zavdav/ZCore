package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.*

class CommandIgnore : ZCoreCommand(
    "ignore",
    description = "Toggles whether or not you ignore a player.",
    usage = "/ignore <player>",
    permission = "zcore.ignore",
    isPlayerOnly = true,
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val user = User.from(event.sender as Player)
        val uuid = Utils.getUUIDFromUsername(event.args[0])
        assert(user.uuid != uuid, "cannotIgnoreSelf")

        if (uuid in user.ignores) {
            user.setIgnored(uuid, false)
            event.sender.sendTl("unignoredPlayer", "name" to User.from(uuid).name)
        } else {
            user.setIgnored(uuid, true)
            event.sender.sendTl("ignoredPlayer", "name" to User.from(uuid).name)
        }
    }
}