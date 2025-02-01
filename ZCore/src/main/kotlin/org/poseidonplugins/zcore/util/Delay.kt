package org.poseidonplugins.zcore.util

import org.bukkit.Location
import org.bukkit.entity.Player

class Delay(
    private val player: Player,
    delay: Int,
    private val runnable: () -> Unit
) : Runnable {

    private var task: Int = -1
    private var check: Int = -1
    private val health: Int = player.health
    private val location: Location = player.location

    init {
        if (delay > 0) {
            check = syncRepeatingTask({ check() }, 0, 1)
            task = syncDelayedTask(this, delay.coerceAtLeast(0) * 20L)
        } else {
            runnable.invoke()
        }
    }

    override fun run() {
        cancelTask(check)
        runnable.invoke()
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