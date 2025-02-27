package me.zavdav.zcore.commands

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.util.getMessage
import me.zavdav.zcore.util.send
import me.zavdav.zcore.util.sendTl
import org.poseidonplugins.commandapi.CommandEvent

class CommandZCore : ZCoreCommand(
    "zcore",
    description = "Displays information about ZCore.",
    usage = "/zcore",
    permission = "zcore.zcore"
) {

    override fun execute(event: CommandEvent) {
        val asciiArt = getMessage("zcoreAsciiArt")
        for (line in asciiArt.split("\n")) {
            event.sender.send(line)
        }
        event.sender.sendTl("zcoreVersion", "version" to ZCore.INSTANCE.description.version)
    }
}