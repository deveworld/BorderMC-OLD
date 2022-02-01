package com.github.mcsim415.bordermc.data

import org.bukkit.entity.Player
import java.util.*

data class Room(var players: MutableList<Player>, var playingPlayers: MutableList<Player> , var map: Int, var state: Int, var uuid: UUID) {
    fun getMapName(): String {
        return when(map) {
            0 -> "City"
            else -> "None"
        }
    }
}
