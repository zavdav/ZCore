package me.zavdav.zcore.commands

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.sendTl
import org.bukkit.command.CommandSender

class CommandReload : AbstractCommand(
    "zcore reload",
    "Reloads ZCore.",
    "/zcore reload",
    "zcore.reload",
    false,
    maxArgs = 0
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        ZCore.INSTANCE.reload()
        val desc = ZCore.INSTANCE.description
        sender.sendTl("reloadedPlugin", "plugin" to desc.name, "version" to desc.version)
    }
}