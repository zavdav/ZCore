package me.zavdav.zcore.config

import me.zavdav.zcore.util.Logger
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object Items {

    private val itemMap: MutableMap<String, Pair<Int, Short>> = mutableMapOf()

    fun load() {
        val stream: InputStream
        try {
            stream = this::class.java.getResourceAsStream("/items.csv")!!
        } catch (e: Exception) {
            Logger.severe("Failed to load items.csv")
            throw e
        }

        BufferedReader(InputStreamReader(stream)).useLines {
            for (line in it) {
                val strings = line.split(",", limit = 3)
                itemMap[strings[0]] = strings[1].toInt() to strings[2].toShort()
            }
        }
    }

    fun get(item: String): ItemStack? {
        val itemId = itemMap[item]?.first ?: return null
        val data = itemMap[item]?.second?.coerceIn(0, 15) ?: return null
        val material = Material.getMaterial(itemId) ?: return null
        return ItemStack(material, Config.giveAmount, data)
    }

    fun itemFromString(string: String): ItemStack? {
        if (!string.matches("(\\d+)(?::(\\d+))?".toRegex())) return null
        val split = string.split(":", limit = 2)

        val itemId = split[0].toIntOrNull()?.coerceAtLeast(1) ?: return null
        val data = if (split.size == 2) {
            split[1].toShortOrNull()?.coerceAtLeast(0) ?: return null
        }
        else 0
        val material = Material.getMaterial(itemId) ?: return null
        return ItemStack(material, Config.giveAmount, data)
    }
}