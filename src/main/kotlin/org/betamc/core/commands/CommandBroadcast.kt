package org.betamc.core.commands

import org.betamc.core.config.Property
import org.betamc.core.util.format
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
        broadcastMessage(format(Property.BROADCAST_FORMAT,
            "message" to joinArgs(event.args, 0)))
    }
}