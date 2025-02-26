package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.user.User
import me.zavdav.zcore.user.UserMap
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getUUIDFromString
import me.zavdav.zcore.util.sendTl
import org.poseidonplugins.commandapi.CommandEvent

class CommandUnban : ZCoreCommand(
    "unban",
    listOf("pardon"),
    "Unbans a player from the server.",
    "/unban <player>",
    "zcore.unban",
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val uuid = getUUIDFromString(event.args[0])
        val name = if (UserMap.isUserKnown(uuid)) User.from(uuid).name else uuid
        assert(Punishments.isPlayerBanned(uuid), "userNotBanned", "user" to name)

        Punishments.unbanPlayer(uuid)
        event.sender.sendTl("unbannedPlayer", "user" to name)
    }
}