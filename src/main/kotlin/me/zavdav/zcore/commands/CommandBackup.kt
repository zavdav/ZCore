package me.zavdav.zcore.commands

import me.zavdav.zcore.commands.core.AbstractCommand
import me.zavdav.zcore.util.Backup
import org.bukkit.command.CommandSender

class CommandBackup : AbstractCommand(
    "zcore backup",
    "Creates a new backup of ZCore's data.",
    "/zcore backup",
    "zcore.backup",
    false,
    maxArgs = 0
) {

    override fun execute(sender: CommandSender, args: List<String>) {
        Backup.run(sender)
    }
}