package me.zavdav.zcore.data

import org.bukkit.inventory.ItemStack
import java.time.LocalDateTime
import java.util.UUID

class Mute(
    val uuid: UUID,
    val until: LocalDateTime?,
    val reason: String
)

class Ban(
    val uuid: UUID,
    val until: LocalDateTime?,
    val reason: String
)

class IPBan(
    val ip: String,
    var uuids: List<UUID>,
    val until: LocalDateTime?,
    val reason: String
)

class Kit(
    val name: String,
    val items: Array<out ItemStack>,
    val cost: Double,
    val cooldown: Int
)