package org.betamc.core.commands

import org.betamc.core.BMCCore
import org.betamc.core.config.Language
import org.betamc.core.util.Utils
import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage

class CommandReload : Command(
    "bmc.reload",
    description = "Reloads BMC-Core.",
    usage = "/bmc reload",
    permission = "bmc.reload",
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        (BMCCore.plugin as BMCCore).initConfig()

        val desc = BMCCore.plugin.description
        sendMessage(event.sender, Utils.format(Language.RELOAD_SUCCESS, desc.name, desc.version))
    }
}