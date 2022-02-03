package com.github.mcsim415.bordermc.event

import com.github.mcsim415.bordermc.BordermcPlugin
import com.github.mcsim415.bordermc.utils.BarUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerData(plugin: BordermcPlugin): Listener {
    private val dataManager = plugin.dataManager
    private val gameManager = plugin.gameManager

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        dataManager.respawn(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        val room = dataManager.getRoomWithPlayer(player)
        dataManager.delGamePlayer(player)
        if (room != null) {
            room.players.remove(player)
            room.playingPlayers.remove(player)
            if (room.players.size == 0) {
                dataManager.delRoom(room)
            } else {
                if (room.state == 1) {
                    gameManager.onDeath("Quit", player)
                }
            }
            BarUtil.removeBar(player)
        }
    }
}