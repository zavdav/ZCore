package org.betamc.core.commands

import org.betamc.core.BMCCore
import org.betamc.core.util.format
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
        sendMessage(event.sender, format("pluginReloaded",
            "plugin" to desc.name,
            "version" to desc.version))
    }
}