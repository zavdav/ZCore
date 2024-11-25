package org.betamc.core.commands

import org.betamc.core.config.Property
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.broadcastMessage
import org.poseidonplugins.commandapi.joinArgs

class CommandBroadcast : Command(
    "broadcast",
    listOf("bc"),
    "Broadcasts a message to all players.",
    "/broadcast <message>",
    "bmc.broadcast",
    minArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        broadcastMessage(Property.BROADCAST_FORMAT.toString()
            .replace("%message%", joinArgs(event.args, 0)))
    }
}