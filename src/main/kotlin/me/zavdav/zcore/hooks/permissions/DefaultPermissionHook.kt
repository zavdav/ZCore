package me.zavdav.zcore.hooks.permissions

import org.bukkit.command.CommandSender
import java.util.*

class DefaultPermissionHook : PermissionHook {

    override fun hasPermission(sender: CommandSender, permission: String): Boolean =
        sender.isOp || sender.hasPermission(permission)

    override fun getPrefix(uuid: UUID): String = ""

    override fun getSuffix(uuid: UUID): String = ""
}