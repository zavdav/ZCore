package org.poseidonplugins.zcore.config

import org.bukkit.inventory.ItemStack
import org.poseidonplugins.zcore.util.Utils.roundTo

object Kits {

    private lateinit var kits: MutableMap<String, Kit>

    @Suppress("UNCHECKED_CAST")
    fun load() {
        this.kits = mutableMapOf()
        val kits = Config.kits
        for (kit in kits) {
            val name = kit.key
            val map = kit.value as Map<String, Any>
            val cost = ((map["cost"] ?: 0) as Number).toDouble().coerceIn(0.0..Config.maxBalance).roundTo(2)
            val cooldown = ((map["cooldown"] ?: 0) as Number).toInt().coerceAtLeast(0)
            val items = map["items"] as List<String>
            val itemStacks = mutableListOf<ItemStack>()

            for (item in items) {
                val split = item.split(",", limit = 3)
                val idRange = (1..359) + (2256..2257)
                val id = if ((split[0].toInt()) in idRange) split[0].toInt() else 1
                val data = split[1].toShort().coerceIn(0, 15)
                val count = split[2].toInt().coerceIn(1, 64)
                itemStacks.add(ItemStack(id, count, data))
            }
            this.kits[name] = Kit(name, itemStacks.toTypedArray(), cost, cooldown)
        }
    }

    fun getKits(): Map<String, Kit> = kits.toMap()

    fun getKit(name: String): Kit? = kits[name.lowercase()]

    class Kit(
        val name: String,
        val items: Array<out ItemStack>,
        val cost: Double,
        val cooldown: Int
    )
}