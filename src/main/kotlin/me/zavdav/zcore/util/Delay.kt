package me.zavdav.zcore.util

import me.zavdav.zcore.api.EconomyException
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class Delay(
    private val sender: CommandSender,
    private val target: Player,
    delay: Int,
    private val block: () -> Unit
) {

    private var task: Int = -1
    private var check: Int = -1
    private val health: Int = target.health
    private val location: Location = target.location

    init {
        if (delay > 0) {
            check = syncRepeatingTask(0, 1) { check() }
            task = syncDelayedTask(delay.coerceAtLeast(0) * 20L) {
                cancelTask(check)
                runBlock()
            }
        } else {
            runBlock()
        }
    }

    private fun runBlock() {
        try {
            block()
        } catch (e: EconomyException) {
            sender.sendMessage(e.message)
        } catch (e: MiscellaneousException) {
            sender.sendMessage(e.message)
        }
    }

    private fun check() {
        if (!target.isOnline || target.health < health ||
            target.location.blockX != location.blockX ||
            target.location.blockY != location.blockY ||
            target.location.blockZ != location.blockZ) {
            cancelTask(task)
            cancelTask(check)
            target.sendTl("youMoved")
        }
    }
}