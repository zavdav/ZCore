package org.betamc.core.commands

import org.betamc.core.config.Property
import org.betamc.core.util.formatError
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage

class CommandRules : Command(
    "rules",
    description = "Shows the server's rules.",
    usage = "/rules",
    permission = "bmc.rules",
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        if (Property.RULES.toString().isEmpty()) {
            sendMessage(event.sender, formatError("noRulesSet"))
            return
        }
        val rules = Property.RULES.toList()
        for (line in rules) {
            sendMessage(event.sender, line)
        }
    }
}