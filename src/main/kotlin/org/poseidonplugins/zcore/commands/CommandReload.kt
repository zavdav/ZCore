package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.Command
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.sendMessage
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.util.format

class CommandReload : Command(
    "zcore.reload",
    description = "Reloads ZCore.",
    usage = "/zcore reload",
    permission = "zcore.reload",
    maxArgs = 0,
    preprocessor = Preprocessor()) {

    override fun execute(event: CommandEvent) {
        Config.load()

        val desc = ZCore.plugin.description
        sendMessage(event.sender, format("pluginReloaded",
            "plugin" to desc.name,
            "version" to desc.version))
    }
}