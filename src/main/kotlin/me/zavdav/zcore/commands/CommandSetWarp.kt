package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.data.Warps
import me.zavdav.zcore.util.assertOrSend
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandSetWarp : AbstractCommand(
    "setwarp",
    "Sets a new warp at your current location.",
    "/setwarp <name>",
    "zcore.setwarp",
    minArgs = 1,
    maxArgs = 1
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        val warpName = args[0]
        sender.assertOrSend("invalidWarpName") { warpName.matches("^[a-zA-Z0-9_-]+$".toRegex()) }
        sender.assertOrSend("warpAlreadyExists", warpName) { !Warps.warpExists(warpName) }

        Warps.setWarp(warpName, (sender as Player).location)
        sender.sendTl("setWarp", warpName)
    }
}