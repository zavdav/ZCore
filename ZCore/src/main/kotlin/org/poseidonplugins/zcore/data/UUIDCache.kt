package org.poseidonplugins.zcore.data

import com.github.cliftonlabs.json_simple.Jsoner
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.util.Logger
import org.poseidonplugins.zcore.util.Utils
import java.io.File
import java.io.FileWriter
import java.util.UUID

object UUIDCache : JsonData(File(ZCore.dataFolder, "uuidcache.json")) {

    val nameLookupByUuid: MutableMap<UUID, String> = LinkedHashMap()
    val uuidLookupByName: MutableMap<String, UUID> = LinkedHashMap()

    @Synchronized
    fun load() {
        for (entry in json.entries) {
            if (!Utils.UUID_PATTERN.matcher(entry.key).matches()) {
                Logger.warning("Found corrupt UUID: ${entry.key}")
                continue
            }
            addEntry(UUID.fromString(entry.key), entry.value.toString())
        }
    }

    @Synchronized
    fun addEntry(uuid: UUID, username: String) {
        nameLookupByUuid[uuid] = username
        uuidLookupByName[username] = uuid
    }

    @Synchronized
    fun getUUIDFromUsername(username: String, ignoreCase: Boolean = true): UUID? =
        uuidLookupByName[username] ?:
        nameLookupByUuid.entries.firstOrNull { ignoreCase && it.value.equals(username, true) }?.key

    @Synchronized
    override fun saveData() {
        if (hashCode == nameLookupByUuid.hashCode()) return
        hashCode = nameLookupByUuid.hashCode()
        saveTo(file)
    }

    override fun saveTo(file: File) {
        json.putAll(nameLookupByUuid.mapKeys { it.key.toString() })
        FileWriter(file).use { fw ->
            fw.write(Jsoner.prettyPrint(json.toJson()))
            fw.flush()
        }
    }
}