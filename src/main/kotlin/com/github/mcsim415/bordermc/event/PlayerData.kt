package com.github.mcsim415.bordermc.event

import com.github.mcsim415.bordermc.BordermcPlugin
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack

class PlayerData(plugin: BordermcPlugin): Listener {
    private val dataManager = plugin.dataManager

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player = event.player
        player.teleport(Location(Bukkit.getWorld("world"), 0.5, 5.0, 0.5, (-90).toFloat(), (0).toFloat()))
        player.gameMode = GameMode.ADVENTURE
        player.foodLevel = 20
        player.health = 20.0
        player.inventory.clear()
        player.inventory.armorContents = null
        for (players in Bukkit.getOnlinePlayers()) {
            players.canSee(player)
            player.canSee(players)
        }
        dataManager.setGamePlayer(player, 0)
        dataManager.giveMenu(player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val room = dataManager.getRoomWithPlayer(player)
        dataManager.delGamePlayer(player)
        if (room?.players?.size == 0) {
            dataManager.delRoom(room)
        }
    }
}