package me.zavdav.zcore.commands

import me.zavdav.zcore.data.Punishments
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.Utils
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.sendTl
import org.poseidonplugins.commandapi.CommandEvent

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
        val user = User.from(uuid)
        assert(Punishments.isMuted(uuid), "userNotMuted", "user" to user.name)
        Punishments.unmute(uuid)
        event.sender.sendTl("unmutedPlayer", "user" to user.name)
    }
}