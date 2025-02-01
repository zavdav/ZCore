package org.poseidonplugins.zcore.mocks

import org.bukkit.*
import org.bukkit.World.Environment
import org.bukkit.block.Biome
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.generator.BlockPopulator
import org.bukkit.generator.ChunkGenerator
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*

class MockWorld(private val name: String, private val env: Environment) : World {

    override fun getBlockAt(p0: Int, p1: Int, p2: Int): Block {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getBlockAt(p0: Location?): Block {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getBlockTypeIdAt(p0: Int, p1: Int, p2: Int): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getBlockTypeIdAt(p0: Location?): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getHighestBlockYAt(p0: Int, p1: Int): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getHighestBlockYAt(p0: Location?): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getHighestBlockAt(p0: Int, p1: Int): Block {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getHighestBlockAt(p0: Location?): Block {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getChunkAt(p0: Int, p1: Int): Chunk {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getChunkAt(p0: Location?): Chunk {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getChunkAt(p0: Block?): Chunk {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isChunkLoaded(p0: Chunk?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isChunkLoaded(p0: Int, p1: Int): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getLoadedChunks(): Array<Chunk> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun loadChunk(p0: Chunk?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun loadChunk(p0: Int, p1: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun loadChunk(p0: Int, p1: Int, p2: Boolean): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun unloadChunk(p0: Chunk?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun unloadChunk(p0: Int, p1: Int): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun unloadChunk(p0: Int, p1: Int, p2: Boolean): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun unloadChunk(p0: Int, p1: Int, p2: Boolean, p3: Boolean): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun unloadChunkRequest(p0: Int, p1: Int): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun unloadChunkRequest(p0: Int, p1: Int, p2: Boolean): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun regenerateChunk(p0: Int, p1: Int): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun refreshChunk(p0: Int, p1: Int): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun dropItem(p0: Location?, p1: ItemStack?): Item {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun dropItemNaturally(p0: Location?, p1: ItemStack?): Item {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun spawnArrow(p0: Location?, p1: Vector?, p2: Float, p3: Float): Arrow {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun generateTree(p0: Location?, p1: TreeType?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun generateTree(p0: Location?, p1: TreeType?, p2: BlockChangeDelegate?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun spawnCreature(p0: Location?, p1: CreatureType?): LivingEntity {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun strikeLightning(p0: Location?): LightningStrike {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun strikeLightningEffect(p0: Location?): LightningStrike {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getEntities(): MutableList<Entity> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getLivingEntities(): MutableList<LivingEntity> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getPlayers(): MutableList<Player> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getName(): String = name

    override fun getUID(): UUID {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getId(): Long {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getSpawnLocation(): Location {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setSpawnLocation(p0: Int, p1: Int, p2: Int): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getTime(): Long {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setTime(p0: Long) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getFullTime(): Long {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setFullTime(p0: Long) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun hasStorm(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setStorm(p0: Boolean) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getWeatherDuration(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setWeatherDuration(p0: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isThundering(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setThundering(p0: Boolean) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getThunderDuration(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setThunderDuration(p0: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun createExplosion(p0: Double, p1: Double, p2: Double, p3: Float): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun createExplosion(p0: Double, p1: Double, p2: Double, p3: Float, p4: Boolean): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun createExplosion(p0: Double, p1: Double, p2: Double, p3: Float,
                                 p4: Boolean, p5: EntityDamageEvent.DamageCause?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun createExplosion(p0: Location?, p1: Float): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun createExplosion(p0: Location?, p1: Float, p2: Boolean): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun createExplosion(p0: Location?, p1: Float, p2: Boolean, p3: EntityDamageEvent.DamageCause?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getEnvironment(): Environment = env

    override fun getSeed(): Long {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getPVP(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setPVP(p0: Boolean) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getGenerator(): ChunkGenerator {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun save() {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getPopulators(): MutableList<BlockPopulator> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun <T : Entity?> spawn(p0: Location?, p1: Class<T>?): T {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun playEffect(p0: Location?, p1: Effect?, p2: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun playEffect(p0: Location?, p1: Effect?, p2: Int, p3: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getEmptyChunkSnapshot(p0: Int, p1: Int, p2: Boolean, p3: Boolean): ChunkSnapshot {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setSpawnFlags(p0: Boolean, p1: Boolean) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getAllowAnimals(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getAllowMonsters(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getBiome(p0: Int, p1: Int): Biome {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getTemperature(p0: Int, p1: Int): Double {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getHumidity(p0: Int, p1: Int): Double {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getMaxHeight(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getKeepSpawnInMemory(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setKeepSpawnInMemory(p0: Boolean) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isAutoSave(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setAutoSave(p0: Boolean) {
        throw UnsupportedOperationException("Operation not supported.")
    }
}