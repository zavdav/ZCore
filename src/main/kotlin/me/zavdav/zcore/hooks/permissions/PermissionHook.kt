package me.zavdav.zcore.hooks.permissions

import org.bukkit.command.CommandSender
import java.util.*

interface PermissionHook {

    fun hasPermission(sender: CommandSender, permission: String): Boolean

    fun getPrefix(uuid: UUID): String

    fun getSuffix(uuid: UUID): String
}