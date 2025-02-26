package me.zavdav.zcore.hooks.permissions

import me.zavdav.zcore.util.getUsernameFromUUID
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.colorize
import ru.tehkode.permissions.PermissionManager
import ru.tehkode.permissions.bukkit.PermissionsEx
import java.util.UUID

class PermissionsExHook : PermissionHook {

    private val manager: PermissionManager = PermissionsEx.getPermissionManager()

    override fun hasPermission(sender: CommandSender, permission: String): Boolean =
        if (sender !is Player) true else manager.has(sender, permission)

    override fun getPrefix(uuid: UUID): String =
        colorize(manager.getUser(getUsernameFromUUID(uuid))?.prefix ?: "")

    override fun getSuffix(uuid: UUID): String =
        colorize(manager.getUser(getUsernameFromUUID(uuid))?.suffix ?: "")
}