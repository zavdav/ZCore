package org.poseidonplugins.zcore.util

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.sendMessage
import org.poseidonplugins.zcore.ZCore

class Delay(
    private val player: Player,
    private val runnable: Runnable,
    delay: Int) : Runnable {

    private val task: Int
    private val check: Int
    private val health: Int = player.health
    private val location: Location = player.location

    init {
        check = Bukkit.getScheduler().scheduleSyncRepeatingTask(ZCore.plugin, { check() }, 0, 1)
        task = Bukkit.getScheduler().scheduleSyncDelayedTask(ZCore.plugin, this, delay.coerceAtLeast(0) * 20L)
    }

    override fun run() {
        Bukkit.getScheduler().cancelTask(check)
        runnable.run()
    }

    private fun check() {
        if (player.health < health ||
            player.location.blockX != location.blockX ||
            player.location.blockY != location.blockY ||
            player.location.blockZ != location.blockZ) {
            Bukkit.getScheduler().cancelTask(task)
            Bukkit.getScheduler().cancelTask(check)
            sendMessage(player, format("youMoved"))
        }
    }
}