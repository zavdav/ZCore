package org.poseidonplugins.zimport.task

import org.poseidonplugins.zcore.data.Punishments
import org.poseidonplugins.zcore.user.User
import org.poseidonplugins.zcore.user.UserMap
import org.poseidonplugins.zimport.config.Config
import org.poseidonplugins.zimport.hooks.plugins.CorePluginHook
import org.poseidonplugins.zimport.util.Logger
import java.time.LocalDateTime

class TransferTask(private val hook: CorePluginHook) : Runnable {

    override fun run() {
        val start = System.currentTimeMillis()

        for (uuid in hook.getKnownUsers()) {
            if (uuid in UserMap.knownUsers) {
                if (!Config.replaceExisting) {
                    Logger.info("Skipping existing userdata of $uuid")
                    continue
                }
                Logger.info("Replacing existing userdata of $uuid")
            }

            val user = User.from(uuid, false)
            user.updateOnJoin(hook.getUsername(uuid))
            if (Config.transferBalances) user.balance = hook.getBalance(uuid)
            if (Config.transferHomes) user.homes = hook.getHomes(uuid)
            if (Config.transferPunishments) {
                if (hook.isBanned(uuid)) {
                    val banExpiry = hook.getBanExpiry(uuid)
                    if (banExpiry == LocalDateTime.MAX) {
                        Punishments.ban(uuid)
                    } else {
                        Punishments.ban(uuid, banExpiry)
                    }
                }
                if (hook.isMuted(uuid)) {
                    Punishments.mute(uuid, hook.getMuteExpiry(uuid))
                }
            }

            user.saveData()
        }

        Punishments.saveData()
        val elapsedTime = System.currentTimeMillis() - start
        Logger.info("Transfer completed in $elapsedTime ms!")
    }
}