package org.poseidonplugins.zcore.player

import org.bukkit.entity.Player
import org.poseidonplugins.zcore.ZCore
import org.poseidonplugins.zcore.util.Utils
import java.io.File
import java.util.UUID

object PlayerMap {

    private val playerMap: MutableMap<UUID, ZPlayer> = mutableMapOf()
    private val knownPlayers: MutableSet<UUID> = mutableSetOf()

    init {
        val dataFolder = File(ZCore.dataFolder, "userdata")
        if (!dataFolder.exists()) dataFolder.mkdirs()

        for (file in dataFolder.list()!!) {
            if (!file.endsWith(".json")) continue
            val uuid = file.replace(".json", "")
            if (!Utils.UUID_PATTERN.matcher(uuid).matches()) {
                ZCore.logger.warning("${ZCore.prefix} Found corrupt UUID: $uuid")
                continue
            }
            knownPlayers.add(UUID.fromString(uuid))
        }
    }

    fun getPlayer(uuid: UUID): ZPlayer {
        if (!knownPlayers.contains(uuid)) knownPlayers.add(uuid)
        if (playerMap.containsKey(uuid)) return playerMap[uuid]!!

        val zPlayer = ZPlayer(uuid)
        playerMap[uuid] = zPlayer
        return zPlayer
    }

    fun getPlayer(player: Player): ZPlayer = getPlayer(player.uniqueId)

    @Synchronized
    fun runTasks() {
        playerMap.entries.removeIf { entry ->
            val zPlayer = getPlayer(entry.key)
            if (!zPlayer.isOnline) {
                zPlayer.saveData()
                true
            }
            else false
        }
    }

    fun isPlayerKnown(uuid: UUID): Boolean = knownPlayers.contains(uuid)

    @Synchronized
    fun saveData() {
        for (zPlayer in playerMap.values) {
            zPlayer.saveData()
        }
    }
}