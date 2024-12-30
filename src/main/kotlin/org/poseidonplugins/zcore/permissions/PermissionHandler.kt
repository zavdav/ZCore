package org.poseidonplugins.zcore.permissions

import org.bukkit.Bukkit
import org.poseidonplugins.zcore.player.ZPlayer

object PermissionHandler {

    private var handler: IPermissionHandler = DefaultPermissionHandler()

    init {
        if (Bukkit.getPluginManager().isPluginEnabled("JPerms")) {
            handler = JohnyPermsHandler()
        }
    }

    fun getPrefix(zPlayer: ZPlayer) = handler.getPrefix(zPlayer.uuid)

    fun getSuffix(zPlayer: ZPlayer) = handler.getSuffix(zPlayer.uuid)
}