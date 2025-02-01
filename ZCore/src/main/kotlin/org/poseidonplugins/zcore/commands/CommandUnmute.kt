package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.data.Punishments
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.util.Utils
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.sendTl

class CommandUnmute : ZCoreCommand(
    "unmute",
    description = "Unmutes a player, allowing them to chat again.",
    usage = "/unmute <player>",
    permission = "zcore.unmute",
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val uuid = Utils.getUUIDFromUsername(event.args[0])
        assert(Punishments.isMuted(uuid), "userNotMuted")
        Punishments.unmute(uuid)
        event.sender.sendTl("userUnmuted", "user" to User.from(uuid).name)
    }
}