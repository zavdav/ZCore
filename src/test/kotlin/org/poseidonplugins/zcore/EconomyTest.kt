package org.poseidonplugins.zcore

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.mocks.MockPlayer
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.BalanceOutOfBoundsException
import org.poseidonplugins.zcore.util.NoFundsException
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

class EconomyTest : ZCoreTest {

    @Test
    fun `test setting balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        player1.joinServer()
        val zPlayer1 = PlayerMap.getPlayer(player1)

        Economy.setBalance(zPlayer1.uuid, 5000.0)
        assertEquals(5000.0, zPlayer1.balance)

        Economy.setBalance(zPlayer1.uuid, 2500.5)
        assertEquals(2500.5, zPlayer1.balance)

        Economy.setBalance(zPlayer1.uuid, 5999.994)
        assertEquals(5999.99, zPlayer1.balance)

        Economy.setBalance(zPlayer1.uuid, 6999.995)
        assertEquals(7000.0, zPlayer1.balance)

        Economy.setBalance(zPlayer1.uuid, -100.0)
        assertEquals(0.0, zPlayer1.balance)

        assertThrows<BalanceOutOfBoundsException> {
            Economy.setBalance(zPlayer1.uuid,
                Config.getDouble("maxBalance", 0.0, Economy.MAX_BALANCE) + 1.0
            )
        }
        assertDoesNotThrow {
            Economy.setBalance(zPlayer1.uuid,
                Config.getDouble("maxBalance", 0.0, Economy.MAX_BALANCE)
            )
        }
    }

    @Test
    fun `test adding balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        player1.joinServer()
        val zPlayer1 = PlayerMap.getPlayer(player1)

        Economy.addBalance(zPlayer1.uuid, 5000.0)
        assertEquals(5000.0, zPlayer1.balance)

        Economy.addBalance(zPlayer1.uuid, 2500.5)
        assertEquals(7500.5, zPlayer1.balance)

        Economy.addBalance(zPlayer1.uuid, 5999.994)
        assertEquals(13500.49, zPlayer1.balance)

        Economy.addBalance(zPlayer1.uuid, 6999.995)
        assertEquals(20500.49, zPlayer1.balance)

        Economy.addBalance(zPlayer1.uuid, -100.0)
        assertEquals(20400.49, zPlayer1.balance)

        Economy.setBalance(zPlayer1.uuid,
            Config.getDouble("maxBalance", 0.0, Economy.MAX_BALANCE) - 100.0
        )
        assertThrows<BalanceOutOfBoundsException> {
            Economy.addBalance(zPlayer1.uuid, 100.005)
        }
        assertDoesNotThrow {
            Economy.addBalance(zPlayer1.uuid, 100.004)
        }
    }

    @Test
    fun `test subtracting balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        player1.joinServer()
        val zPlayer1 = PlayerMap.getPlayer(player1)
        Economy.setBalance(zPlayer1.uuid, 50000.0)

        Economy.subtractBalance(zPlayer1.uuid, 5000.0)
        assertEquals(45000.0, zPlayer1.balance)

        Economy.subtractBalance(zPlayer1.uuid, 2500.5)
        assertEquals(42499.5, zPlayer1.balance)

        Economy.subtractBalance(zPlayer1.uuid, 5999.994)
        assertEquals(36499.51, zPlayer1.balance)

        Economy.subtractBalance(zPlayer1.uuid, 6999.995)
        assertEquals(29499.51, zPlayer1.balance)

        Economy.subtractBalance(zPlayer1.uuid, -100.0)
        assertEquals(29599.51, zPlayer1.balance)

        assertThrows<NoFundsException> {
            Economy.subtractBalance(zPlayer1.uuid, 29599.515)
        }
        assertDoesNotThrow {
            Economy.subtractBalance(zPlayer1.uuid, 29599.514)
        }
    }

    @Test
    fun `test transferring balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        val player2 = MockPlayer(UUID.randomUUID(), "Player2")
        player1.joinServer()
        player2.joinServer()

        val zPlayer1 = PlayerMap.getPlayer(player1)
        val zPlayer2 = PlayerMap.getPlayer(player2)
        Economy.setBalance(zPlayer1.uuid, 30000.0)
        Economy.setBalance(zPlayer2.uuid, 25000.0)

        Economy.transferBalance(zPlayer1.uuid, zPlayer2.uuid, 5000.0)
        assertEquals(25000.0, zPlayer1.balance)
        assertEquals(30000.0, zPlayer2.balance)

        Economy.transferBalance(zPlayer2.uuid, zPlayer1.uuid, 2500.5)
        assertEquals(27500.5, zPlayer1.balance)
        assertEquals(27499.5, zPlayer2.balance)

        Economy.transferBalance(zPlayer1.uuid, zPlayer2.uuid, 5999.994)
        assertEquals(21500.51, zPlayer1.balance)
        assertEquals(33499.49, zPlayer2.balance)

        Economy.transferBalance(zPlayer2.uuid, zPlayer1.uuid, 6999.995)
        assertEquals(28500.51, zPlayer1.balance)
        assertEquals(26499.49, zPlayer2.balance)

        Economy.transferBalance(zPlayer1.uuid, zPlayer2.uuid, -100.0)
        assertEquals(28600.51, zPlayer1.balance)
        assertEquals(26399.49, zPlayer2.balance)

        assertThrows<NoFundsException> {
            Economy.transferBalance(zPlayer1.uuid, zPlayer2.uuid, 28600.515)
        }
        assertDoesNotThrow {
            Economy.transferBalance(zPlayer1.uuid, zPlayer2.uuid, 28600.514)
        }

        Economy.setBalance(zPlayer1.uuid, 5000.0)
        Economy.setBalance(zPlayer2.uuid,
            Config.getDouble("maxBalance", 0.0, Economy.MAX_BALANCE) - 100.0
        )
        assertThrows<BalanceOutOfBoundsException> {
            Economy.transferBalance(zPlayer1.uuid, zPlayer2.uuid, 100.005)
        }
        assertDoesNotThrow {
            Economy.transferBalance(zPlayer1.uuid, zPlayer2.uuid, 100.004)
        }
    }
}