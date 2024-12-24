package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage
import org.poseidonplugins.zcore.ZCore

class CommandZCore : Command(
    "zcore",
    description = "Displays information about ZCore.",
    usage = "/zcore",
    permission = "zcore.zcore",
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val desc = ZCore.plugin.description
        sendMessage(event.sender, "&e${desc.name} v${desc.version}")
        sendMessage(event.sender, "&eType /help for a list of commands.")
    }
}