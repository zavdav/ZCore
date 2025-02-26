package me.zavdav.zcore.commands

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.sendTl
import org.poseidonplugins.commandapi.CommandEvent

class CommandReload : ZCoreCommand(
    "zcore.reload",
    description = "Reloads ZCore.",
    usage = "/zcore reload",
    permission = "zcore.reload",
    maxArgs = 0
) {

    override fun execute(event: CommandEvent) {
        ZCore.INSTANCE.reload()
        val desc = ZCore.INSTANCE.description
        event.sender.sendTl("reloadedPlugin", "plugin" to desc.name, "version" to desc.version)
    }
}