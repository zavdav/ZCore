package org.poseidonplugins.zimport.hooks.plugins

import com.github.cliftonlabs.json_simple.JsonObject
import java.time.LocalDateTime
import java.util.UUID

interface CorePluginHook {

    fun getKnownUsers(): Set<UUID>

    fun getUsername(uuid: UUID): String

    fun getBalance(uuid: UUID): Double

    fun getHomes(uuid: UUID): JsonObject

    fun isBanned(uuid: UUID): Boolean

    fun getBanExpiry(uuid: UUID): LocalDateTime

    fun isMuted(uuid: UUID): Boolean

    fun getMuteExpiry(uuid: UUID): LocalDateTime
}