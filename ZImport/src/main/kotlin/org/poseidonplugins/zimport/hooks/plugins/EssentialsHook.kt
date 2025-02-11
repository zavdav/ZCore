package org.poseidonplugins.zimport.hooks.plugins

import com.earth2me.essentials.Essentials
import com.earth2me.essentials.User
import com.earth2me.essentials.UserData
import com.github.cliftonlabs.json_simple.JsonObject
import com.projectposeidon.api.PoseidonUUID
import com.projectposeidon.johnymuffin.UUIDManager
import org.poseidonplugins.zimport.util.Logger
import org.poseidonplugins.zimport.util.unixToDateTime
import java.time.LocalDateTime
import java.util.*

class EssentialsHook(private val essentials: Essentials) : CorePluginHook {

    private lateinit var userMap: MutableMap<UUID, User>
    private lateinit var uuidCache: List<Map<String, *>>

    override fun getKnownUsers(): Set<UUID> {
        if (!this::userMap.isInitialized) {
            userMap = mutableMapOf()
            for (user in essentials.userMap.allUsers) {
                val uuid = getUserUUID(user.name)
                if (uuid == null) {
                    Logger.warning("UUID of player ${user.name} could not be found.")
                    continue
                }
                userMap[uuid] = user
            }
        }
        return userMap.keys
    }

    override fun getUsername(uuid: UUID): String =
        PoseidonUUID.getPlayerUsernameFromUUID(uuid) ?: userMap[uuid]?.name ?: "null"

    override fun getBalance(uuid: UUID): Double =
        userMap[uuid]?.money ?: 0.0

    @Suppress("UNCHECKED_CAST")
    override fun getHomes(uuid: UUID): JsonObject {
        val user = userMap[uuid] ?: return JsonObject()
        val method = UserData::class.java.getDeclaredMethod("_getHomes")
        method.isAccessible = true
        var homes = method.invoke(user) as Map<String, Any>
        method.isAccessible = false

        homes = homes.mapValues { JsonObject(it.value as Map<String, *>) }
        return JsonObject(homes)
    }

    override fun isBanned(uuid: UUID): Boolean =
        userMap[uuid]?.isBanned == true

    override fun getBanExpiry(uuid: UUID): LocalDateTime {
        val banTimeout = userMap[uuid]?.banTimeout ?: return unixToDateTime(0L)
        return if (banTimeout == 0L) LocalDateTime.MAX else unixToDateTime(banTimeout)
    }

    override fun isMuted(uuid: UUID): Boolean =
        userMap[uuid]?.isMuted == true

    override fun getMuteExpiry(uuid: UUID): LocalDateTime =
        unixToDateTime(userMap[uuid]?.muteTimeout ?: 0L)

    @Suppress("UNCHECKED_CAST")
    private fun getUserUUID(username: String): UUID? {
        if (!this::uuidCache.isInitialized) {
            val field = UUIDManager::class.java.getDeclaredField("UUIDJsonArray")
            field.isAccessible = true
            uuidCache = field.get(UUIDManager.getInstance()) as List<Map<String, *>>
            field.isAccessible = false
        }

        for (entry in uuidCache) {
            val cachedName = entry["name"]?.toString()
            if (cachedName != null && username.equals(cachedName, true)) {
                return UUID.fromString(entry["uuid"].toString())
            }
        }
        return null
    }
}