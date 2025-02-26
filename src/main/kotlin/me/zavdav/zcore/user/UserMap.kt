package me.zavdav.zcore.user

import me.zavdav.zcore.ZCore
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.util.Logger
import me.zavdav.zcore.util.UUID_PATTERN
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object UserMap {

    val userMap: MutableMap<UUID, User> = ConcurrentHashMap<UUID, User>()
    val knownUsers: MutableSet<UUID> = mutableSetOf()
    private val precacheAll: Boolean

    init {
        val dataFolder = File(ZCore.dataFolder, "userdata")
        if (!dataFolder.exists()) dataFolder.mkdirs()

        for (file in dataFolder.list()!!) {
            if (!file.endsWith(".json")) continue
            val uuid = file.replace(".json", "")
            if (!UUID_PATTERN.matcher(uuid).matches()) {
                Logger.warning("Found corrupt UUID: $uuid")
                continue
            }
            knownUsers.add(UUID.fromString(uuid))
        }

        precacheAll = Config.precacheAllPlayers
        if (precacheAll) {
            for (uuid in knownUsers) {
                User.from(uuid)
            }
            Logger.info("Precached ${userMap.size} user(s).")
        }
    }

    fun getAllUsers(): Set<User> =
        if (precacheAll) userMap.values.toSet()
        else knownUsers.map { User.from(it) }.toSet()

    fun checkOnlineUsers() {
        userMap.entries.removeIf {
            val user = it.value
            if (!user.isOnline && System.currentTimeMillis() - user.lastSeen >= 600 * 1000) {
                user.saveData(true)
                !precacheAll
            } else {
                user.checkIsAfk()
                false
            }
        }
    }

    fun isUserKnown(uuid: UUID): Boolean = uuid in knownUsers

    fun saveData(async: Boolean, force: Boolean) {
        for (user in userMap.values) {
            user.saveData(async, force)
        }
    }
}