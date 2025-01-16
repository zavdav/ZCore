package org.poseidonplugins.zcore.hooks.permissions

import org.bukkit.command.CommandSender
import java.util.UUID

interface PermissionHook {

    fun hasPermission(sender: CommandSender, permission: String): Boolean

    fun getPrefix(uuid: UUID): String

    fun getSuffix(uuid: UUID): String
}