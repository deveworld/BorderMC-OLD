package com.github.mcsim415.bordermc.data

import com.github.mcsim415.bordermc.utils.BarUtil
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import java.io.*
import java.util.*

class DataManager {
    private val rooms: HashMap<UUID, Room> = hashMapOf()
    private val taskIDs: HashMap<UUID, Int> = hashMapOf()
    private val gamePlayers: HashMap<UUID, Int> = hashMapOf()
    // 0 -> Not Playing game
    // 1 -> Wait Game
    // 2 -> Playing Game
    // 3 -> Spectating Game
    // 4 -> Exit soon
    private val maps = 0
    private val pathName = "/home/pi/Desktop/bmWorlds/"

    fun getWaitRoomUUID(): UUID {
        for (room in rooms) {
            if (room.value.state == 0) {
                return room.key
            }
        }
        addRoom((0..maps).random())
        return getWaitRoomUUID()
    }

    fun getTaskID(player: Player): Int {
        return taskIDs[player.uniqueId] ?: 0
    }

    fun setTaskID(player: Player, taskID: Int) {
        taskIDs[player.uniqueId] = taskID
    }

    fun setGamePlayer(player: Player, int: Int) {
        gamePlayers[player.uniqueId] = int
    }

    fun delGamePlayer(player: Player) {
        gamePlayers.remove(player.uniqueId)
        for (room in rooms) {
            if (room.value.players.contains(player)) {
                room.value.players.remove(player)
            }
            if (room.value.playingPlayers.contains(player)) {
                room.value.playingPlayers.remove(player)
            }
        }
    }

    fun getGamePlayer(player: Player): Int? {
        return gamePlayers[player.uniqueId]
    }

    fun respawn(player: Player, teleport: Boolean = true) {
        player.spigot().respawn()
        if (teleport) {
            player.teleport(Location(Bukkit.getWorld("world"), 0.5, 5.0, 0.5, (-90).toFloat(), (0).toFloat()), PlayerTeleportEvent.TeleportCause.COMMAND)
        }
        player.gameMode = GameMode.ADVENTURE
        player.foodLevel = 20
        player.health = 20.0
        player.inventory.clear()
        player.inventory.armorContents = null
        player.scoreboard = Bukkit.getScoreboardManager().newScoreboard
        player.allowFlight = false
        player.isFlying = false
        BarUtil.removeBar(player)
        setGamePlayer(player, 0)
        player.removePotionEffect(PotionEffectType.INVISIBILITY)
        val room = getRoomWithPlayer(player)
        if (room != null) {
            room.players.remove(player)
            room.playingPlayers.remove(player)
            if (room.players.size == 0) {
                delRoom(room)
            }
        }
        giveMenu(player)
    }

    private fun giveMenu(player: Player) {
        val playItem = ItemStack(Material.COMPASS)
        playItem.itemMeta.lore = listOf("§fClick to play game")
        playItem.itemMeta.displayName = "§0Play Game"
        player.inventory.setItem(0, playItem)
    }

    private fun addRoom(mapId: Int): UUID {
        val uuid = UUID.randomUUID()
        val room = Room(mutableListOf(), mutableListOf(), mapId, 0, uuid)
        rooms[uuid] = room
        val path = when(mapId) {
            0 -> pathName + "city/"
            else -> throw Exception("No Match World")
        }
        File(path).copyRecursively(File(Bukkit.getWorldContainer(), uuid.toString()))
        val world = WorldCreator(uuid.toString()).createWorld()
        world.isAutoSave = false
        return uuid
    }

    fun delRoom(room: Room) {
        val uuid = room.uuid
        try {
            rooms.remove(uuid)
        } catch (e: Exception) {
            Bukkit.getLogger().warning("Failed to delete room")
            Bukkit.getLogger().warning(e.toString())
        }
        Bukkit.unloadWorld(Bukkit.getWorld(uuid.toString()), false)
        File(Bukkit.getWorldContainer(), uuid.toString()).deleteRecursively()
    }

    fun removeAllRooms() {
        rooms.values.forEach {
            delRoom(it)
        }
    }

    fun joinRoom(player: Player, uuid: UUID) {
        rooms[uuid]?.players?.add(player)
        rooms[uuid]?.playingPlayers?.add(player)
        player.teleport(Bukkit.getWorld(uuid.toString()).spawnLocation.add(0.5, 0.5, 0.5))
    }

    fun getRoom(uuid: UUID): Room? {
        return rooms[uuid]
    }

    fun getRoomWithPlayer(player: Player): Room? {
        for (room in rooms.values) {
            if (room.players.contains(player)) {
                return room
            }
        }
        return null
    }
}