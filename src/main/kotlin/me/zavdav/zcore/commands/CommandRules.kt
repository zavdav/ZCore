package me.zavdav.zcore.commands

import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.assert
import me.zavdav.zcore.util.send
import org.poseidonplugins.commandapi.CommandEvent

class CommandRules : ZCoreCommand(
    "rules",
    description = "Shows the server's rules.",
    usage = "/rules",
    permission = "zcore.rules",
    maxArgs = 0
) {

    override fun execute(event: CommandEvent) {
        assert(Config.rules.isNotEmpty(), "noRules")
        for (line in Config.rules) {
            event.sender.send(line)
        }
    }
}