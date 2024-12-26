package org.poseidonplugins.zcore.commands

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.Preprocessor
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.commandapi.sendMessage
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.config.Property
import org.poseidonplugins.zcore.exceptions.*
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatError

class Preprocessor : Preprocessor() {
    override fun preprocess(event: CommandEvent) {
        if (!hasPermission(event.sender, event.command.permission)) {
            sendMessage(event.sender, format("noPermission"))
        } else if (event.command.isPlayerOnly && event.sender !is Player) {
            sendMessage(event.sender, format("playerOnly"))
        } else if (event.args.size < event.command.minArgs || (event.args.size > event.command.maxArgs && event.command.maxArgs >= 0)) {
            sendMessage(event.sender, event.command.description)
            sendMessage(event.sender, "Usage: ${event.command.usage}")
        } else {
            Bukkit.getPluginManager().callEvent(event)
            try {
                if (!event.isCancelled) event.command.execute(event)
            } catch (e: PlayerNotFoundException) {
                sendMessage(event.sender, formatError("playerNotFound", "player" to e.username))
            } catch (e: UnknownUserException) {
                sendMessage(event.sender, formatError("unknownUser", "uuid" to e.uuid))
            } catch (e: NoFundsException) {
                sendMessage(event.sender, formatError("noFunds"))
            } catch (e: BalanceOutOfBoundsException) {
                sendMessage(event.sender, formatError("balanceOutOfBounds",
                    "user" to PlayerMap.getPlayer(e.uuid).name,
                    "amount" to Economy.formatBalance(Property.MAX_BALANCE.toDouble())))
            } catch (e: UnsafeDestinationException) {
                sendMessage(event.sender, formatError("unsafeDestination"))
            }
        }
    }
}