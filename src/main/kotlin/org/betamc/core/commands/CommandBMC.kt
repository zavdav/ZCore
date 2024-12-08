package org.betamc.core.commands

import org.betamc.core.BMCCore
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage

class CommandBMC : Command(
    "bmc",
    description = "True and Real!",
    usage = "/bmc",
    permission = "bmc.bmc",
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        val desc = BMCCore.plugin.description
        sendMessage(event.sender, "&e${desc.name} v${desc.version}")
        sendMessage(event.sender, "&eTrue and Real!")
        sendMessage(event.sender, "&eType /help for a list of commands.")
    }
}