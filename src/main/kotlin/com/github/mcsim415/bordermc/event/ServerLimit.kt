package com.github.mcsim415.bordermc.event

import com.github.mcsim415.bordermc.BordermcPlugin
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.CraftItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.util.Vector

class ServerLimit(plugin: BordermcPlugin): Listener {
    private val dataManager = plugin.dataManager

    @EventHandler
    fun onWeatherChange(event: WeatherChangeEvent) {
        if (event.world.name == "world") {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onHungry(event: FoodLevelChangeEvent) {
        if (event.entity is Player) {
            if (dataManager.getGamePlayer(event.entity as Player) != 2) {
                (event.entity as Player).foodLevel = 20
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onThrow(event: PlayerDropItemEvent) {
        if (dataManager.getGamePlayer(event.player) != 2) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlaceBlock(event: BlockPlaceEvent) {
        if (event.block.y > 150) {
            event.isCancelled = true
            val player = event.player
            player.sendMessage("§fYou cannot place blocks above 150")
        }
        if (dataManager.getGamePlayer(event.player) != 2) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        if (dataManager.getGamePlayer(event.player) != 2) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onCraft(event: CraftItemEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        if (event.player.isFlying && event.to.y > 150) {
            val player: Player = event.player
            event.player.teleport(event.from.subtract(0.0, 1.0E-5, 0.0))
            player.velocity = Vector(
                player.velocity.x,
                (-3).toDouble(),
                player.velocity.z
            )
            player.playSound(
                player.location,
                Sound.BAT_TAKEOFF,
                1.0f,
                1.0f
            )
        }
    }
}