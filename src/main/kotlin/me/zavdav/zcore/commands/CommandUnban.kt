package me.zavdav.zcore.commands

import me.zavdav.zcore.api.Punishments
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.user.User
import me.zavdav.zcore.user.UserMap
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.getUUIDFromString
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender

class CommandUnban : AbstractCommand(
    "unban",
    "Unbans a player from the server.",
    "/unban <player>",
    "zcore.unban",
    false,
    1,
    1,
    listOf("pardon")
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val uuid = getUUIDFromString(args[0])
        val name = if (UserMap.isUserKnown(uuid)) User.from(uuid).name else uuid
        assert(Punishments.isPlayerBanned(uuid), "userNotBanned", name)

        Punishments.unbanPlayer(uuid)
        sender.sendTl("unbannedPlayer", name)
    }
}