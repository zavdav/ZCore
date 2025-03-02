package me.zavdav.zcore.hooks.permissions

import org.bukkit.entity.Player
import java.util.UUID

interface PermissionHook {

    fun hasPermission(player: Player, permission: String): Boolean

    fun getPrefix(uuid: UUID): String

    fun getSuffix(uuid: UUID): String
}