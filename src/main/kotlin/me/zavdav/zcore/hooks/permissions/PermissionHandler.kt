package me.zavdav.zcore.hooks.permissions

import me.zavdav.zcore.user.User
import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PermissionHandler {

    private var hook: PermissionHook = DefaultPermissionHook()

    init {
        if (Bukkit.getPluginManager().isPluginEnabled("JPerms")) {
            hook = JohnyPermsHook()
        } else if (Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")) {
            hook = PermissionsExHook()
        }
    }

    fun hasPermission(player: Player, permission: String): Boolean =
        hook.hasPermission(player, permission)

    fun getPrefix(user: User): String =
        hook.getPrefix(user.uuid)

    fun getSuffix(user: User): String =
        hook.getSuffix(user.uuid)
}