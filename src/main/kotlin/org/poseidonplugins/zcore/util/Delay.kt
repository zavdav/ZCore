package org.poseidonplugins.zcore.util

import org.bukkit.Location
import org.bukkit.entity.Player

class Delay(
    private val player: Player,
    private val runnable: Runnable,
    delay: Int) : Runnable {

    private val task: Int
    private val check: Int
    private val health: Int = player.health
    private val location: Location = player.location

    init {
        check = syncRepeatingTask({ check() }, 0, 1)
        task = syncDelayedTask(this, delay.coerceAtLeast(0) * 20L)
    }

    override fun run() {
        cancelTask(check)
        runnable.run()
    }

    private fun check() {
        if (!player.isOnline || player.health < health ||
            player.location.blockX != location.blockX ||
            player.location.blockY != location.blockY ||
            player.location.blockZ != location.blockZ) {
            cancelTask(task)
            cancelTask(check)
            player.sendTl("youMoved")
        }
    }
}