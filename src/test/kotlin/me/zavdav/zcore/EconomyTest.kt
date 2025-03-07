package me.zavdav.zcore

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.config.Config
import me.zavdav.zcore.mocks.MockPlayer
import me.zavdav.zcore.user.User
import me.zavdav.zcore.util.BalanceOutOfBoundsException
import me.zavdav.zcore.util.NoFundsException
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class EconomyTest : ZCoreTest {

    @Test
    fun `test setting balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        player1.joinServer()
        val user1 = User.from(player1)

        Economy.setBalance(user1.uuid, 5000.0)
        assertEquals(5000.0, user1.balance)

        Economy.setBalance(user1.uuid, 2500.5)
        assertEquals(2500.5, user1.balance)

        Economy.setBalance(user1.uuid, 5999.994)
        assertEquals(5999.99, user1.balance)

        Economy.setBalance(user1.uuid, 6999.995)
        assertEquals(7000.0, user1.balance)

        Economy.setBalance(user1.uuid, -100.0)
        assertEquals(0.0, user1.balance)

        assertThrows<BalanceOutOfBoundsException> {
            Economy.setBalance(user1.uuid, Config.maxBalance + 1.0)
        }
        assertDoesNotThrow {
            Economy.setBalance(user1.uuid, Config.maxBalance)
        }
    }

    @Test
    fun `test adding balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        player1.joinServer()
        val user1 = User.from(player1)

        Economy.addBalance(user1.uuid, 5000.0)
        assertEquals(5000.0, user1.balance)

        Economy.addBalance(user1.uuid, 2500.5)
        assertEquals(7500.5, user1.balance)

        Economy.addBalance(user1.uuid, 5999.994)
        assertEquals(13500.49, user1.balance)

        Economy.addBalance(user1.uuid, 6999.995)
        assertEquals(20500.49, user1.balance)

        Economy.addBalance(user1.uuid, -100.0)
        assertEquals(20400.49, user1.balance)

        Economy.setBalance(user1.uuid, Config.maxBalance - 100.0)
        assertThrows<BalanceOutOfBoundsException> {
            Economy.addBalance(user1.uuid, 100.005)
        }
        assertDoesNotThrow {
            Economy.addBalance(user1.uuid, 100.004)
        }
    }

    @Test
    fun `test subtracting balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        player1.joinServer()
        val user1 = User.from(player1)
        Economy.setBalance(user1.uuid, 50000.0)

        Economy.subtractBalance(user1.uuid, 5000.0)
        assertEquals(45000.0, user1.balance)

        Economy.subtractBalance(user1.uuid, 2500.5)
        assertEquals(42499.5, user1.balance)

        Economy.subtractBalance(user1.uuid, 5999.994)
        assertEquals(36499.51, user1.balance)

        Economy.subtractBalance(user1.uuid, 6999.995)
        assertEquals(29499.51, user1.balance)

        Economy.subtractBalance(user1.uuid, -100.0)
        assertEquals(29599.51, user1.balance)

        assertThrows<NoFundsException> {
            Economy.subtractBalance(user1.uuid, 29599.515)
        }
        assertDoesNotThrow {
            Economy.subtractBalance(user1.uuid, 29599.514)
        }
    }

    @Test
    fun `test multiplying balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        player1.joinServer()
        val user1 = User.from(player1)

        Economy.setBalance(user1.uuid, 5000.0)
        assertEquals(5000.0, user1.balance)

        Economy.multiplyBalance(user1.uuid, 3.0)
        assertEquals(15000.0, user1.balance)

        Economy.multiplyBalance(user1.uuid, 0.5)
        assertEquals(7500.0, user1.balance)

        Economy.setBalance(user1.uuid, 500.0)
        assertEquals(500.0, user1.balance)

        Economy.multiplyBalance(user1.uuid, 1.125125)
        assertEquals(562.56, user1.balance)

        Economy.setBalance(user1.uuid, Config.maxBalance / 4)
        assertThrows<BalanceOutOfBoundsException> {
            Economy.multiplyBalance(user1.uuid, 4.01)
        }
        assertDoesNotThrow {
            Economy.addBalance(user1.uuid, 4.0)
        }
    }

    @Test
    fun `test dividing balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        player1.joinServer()
        val user1 = User.from(player1)

        Economy.setBalance(user1.uuid, 5000.0)
        assertEquals(5000.0, user1.balance)

        Economy.divideBalance(user1.uuid, 5.0)
        assertEquals(1000.0, user1.balance)

        Economy.divideBalance(user1.uuid, 0.5)
        assertEquals(2000.0, user1.balance)

        Economy.setBalance(user1.uuid, 6000.0)
        assertEquals(6000.0, user1.balance)

        Economy.divideBalance(user1.uuid, 1.55)
        assertEquals(3870.97, user1.balance)

        Economy.setBalance(user1.uuid, Config.maxBalance / 4)
        assertThrows<BalanceOutOfBoundsException> {
            Economy.divideBalance(user1.uuid, 0.249)
        }
    }

    @Test
    fun `test transferring balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        val player2 = MockPlayer(UUID.randomUUID(), "Player2")
        player1.joinServer()
        player2.joinServer()

        val user1 = User.from(player1)
        val user2 = User.from(player2)
        Economy.setBalance(user1.uuid, 30000.0)
        Economy.setBalance(user2.uuid, 25000.0)

        Economy.transferBalance(user1.uuid, user2.uuid, 5000.0)
        assertEquals(25000.0, user1.balance)
        assertEquals(30000.0, user2.balance)

        Economy.transferBalance(user2.uuid, user1.uuid, 2500.5)
        assertEquals(27500.5, user1.balance)
        assertEquals(27499.5, user2.balance)

        Economy.transferBalance(user1.uuid, user2.uuid, 5999.994)
        assertEquals(21500.51, user1.balance)
        assertEquals(33499.49, user2.balance)

        Economy.transferBalance(user2.uuid, user1.uuid, 6999.995)
        assertEquals(28500.51, user1.balance)
        assertEquals(26499.49, user2.balance)

        Economy.transferBalance(user1.uuid, user2.uuid, -100.0)
        assertEquals(28600.51, user1.balance)
        assertEquals(26399.49, user2.balance)

        assertThrows<NoFundsException> {
            Economy.transferBalance(user1.uuid, user2.uuid, 28600.515)
        }
        assertDoesNotThrow {
            Economy.transferBalance(user1.uuid, user2.uuid, 28600.514)
        }

        Economy.setBalance(user1.uuid, 5000.0)
        Economy.setBalance(user2.uuid, Config.maxBalance - 100.0)
        assertThrows<BalanceOutOfBoundsException> {
            Economy.transferBalance(user1.uuid, user2.uuid, 100.005)
        }
        assertDoesNotThrow {
            Economy.transferBalance(user1.uuid, user2.uuid, 100.004)
        }
    }
}