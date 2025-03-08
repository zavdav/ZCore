@file:JvmName("WorldUtils")

package me.zavdav.zcore.util

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import kotlin.math.abs
import kotlin.math.ceil

private val AIR_MATERIALS: Set<Int> = setOf(
    0, 6, 27, 28, 32, 37, 38, 39, 40, 50, 55, 60, 63, 64, 65,
    66, 68, 70, 71, 72, 75, 76, 77, 78, 83, 93, 94, 96, 136
)

fun getSafeHeight(loc: Location): Int {
    val world = loc.world
    val x = loc.blockX
    var y = ceil(loc.y).toInt()
    val z = loc.blockZ

    while (isBlockAboveAir(world, x, y, z)) {
        y -= 1
        if (y < 1) break
    }
    while (isBlockUnsafe(world, x, y, z)) {
        y += 1
        if (y > 127) throw MiscellaneousException(tl("unsafeDestination"))
    }
    return y
}

fun isBlockAboveAir(world: World, x: Int, y: Int, z: Int): Boolean =
    world.getBlockAt(x, y - 1, z).typeId in AIR_MATERIALS

fun isBlockUnsafe(world: World, x: Int, y: Int, z: Int): Boolean {
    val below = world.getBlockAt(x, y - 1, z).typeId

    if (below == Material.LAVA.id || below == Material.STATIONARY_LAVA.id || below == Material.FIRE.id
        || world.getBlockAt(x, y, z).typeId !in AIR_MATERIALS
        || world.getBlockAt(x, y + 1, z).typeId !in AIR_MATERIALS) {
        return true
    }
    return isBlockAboveAir(world, x, y, z)
}

fun roundYaw(float: Float): Int {
    val yaw = if (float < 0) float + 360 else float
    var closest = -1
    var lowestDiff = Float.MAX_VALUE

    for (i in 0..360 step 90) {
        val diff = abs(yaw - i)
        if (diff < lowestDiff) {
            closest = i
            lowestDiff = diff
        }
    }
    return if (closest == 360) 0 else closest
}