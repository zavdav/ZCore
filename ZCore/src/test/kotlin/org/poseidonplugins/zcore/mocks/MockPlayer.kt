package org.poseidonplugins.zcore.mocks

import com.projectposeidon.ConnectionType
import net.minecraft.server.Packet
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import org.bukkit.map.MapView
import org.bukkit.permissions.Permission
import org.bukkit.permissions.PermissionAttachment
import org.bukkit.permissions.PermissionAttachmentInfo
import org.bukkit.plugin.Plugin
import org.bukkit.util.Vector
import java.net.InetSocketAddress
import java.util.*

class MockPlayer(
    private val uuid: UUID,
    private val name: String
) : Player {

    private val messages: MutableList<String> = mutableListOf()
    private var location: Location = Location(server.worlds[0], 0.0, 0.0, 0.0)

    fun joinServer() = MockServer.players.add(this)
    
    fun leaveServer() = MockServer.players.remove(this)

    override fun getLocation(): Location = location

    override fun setVelocity(p0: Vector?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getVelocity(): Vector {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getWorld(): World = location.world

    override fun teleport(location: Location?): Boolean {
        return if (location  == null) {
            false
        } else {
            this.location = location
            true
        }
    }

    override fun teleport(entity: Entity?): Boolean {
        return if (entity == null) {
            false
        } else {
            this.location = entity.location
            true
        }
    }

    override fun getNearbyEntities(p0: Double, p1: Double, p2: Double): MutableList<Entity> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getEntityId(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getFireTicks(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getMaxFireTicks(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setFireTicks(p0: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun remove() {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isDead(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getServer(): Server = MockServer

    override fun getPassenger(): Entity {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setPassenger(p0: Entity?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isEmpty(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun eject(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getFallDistance(): Float {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setFallDistance(p0: Float) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setLastDamageCause(p0: EntityDamageEvent?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getLastDamageCause(): EntityDamageEvent {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getHealth(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setHealth(p0: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getEyeHeight(): Double {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getEyeHeight(p0: Boolean): Double {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getEyeLocation(): Location {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getLineOfSight(p0: HashSet<Byte>?, p1: Int): MutableList<Block> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getTargetBlock(p0: HashSet<Byte>?, p1: Int): Block {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getLastTwoTargetBlocks(p0: HashSet<Byte>?, p1: Int): MutableList<Block> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun throwEgg(): Egg {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun throwSnowball(): Snowball {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun shootArrow(): Arrow {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isInsideVehicle(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun leaveVehicle(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getVehicle(): Vehicle {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getRemainingAir(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setRemainingAir(p0: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getMaximumAir(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setMaximumAir(p0: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun damage(p0: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun damage(p0: Int, p1: Entity?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getMaximumNoDamageTicks(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setMaximumNoDamageTicks(p0: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getLastDamage(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setLastDamage(p0: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getNoDamageTicks(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setNoDamageTicks(p0: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isOp(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setOp(p0: Boolean) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isPermissionSet(p0: String?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isPermissionSet(p0: Permission?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun hasPermission(p0: String?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun hasPermission(p0: Permission?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun addAttachment(p0: Plugin?, p1: String?, p2: Boolean): PermissionAttachment {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun addAttachment(p0: Plugin?): PermissionAttachment {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun addAttachment(p0: Plugin?, p1: String?, p2: Boolean, p3: Int): PermissionAttachment {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun addAttachment(p0: Plugin?, p1: Int): PermissionAttachment {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun removeAttachment(p0: PermissionAttachment?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun recalculatePermissions() {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getEffectivePermissions(): MutableSet<PermissionAttachmentInfo> {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getName(): String = name

    override fun getInventory(): PlayerInventory {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getItemInHand(): ItemStack {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setItemInHand(p0: ItemStack?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isSleeping(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getSleepTicks(): Int {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun sendMessage(message: String?) {
        messages.add(message ?: "")
    }

    override fun isOnline(): Boolean = true

    override fun isBanned(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setBanned(p0: Boolean) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isWhitelisted(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setWhitelisted(p0: Boolean) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getDisplayName(): String {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setDisplayName(p0: String?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setCompassTarget(p0: Location?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getUniqueId(): UUID = uuid

    override fun getPlayerUUID(): UUID {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getCompassTarget(): Location {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getAddress(): InetSocketAddress {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun sendRawMessage(p0: String?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun kickPlayer(p0: String?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun chat(message: String?) {
        MockServer.chat.add(message ?: "")
    }

    override fun performCommand(p0: String?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isSneaking(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setSneaking(p0: Boolean) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun saveData() {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun loadData() {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setSleepingIgnored(p0: Boolean) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isSleepingIgnored(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun playNote(p0: Location?, p1: Byte, p2: Byte) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun playNote(p0: Location?, p1: Instrument?, p2: Note?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun playEffect(p0: Location?, p1: Effect?, p2: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun sendBlockChange(p0: Location?, p1: Material?, p2: Byte) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun sendBlockChange(p0: Location?, p1: Int, p2: Byte) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun sendChunkChange(p0: Location?, p1: Int, p2: Int, p3: Int, p4: ByteArray?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun sendMap(p0: MapView?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun updateInventory() {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun awardAchievement(p0: Achievement?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun incrementStatistic(p0: Statistic?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun incrementStatistic(p0: Statistic?, p1: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun incrementStatistic(p0: Statistic?, p1: Material?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun incrementStatistic(p0: Statistic?, p1: Material?, p2: Int) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun setPlayerTime(p0: Long, p1: Boolean) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getPlayerTime(): Long {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getPlayerTimeOffset(): Long {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isPlayerTimeRelative(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun getConnectionType(): ConnectionType {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun hasReceivedPacket0(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun isUsingReleaseToBeta(): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun resetPlayerTime() {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun hidePlayer(p0: Player?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun showPlayer(p0: Player?) {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun canSee(p0: Player?): Boolean {
        throw UnsupportedOperationException("Operation not supported.")
    }

    override fun sendPacket(p0: Player?, p1: Packet?) {
        throw UnsupportedOperationException("Operation not supported.")
    }
}