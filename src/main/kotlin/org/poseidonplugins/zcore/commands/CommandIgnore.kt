package org.poseidonplugins.zcore.commands

import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.*

class CommandIgnore : Command(
    "ignore",
    description = "Makes you ignore a player.",
    usage = "/ignore <player>",
    permission = "zcore.ignore",
    isPlayerOnly = true,
    minArgs = 1,
    maxArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val user = User.from(event.sender as Player)
        val uuid = Utils.getUUIDFromUsername(event.args[0])
        assert(user.uuid != uuid, "cannotIgnoreSelf")

        if (uuid in user.ignores) {
            user.setIgnored(uuid, false)
            event.sender.sendTl("notIgnoringPlayer", "name" to User.from(uuid).name)
        } else {
            user.setIgnored(uuid, true)
            event.sender.sendTl("ignoringPlayer", "name" to User.from(uuid).name)
        }
    }
}