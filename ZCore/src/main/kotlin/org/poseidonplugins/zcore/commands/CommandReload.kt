package org.poseidonplugins.zcore.commands

import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.config.Items
import org.poseidonplugins.zcore.config.Kits
import org.poseidonplugins.zcore.util.Backup
import org.poseidonplugins.zcore.util.sendTl

class CommandReload : ZCoreCommand(
    "zcore.reload",
    description = "Reloads ZCore.",
    usage = "/zcore reload",
    permission = "zcore.reload",
    maxArgs = 0
) {

    override fun execute(event: CommandEvent) {
        Config.load()
        Items.load()
        Kits.load()
        Backup.init()
        val desc = ZCore.plugin.description
        event.sender.sendTl("pluginReloaded", "plugin" to desc.name, "version" to desc.version)
    }
}