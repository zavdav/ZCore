package org.poseidonplugins.zcore.hooks.permissions

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.poseidonplugins.zcore.user.User

object PermissionHandler {

    private var hook: PermissionHook = DefaultPermissionHook()

    init {
        if (Bukkit.getPluginManager().isPluginEnabled("JPerms")) {
            hook = JohnyPermsHook()
        } else if (Bukkit.getPluginManager().isPluginEnabled("PermissionsEx")) {
            hook = PermissionsExHook()
        }
    }

    fun hasPermission(sender: CommandSender, permission: String): Boolean =
        hook.hasPermission(sender, permission)

    fun getPrefix(user: User): String =
        hook.getPrefix(user.uuid)

    fun getSuffix(user: User): String =
        hook.getSuffix(user.uuid)
}