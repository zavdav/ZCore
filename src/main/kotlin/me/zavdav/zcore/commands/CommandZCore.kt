package me.zavdav.zcore.commands

import me.zavdav.zcore.ZCore
import org.poseidonplugins.commandapi.CommandEvent

class CommandZCore : ZCoreCommand(
    "zcore",
    description = "Displays information about ZCore.",
    usage = "/zcore",
    permission = "zcore.zcore"
) {

    override fun execute(event: CommandEvent) {
        val desc = ZCore.INSTANCE.description
        event.sender.sendMessage("§e${desc.name} v${desc.version}")
        event.sender.sendMessage("§eType /help for a list of commands.")
    }
}