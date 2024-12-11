package org.betamc.core.player

import org.betamc.core.BMCCore
import org.betamc.core.util.Utils
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID

object PlayerMap {

    private val playerMap: MutableMap<UUID, BMCPlayer> = mutableMapOf()
    private val knownPlayers: MutableSet<UUID> = mutableSetOf()

    init {
        val dataFolder = File(BMCCore.dataFolder, "userdata")
        if (!dataFolder.exists()) dataFolder.mkdirs()

        for (file in dataFolder.list()!!) {
            if (!file.endsWith(".json")) continue
            val uuid = file.replace(".json", "")
            if (!Utils.UUID_PATTERN.matcher(uuid).matches()) {
                BMCCore.logger.warning("${BMCCore.prefix} Found corrupt UUID: $uuid")
                continue
            }
            knownPlayers.add(UUID.fromString(uuid))
        }
    }

    fun getPlayer(uuid: UUID): BMCPlayer {
        if (!knownPlayers.contains(uuid)) knownPlayers.add(uuid)
        if (playerMap.containsKey(uuid)) return playerMap[uuid]!!

        val bmcPlayer = BMCPlayer(uuid)
        playerMap[uuid] = bmcPlayer
        return bmcPlayer
    }

    fun getPlayer(player: Player): BMCPlayer = getPlayer(player.uniqueId)

    fun runTasks() {
        playerMap.entries.removeIf { entry ->
            val bmcPlayer = getPlayer(entry.key)
            if (!bmcPlayer.isOnline) {
                bmcPlayer.saveData()
                true
            }
            else false
        }
    }

    fun isPlayerKnown(uuid: UUID): Boolean = knownPlayers.contains(uuid)

    fun saveData() {
        for (bmcPlayer in playerMap.values) {
            bmcPlayer.saveData()
        }
    }
}