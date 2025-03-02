package me.zavdav.zcore.hooks.permissions

import me.zavdav.zcore.util.colorize
import me.zavdav.zcore.util.getUsernameFromUUID
import org.bukkit.entity.Player
import ru.tehkode.permissions.PermissionManager
import ru.tehkode.permissions.bukkit.PermissionsEx
import java.util.UUID

class PermissionsExHook : PermissionHook {

    private val manager: PermissionManager = PermissionsEx.getPermissionManager()

    override fun hasPermission(player: Player, permission: String): Boolean =
        player.isOp || manager.has(player, permission)

    override fun getPrefix(uuid: UUID): String =
        colorize(manager.getUser(getUsernameFromUUID(uuid))?.prefix ?: "")

    override fun getSuffix(uuid: UUID): String =
        colorize(manager.getUser(getUsernameFromUUID(uuid))?.suffix ?: "")
}