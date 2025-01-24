package org.poseidonplugins.zcore.hooks.permissions

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.poseidonplugins.zcore.user.User

object PermissionHandler {

    private var hook: PermissionHook = DefaultPermissionHook()

    init {
        if (Bukkit.getPluginManager().isPluginEnabled("JPerms")) {
            hook = JohnyPermsHook()
        }
    }

    fun hasPermission(sender: CommandSender, permission: String) =
        hook.hasPermission(sender, permission)

    fun getPrefix(user: User) = hook.getPrefix(user.uuid)

    fun getSuffix(user: User) = hook.getSuffix(user.uuid)
}