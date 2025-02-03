package org.poseidonplugins.zimport.hooks.plugins

import com.github.cliftonlabs.json_simple.JsonObject
import com.johnymuffin.beta.fundamentals.Fundamentals
import com.johnymuffin.beta.fundamentals.player.FundamentalsPlayer
import com.johnymuffin.beta.fundamentals.playerdata.FundamentalsPlayerFile
import org.poseidonplugins.zimport.util.unixToDateTime
import java.time.LocalDateTime
import java.util.*

class FundamentalsHook(private val fundamentals: Fundamentals) : CorePluginHook {

    private lateinit var userMap: Map<UUID, FundamentalsPlayer>

    override fun getKnownUsers(): Set<UUID> {
        if (!this::userMap.isInitialized) {
            val fPlayerMap = fundamentals.playerMap
            userMap = fPlayerMap.knownPlayers.associate { it!! to fPlayerMap.getPlayer(it)!! }
        }
        return userMap.keys
    }

    override fun getUsername(uuid: UUID): String =
        fundamentals.playerCache.getUsernameFromUUID(uuid) ?: "null"

    @Suppress("DEPRECATION")
    override fun getBalance(uuid: UUID): Double =
        userMap[uuid]?.balance ?: 0.0

    @Suppress("UNCHECKED_CAST")
    override fun getHomes(uuid: UUID): JsonObject {
        val user = userMap[uuid] ?: return JsonObject()
        val method = FundamentalsPlayerFile::class.java.getDeclaredMethod("getPlayerHomeJsonData", String::class.java)
        method.isAccessible = true
        val homes = user.playerHomes.associateWith { method.invoke(user, it) as Map<String, *> }
        method.isAccessible = false
        return JsonObject(homes)
    }

    override fun isBanned(uuid: UUID): Boolean = false

    override fun getBanExpiry(uuid: UUID): LocalDateTime = unixToDateTime(0L)

    override fun isMuted(uuid: UUID): Boolean = false

    override fun getMuteExpiry(uuid: UUID): LocalDateTime = unixToDateTime(0L)
}