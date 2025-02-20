package me.zavdav.zcore.commands

import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.broadcast
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.joinArgs

class CommandBroadcast : ZCoreCommand(
    "broadcast",
    listOf("bc"),
    "Broadcasts a message to all players.",
    "/broadcast <message>",
    "zcore.broadcast",
    minArgs = 1
) {

    override fun execute(event: CommandEvent) {
        broadcast(Config.broadcast, "message" to joinArgs(event.args, 0))
    }
}