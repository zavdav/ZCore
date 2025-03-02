package me.zavdav.zcore.hooks.permissions

import org.bukkit.entity.Player
import java.util.UUID

class DefaultPermissionHook : PermissionHook {

    override fun hasPermission(player: Player, permission: String): Boolean =
        player.isOp || player.hasPermission(permission)

    override fun getPrefix(uuid: UUID): String = ""

    override fun getSuffix(uuid: UUID): String = ""
}