package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.assert
import org.poseidonplugins.zcore.util.send

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