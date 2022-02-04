package com.github.mcsim415.bordermc.event

import com.github.mcsim415.bordermc.BordermcPlugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

class PlayerDeath(plugin: BordermcPlugin): Listener {
    private val gameManager = plugin.gameManager
    private val dataManager = plugin.dataManager

    private fun checkPlayerOut(player: Player): Boolean {
        val size = player.world.worldBorder.size / 2
        val x = player.location.x
        val z = player.location.z
        return x > size || x < -size || z > size || z < -size
    }

//    @EventHandler
//    fun onHelp(event: AsyncPlayerChatEvent) {
//        if (
//            dataManager.getGamePlayer(event.player) == 2
//            && event.message.equals("help", true)
//            && checkPlayerOut(event.player)
//        ) {
//            event.isCancelled = true
//            val x = event.player.location.x.toInt()
//            val z = event.player.location.z.toInt()
//            for (y in 100 downTo 1) {
//                val block = event.player.world.getBlockAt(x, y, z)
//                if (block.type != Material.AIR) {
//                    event.player.teleport(block.location.add(0.5, 1.0, 0.5), PlayerTeleportEvent.TeleportCause.COMMAND)
//                    break
//                }
//            }
//        }
//    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            if (dataManager.getGamePlayer(event.entity as Player) != 2) {
                event.isCancelled = true
            } else if (dataManager.getGamePlayer(event.entity as Player) == 2) {
                val victim = event.entity as Player
//                if (checkPlayerOut(victim)) {
//                    val bar = ActionBar(this)
//                    bar.sendActionBar(victim, "§aPlease enter `help` at chat to teleport to the above.", 10)
//                }
                if (victim.health - event.finalDamage <= 0) {
                    event.isCancelled = true
                    val border = checkPlayerOut(event.entity as Player)
                    val cause: String = when (event.cause) {
                        EntityDamageEvent.DamageCause.CONTACT -> "Contact"
                        EntityDamageEvent.DamageCause.ENTITY_ATTACK -> "Entity Attack"
                        EntityDamageEvent.DamageCause.PROJECTILE -> "Projectile"
                        EntityDamageEvent.DamageCause.SUFFOCATION -> "Suffocation"
                        EntityDamageEvent.DamageCause.FALL -> "Fall"
                        EntityDamageEvent.DamageCause.FIRE -> "Fire"
                        EntityDamageEvent.DamageCause.FIRE_TICK -> "Fire Tick"
                        EntityDamageEvent.DamageCause.MELTING -> "Melting"
                        EntityDamageEvent.DamageCause.LAVA -> "Lava"
                        EntityDamageEvent.DamageCause.DROWNING -> "Drowning"
                        EntityDamageEvent.DamageCause.BLOCK_EXPLOSION -> "Explosion"
                        EntityDamageEvent.DamageCause.ENTITY_EXPLOSION -> "Entity Explosion"
                        EntityDamageEvent.DamageCause.VOID -> "Void"
                        EntityDamageEvent.DamageCause.LIGHTNING -> "Lightning"
                        EntityDamageEvent.DamageCause.SUICIDE -> return
                        EntityDamageEvent.DamageCause.STARVATION -> "Starvation"
                        EntityDamageEvent.DamageCause.POISON -> "Poison"
                        EntityDamageEvent.DamageCause.MAGIC -> "Magic"
                        EntityDamageEvent.DamageCause.WITHER -> "Wither"
                        EntityDamageEvent.DamageCause.FALLING_BLOCK -> "Falling Block"
                        EntityDamageEvent.DamageCause.THORNS -> "Thorns"
                        EntityDamageEvent.DamageCause.CUSTOM -> "Custom"
                        else -> "Unknown"
                    }
                    if (border) {
                        gameManager.onDeath("Outside the border", victim)
                    } else {
                        gameManager.onDeath(cause, victim)
                    }
                }
            }
        }
    }

    @EventHandler
    fun onKill(event: EntityDamageByEntityEvent) {
        if (dataManager.getGamePlayer(event.entity as Player) == 2) {
            if (event.damager is Player && event.entity is Player) {
                val killer = event.damager as Player
                val victim = event.entity as Player
                if (victim.health - event.finalDamage <= 0) {
                    event.isCancelled = true
                    gameManager.onKill(killer, victim)
                }
            }
        }
    }
}