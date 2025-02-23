package me.zavdav.zcore.commands

import me.zavdav.zcore.util.Utils
import me.zavdav.zcore.util.Utils.isSelf
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.kick
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.tl
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.colorize
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.joinArgs

class CommandKick : ZCoreCommand(
    "kick",
    description = "Kicks a player from the server.",
    usage = "/kick <player> [message]",
    permission = "zcore.kick",
    minArgs = 1
) {

    override fun execute(event: CommandEvent) {
        val target = Utils.getPlayerFromUsername(event.args[0])
        if ((event.sender as? Player)?.isSelf(target) != true) {
            assert(!hasPermission(target, "zcore.kick.exempt"), "cannotKickPlayer", "name" to target.name)
        }
        val reason = colorize(if (event.args.size > 1) joinArgs(event.args, 1)
            else tl("kickReason"))

        target.kick("kickScreen", "reason" to reason)
        event.sender.sendTl("kickedPlayer", "player" to target.name, "reason" to reason)
    }
}