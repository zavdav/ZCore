package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.colorize
import me.zavdav.zcore.util.isAuthorized
import me.zavdav.zcore.util.joinArgs
import me.zavdav.zcore.util.kick
import me.zavdav.zcore.util.sendTl
import me.zavdav.zcore.util.tl
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandKickAll : AbstractCommand(
    "kickall",
    "Kicks all players from the server.",
    "/kickall [message]",
    "zcore.kickall",
    false
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val reason = colorize(if (args.isNotEmpty()) joinArgs(args, 0) else tl("kickReason"))

        for (player in Bukkit.getOnlinePlayers()) {
            if (player.isAuthorized("zcore.kick.exempt")) continue
            if (sender !is Player || sender != player) {
                player.kick("kickScreen", reason)
            }
        }
        sender.sendTl("kickedAll", reason)
    }
}