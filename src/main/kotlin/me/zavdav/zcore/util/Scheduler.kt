@file:JvmName("Scheduler")

package me.zavdav.zcore.util

import me.zavdav.zcore.ZCore
import org.bukkit.Bukkit

inline fun syncDelayedTask(crossinline block: () -> Any?): Int =
    syncDelayedTask(0, block)

inline fun syncDelayedTask(delay: Long, crossinline block: () -> Any?): Int =
    syncRepeatingTask(delay, -1, block)

inline fun syncRepeatingTask(delay: Long, interval: Long, crossinline block: () -> Any?): Int =
    Bukkit.getScheduler().scheduleSyncRepeatingTask(ZCore.INSTANCE, {
        try {
            block()
        } catch (_: CommandException) {}
    }, delay, interval)

inline fun asyncDelayedTask(crossinline block: () -> Any?): Int =
    asyncDelayedTask(0, block)

inline fun asyncDelayedTask(delay: Long, crossinline block: () -> Any?): Int =
    asyncRepeatingTask(delay, -1, block)

inline fun asyncRepeatingTask(delay: Long, interval: Long, crossinline block: () -> Any?): Int =
    Bukkit.getScheduler().scheduleAsyncRepeatingTask(ZCore.INSTANCE, {
        try {
            block()
        } catch (_: CommandException) {}
    }, delay, interval)

fun cancelTask(taskId: Int) = Bukkit.getScheduler().cancelTask(taskId)