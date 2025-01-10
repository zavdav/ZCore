package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.sendErrTl

class CommandRules : Command(
    "rules",
    description = "Shows the server's rules.",
    usage = "/rules",
    permission = "zcore.rules",
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        if (Config.isEmpty("rules")) {
            event.sender.sendErrTl("noRulesSet")
            return
        }

        for (line in Config.getList("rules")) {
            event.sender.sendMessage(line)
        }
    }
}