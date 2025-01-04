package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.broadcastMessage
import org.poseidonplugins.commandapi.joinArgs
import org.poseidonplugins.zcore.util.formatProperty

class CommandBroadcast : Command(
    "broadcast",
    listOf("bc"),
    "Broadcasts a message to all players.",
    "/broadcast <message>",
    "zcore.broadcast",
    minArgs = 1,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        broadcastMessage(formatProperty("broadcastFormat", "message" to joinArgs(event.args, 0)))
    }
}