package org.poseidonplugins.zcore.commands

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.poseidonplugins.commandapi.CommandEvent
import org.poseidonplugins.commandapi.Preprocessor
import org.poseidonplugins.commandapi.hasPermission
import org.poseidonplugins.zcore.api.Economy
import org.poseidonplugins.zcore.config.Config
import org.poseidonplugins.zcore.exceptions.*
import org.poseidonplugins.zcore.player.PlayerMap
import org.poseidonplugins.zcore.util.format
import org.poseidonplugins.zcore.util.formatError

class Preprocessor : Preprocessor() {
    override fun preprocess(event: CommandEvent) {
        if (!hasPermission(event.sender, event.command.permission)) {
            event.sender.sendMessage(format("noPermission"))
        } else if (event.command.isPlayerOnly && event.sender !is Player) {
            event.sender.sendMessage(format("playerOnly"))
        } else if (event.args.size < event.command.minArgs ||
            (event.args.size > event.command.maxArgs && event.command.maxArgs >= 0)) {
            event.sender.sendMessage(event.command.description)
            event.sender.sendMessage("Usage: ${event.command.usage}")
        } else {
            Bukkit.getPluginManager().callEvent(event)
            try {
                if (!event.isCancelled) event.command.execute(event)
            } catch (e: InvalidUsageException) {
                event.sender.sendMessage(event.command.description)
                event.sender.sendMessage("Usage: ${event.command.usage}")
            } catch (e: PlayerNotFoundException) {
                event.sender.sendMessage(formatError("playerNotFound", "player" to e.username))
            } catch (e: UnknownUserException) {
                event.sender.sendMessage(formatError("unknownUser", "uuid" to e.uuid))
            } catch (e: NoFundsException) {
                event.sender.sendMessage(formatError("noFunds"))
            } catch (e: BalanceOutOfBoundsException) {
                event.sender.sendMessage(formatError("balanceOutOfBounds",
                    "user" to PlayerMap.getPlayer(e.uuid).name,
                    "amount" to Economy.formatBalance(Config.getDouble("maxBalance", 0.0, 10000000000000.0))))
            } catch (e: UnsafeDestinationException) {
                event.sender.sendMessage(formatError("unsafeDestination"))
            }
        }
    }
}