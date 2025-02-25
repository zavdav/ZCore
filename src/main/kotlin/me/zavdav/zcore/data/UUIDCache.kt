package me.zavdav.zcore.data

import com.github.cliftonlabs.json_simple.JsonObject
import me.zavdav.zcore.util.Logger
import me.zavdav.zcore.util.Utils
import java.util.UUID

object UUIDCache : JsonData("uuidcache.json") {

    val nameLookupByUuid: MutableMap<UUID, String> = mutableMapOf()
    val uuidLookupByName: MutableMap<String, UUID> = mutableMapOf()

    init { deserialize() }

    override fun deserialize() {
        for (entry in json.entries) {
            if (!Utils.UUID_PATTERN.matcher(entry.key).matches()) {
                Logger.warning("Found corrupt UUID: ${entry.key}")
                continue
            }
            addEntry(UUID.fromString(entry.key), entry.value.toString())
        }
    }

    override fun serialize() {
        json = JsonObject(nameLookupByUuid.map {
            it.key.toString() to it.value
        }.toMap())
    }

    fun addEntry(uuid: UUID, username: String) {
        nameLookupByUuid[uuid] = username
        uuidLookupByName[username] = uuid
    }

    fun getUUIDFromUsername(username: String, ignoreCase: Boolean = true): UUID? =
        uuidLookupByName[username] ?:
        nameLookupByUuid.entries.firstOrNull { ignoreCase && it.value.equals(username, true) }?.key

    fun getUsernameFromUUID(uuid: UUID): String? = nameLookupByUuid[uuid]
}