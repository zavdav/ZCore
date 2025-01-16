package org.poseidonplugins.zcore.hooks.permissions

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.poseidonplugins.zcore.player.ZPlayer

object PermissionHandler {

    private var hook: PermissionHook = DefaultPermissionHook()

    init {
        if (Bukkit.getPluginManager().isPluginEnabled("JPerms")) {
            hook = JohnyPermsHook()
        }
    }

    fun hasPermission(sender: CommandSender, permission: String) =
        hook.hasPermission(sender, permission)

    fun getPrefix(zPlayer: ZPlayer) = hook.getPrefix(zPlayer.uuid)

    fun getSuffix(zPlayer: ZPlayer) = hook.getSuffix(zPlayer.uuid)
}