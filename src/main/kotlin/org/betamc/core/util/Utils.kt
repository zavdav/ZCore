package org.betamc.core.util

import com.projectposeidon.api.PoseidonUUID
import com.projectposeidon.api.UUIDType
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID

object Utils {

    @JvmStatic fun getPlayerFromUsername(name: String): Player? = Bukkit.matchPlayer(name).getOrNull(0)

    @JvmStatic fun getUUIDFromUsername(name: String): UUID? {
        val player = getPlayerFromUsername(name)
        if (player != null) return player.uniqueId

        return when (PoseidonUUID.getPlayerUUIDCacheStatus(name)) {
            UUIDType.ONLINE -> PoseidonUUID.getPlayerUUIDFromCache(name, true)
            UUIDType.OFFLINE -> PoseidonUUID.getPlayerUUIDFromCache(name, false)
            else -> null
        }
    }
}