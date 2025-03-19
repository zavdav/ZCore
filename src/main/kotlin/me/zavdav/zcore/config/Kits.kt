package me.zavdav.zcore.config

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.math.BigDecimal

class Kit(
    val name: String,
    val items: Array<out ItemStack>,
    val cost: BigDecimal,
    val cooldown: Int
)

object Kits {

    private val kits: MutableList<Kit> = mutableListOf()

    @Suppress("UNCHECKED_CAST")
    fun load() {
        kits.clear()

        for ((name, kit) in Config.kits) {
            if (name in kits.map { it.name }) continue
            kit as? Map<String, Any> ?: continue

            val items = (kit["items"] as List<String>).map {
                val split = it.split(",", limit = 3)
                val material = Material.getMaterial(split[0].toInt())
                val data = split[1].toShort().coerceIn(0, 15)
                val count = split[2].toInt().coerceIn(1, 64)
                ItemStack(material, count, data)
            }

            val cost = ((kit["cost"] ?: 0) as Number).toDouble().toBigDecimal()
            val cooldown = ((kit["cooldown"] ?: 0) as Number).toInt().coerceAtLeast(0)

            kits.add(Kit(name, items.toTypedArray(), cost, cooldown))
        }
    }

    fun getKits(): List<Kit> = kits.toList()

    fun getKit(name: String): Kit? = kits.firstOrNull { it.name.equals(name, true) }
}