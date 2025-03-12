package me.zavdav.zcore

import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import me.zavdav.zcore.api.Economy
import me.zavdav.zcore.api.LoanNotPermittedException
import me.zavdav.zcore.mocks.MockPlayer
import me.zavdav.zcore.user.User
import java.math.BigDecimal
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class EconomyTest : ZCoreTest {

    @Test
    fun `test setting balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        val user1 = User.from(player1)

        Economy.setBalance(user1.uuid, BigDecimal("5000"))
        assertEquals(BigDecimal("5000").setScale(10), user1.balance)

        Economy.setBalance(user1.uuid, BigDecimal("2500.5"))
        assertEquals(BigDecimal("2500.5").setScale(10), user1.balance)

        Economy.setBalance(user1.uuid, BigDecimal("5999.99"))
        assertEquals(BigDecimal("5999.99").setScale(10), user1.balance)

        Economy.setBalance(user1.uuid, BigDecimal("7000"))
        assertEquals(BigDecimal("7000").setScale(10), user1.balance)

        user1.loanPermitted = false
        assertThrows<LoanNotPermittedException> {
            Economy.setBalance(user1.uuid, BigDecimal("-100"))
        }

        user1.loanPermitted = true
        assertDoesNotThrow {
            Economy.setBalance(user1.uuid, BigDecimal("-100"))
        }
        assertEquals(BigDecimal("-100").setScale(10), user1.balance)
    }

    @Test
    fun `test adding balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        val user1 = User.from(player1)

        Economy.addBalance(user1.uuid, BigDecimal("5000"))
        assertEquals(BigDecimal("5000").setScale(10), user1.balance)

        Economy.addBalance(user1.uuid, BigDecimal("2500.5"))
        assertEquals(BigDecimal("7500.5").setScale(10), user1.balance)

        Economy.addBalance(user1.uuid, BigDecimal("5999.99"))
        assertEquals(BigDecimal("13500.49").setScale(10), user1.balance)

        Economy.addBalance(user1.uuid, BigDecimal("1500.5"))
        assertEquals(BigDecimal("15000.99").setScale(10), user1.balance)

        Economy.addBalance(user1.uuid, BigDecimal("-100.0"))
        assertEquals(BigDecimal("14900.99").setScale(10), user1.balance)
    }

    @Test
    fun `test subtracting balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        val user1 = User.from(player1)

        Economy.setBalance(user1.uuid, BigDecimal("50000"))

        Economy.subtractBalance(user1.uuid, BigDecimal("5000"))
        assertEquals(BigDecimal("45000").setScale(10), user1.balance)

        Economy.subtractBalance(user1.uuid, BigDecimal("2500.5"))
        assertEquals(BigDecimal("42499.5").setScale(10), user1.balance)

        Economy.subtractBalance(user1.uuid, BigDecimal("5999.99"))
        assertEquals(BigDecimal("36499.51").setScale(10), user1.balance)

        Economy.subtractBalance(user1.uuid, BigDecimal("6999.99"))
        assertEquals(BigDecimal("29499.52").setScale(10), user1.balance)

        Economy.subtractBalance(user1.uuid, BigDecimal("-100"))
        assertEquals(BigDecimal("29599.52").setScale(10), user1.balance)

        assertDoesNotThrow {
            Economy.subtractBalance(user1.uuid, BigDecimal("29599.50"))
        }

        Economy.setBalance(user1.uuid, BigDecimal("5000"))
        user1.loanPermitted = false
        assertThrows<LoanNotPermittedException> {
            Economy.subtractBalance(user1.uuid, BigDecimal("6000"))
        }

        user1.loanPermitted = true
        assertDoesNotThrow {
            Economy.subtractBalance(user1.uuid, BigDecimal("6000"))
        }
        assertEquals(BigDecimal("-1000").setScale(10), user1.balance)

    }

    @Test
    fun `test multiplying balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        val user1 = User.from(player1)

        Economy.setBalance(user1.uuid, BigDecimal("5000"))
        assertEquals(BigDecimal("5000").setScale(10), user1.balance)

        Economy.multiplyBalance(user1.uuid, BigDecimal("3"))
        assertEquals(BigDecimal("15000").setScale(10), user1.balance)

        Economy.multiplyBalance(user1.uuid, BigDecimal("0.5"))
        assertEquals(BigDecimal("7500").setScale(10), user1.balance)

        Economy.setBalance(user1.uuid, BigDecimal("500"))
        assertEquals(BigDecimal("500").setScale(10), user1.balance)

        Economy.multiplyBalance(user1.uuid, BigDecimal("1.12345678901234567890"))
        assertEquals(BigDecimal("561.7283945061"), user1.balance)
    }

    @Test
    fun `test dividing balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        val user1 = User.from(player1)

        Economy.setBalance(user1.uuid, BigDecimal("5000"))
        assertEquals(BigDecimal("5000").setScale(10), user1.balance)

        Economy.divideBalance(user1.uuid, BigDecimal("5"))
        assertEquals(BigDecimal("1000").setScale(10), user1.balance)

        Economy.divideBalance(user1.uuid, BigDecimal("0.5"))
        assertEquals(BigDecimal("2000").setScale(10), user1.balance)

        Economy.setBalance(user1.uuid, BigDecimal("6000"))
        assertEquals(BigDecimal("6000").setScale(10), user1.balance)

        Economy.divideBalance(user1.uuid, BigDecimal("1.15"))
        assertEquals(BigDecimal("5217.3913043478").setScale(10), user1.balance)
    }

    @Test
    fun `test transferring balance`() {
        val player1 = MockPlayer(UUID.randomUUID(), "Player1")
        val player2 = MockPlayer(UUID.randomUUID(), "Player2")

        val user1 = User.from(player1)
        val user2 = User.from(player2)
        Economy.setBalance(user1.uuid, BigDecimal("30000"))
        Economy.setBalance(user2.uuid, BigDecimal("25000"))

        Economy.transferBalance(user1.uuid, user2.uuid, BigDecimal("5000"))
        assertEquals(BigDecimal("25000").setScale(10), user1.balance)
        assertEquals(BigDecimal("30000").setScale(10), user2.balance)

        Economy.transferBalance(user2.uuid, user1.uuid, BigDecimal("2500.5"))
        assertEquals(BigDecimal("27500.5").setScale(10), user1.balance)
        assertEquals(BigDecimal("27499.5").setScale(10), user2.balance)

        Economy.transferBalance(user1.uuid, user2.uuid, BigDecimal("5999.99"))
        assertEquals(BigDecimal("21500.51").setScale(10), user1.balance)
        assertEquals(BigDecimal("33499.49").setScale(10), user2.balance)

        Economy.transferBalance(user1.uuid, user2.uuid, BigDecimal("-100.0"))
        assertEquals(BigDecimal("21600.51").setScale(10), user1.balance)
        assertEquals(BigDecimal("33399.49").setScale(10), user2.balance)

        Economy.setBalance(user1.uuid, BigDecimal("1000"))
        user1.loanPermitted = false
        assertThrows<LoanNotPermittedException> {
            Economy.transferBalance(user1.uuid, user2.uuid, BigDecimal("1000.01"))
        }

        assertDoesNotThrow {
            Economy.transferBalance(user1.uuid, user2.uuid, BigDecimal("1000"))
        }
    }
}