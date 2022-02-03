package com.github.mcsim415.bordermc.data

import org.bukkit.entity.Player
import java.util.*

/**
 * @param state 0 = waiting, 1 = starting, 2 = gaming, 3 = ending
 */
data class Room(var players: MutableList<Player>, var playingPlayers: MutableList<Player> , var map: Int, var state: Int, var uuid: UUID) {
    fun getMapName(): String {
        return when(map) {
            0 -> "City"
            else -> "None"
        }
    }
}
