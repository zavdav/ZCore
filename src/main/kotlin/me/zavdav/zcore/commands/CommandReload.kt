package me.zavdav.zcore.commands

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.config.Items
import me.zavdav.zcore.config.Kits
import me.zavdav.zcore.util.Backup
import me.zavdav.zcore.util.sendTl
import org.poseidonplugins.commandapi.CommandEvent

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
        Backup.load()
        val desc = ZCore.plugin.description
        event.sender.sendTl("reloadedPlugin", "plugin" to desc.name, "version" to desc.version)
    }
}