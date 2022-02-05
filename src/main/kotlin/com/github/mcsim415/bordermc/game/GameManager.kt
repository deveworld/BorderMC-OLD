package com.github.mcsim415.bordermc.game

import com.github.mcsim415.bordermc.BordermcPlugin
import com.github.mcsim415.bordermc.data.Room
import com.github.mcsim415.bordermc.utils.BarUtil
import com.github.mcsim415.bordermc.utils.ScoreboardWrapper
import org.bukkit.*
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.Scoreboard
import java.util.*


class GameManager(private val plugin: BordermcPlugin) {
    private val dataManager = plugin.dataManager

    private fun updateRoom(uuid: UUID) {
        val room = dataManager.getRoom(uuid)!!
        if (room.players.size == 12) {
            startGame(uuid)
        } else {
            sendScoreboard(room, getScoreboard(room.getMapName(), room.playingPlayers.size))
        }
    }

    fun onQuit(player: Player) {
        val room = dataManager.getRoomWithPlayer(player)
        if (room != null) {
            if (room.state == 2) {
                room.players.remove(player)
                room.playingPlayers.remove(player)
                if (room.playingPlayers.size != 0) {
                    for (players in room.players) {
                        players.sendMessage("§f${player.name} §fhas left the game")
                    }
                }
                if (room.playingPlayers.size == 1) {
                    onWin(room)
                } else if (room.playingPlayers.size == 0) {
                    dataManager.delRoom(room)
                }
            } else {
                room.state = 0
                sendScoreboard(room, getScoreboard(room.getMapName(), room.playingPlayers.size))
            }
        }
    }

    fun joinGame(player: Player) {
        val uuid = dataManager.getWaitRoomUUID()
        player.foodLevel = 20
        player.health = 20.0
        player.inventory.clear()
        dataManager.setGamePlayer(player, 1)
        dataManager.joinRoom(player, uuid)
        updateRoom(uuid)
        giveItem(player, false)
    }

    fun startGame(uuid: UUID) {
        val room = dataManager.getRoom(uuid)!!
        room.state = 1
        var time = 5
        val mapName = room.getMapName()
        val taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            sendScoreboard(room, getScoreboard(mapName, room.playingPlayers.size, time.toLong()), true)
            time--
        }, 0L, 20L)
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
            Bukkit.getScheduler().cancelTask(taskID)
            if (room.state == 1) {
                val world = Bukkit.getWorld(uuid.toString())
                val spreadRange = (-150..150)

                for (player in room.players) {
                    val x = spreadRange.random(); val z = spreadRange.random()
                    for (y in 20 downTo 1) {
                        val block = world.getBlockAt(x, y, z)
                        if (block.type != Material.AIR) {
                            player.teleport(block.location.add(0.5, 1.0, 0.5), PlayerTeleportEvent.TeleportCause.COMMAND)
                            break
                        }
                    }
                    player.gameMode = GameMode.SURVIVAL
                    dataManager.setGamePlayer(player, 2)
                    player.inventory.clear()
                    player.inventory.armorContents = null
                }
                sendScoreboard(room, getScoreboard(mapName, room.playingPlayers.size, null, "§fRemain Players: §a${room.playingPlayers.size}"))

                when(room.map) {
                    0 -> {
                        for (x in -6..6) {
                            for (y in 98..104) {
                                for (z in -6..6) {
                                    val block: Block = world.getBlockAt(x, y, z)
                                    block.type = Material.AIR
                                }
                            }
                        }
                    }
                }

                room.state = 2
                changeWorldBorder(room, world, 0)
            } else {
                sendScoreboard(room, getScoreboard(mapName, room.playingPlayers.size))
            }
        }, 20L * 6)
    }

    private fun changeWorldBorder(room: Room, world: World, phase: Int) {
        if (world.worldFolder.exists()) {
            when(phase) {
                0 -> {
                    world.worldBorder.center = world.spawnLocation
                    world.worldBorder.size = 300.0
                    world.worldBorder.damageBuffer = 1.0
                    world.worldBorder.damageAmount = 0.0
                    world.worldBorder.warningDistance = 25
                    world.worldBorder.warningTime = 20

                    changeWorldBorder(room, world, 1)
                }
                1 -> {
                    phase(room, world, phase, 180, 260.0, 45, 0.05, 20)
                }
                2 -> {
                    phase(room, world, phase, 150, 200.0, 60, 0.1, 15)
                }
                3 -> {
                    phase(room, world, phase, 120, 160.0, 35, 0.15, 13)
                }
                4 -> {
                    phase(room, world, phase, 60, 100.0, 38, 0.15, 10)
                }
                5 -> {
                    phase(room, world, phase, 50, 60.0, 22, 0.2, 7)
                }
                6 -> {
                    phase(room, world, phase, 30, 20.0, 20, 0.3, 5)
                }
                else -> {
                    for (player in room.players) {
                        BarUtil.setBar(player, "§fLast Phase Done!", 100f)
                    }
                }
            }
        }
    }

    private fun updateBar(room: Room, loadTime: Long) {
        var time = loadTime
        room.taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            val percentage = (time.toFloat() / loadTime.toFloat())*100f
            for (player in room.players) {
                if (player.isOnline && BarUtil.existsBar(player)) {
                    BarUtil.updateHealth(player, percentage)
                } else {
                    dataManager.delRoom(room)
                    break
                }
            }
            time -= 1
        }, 0L, 20L)
    }

    private fun phase(room: Room, world: World, phase: Int, waitingTime: Long, size: Double, shrinkTime: Long, damageAmount: Double, warningDistance: Int) {
        val worldBorder = world.worldBorder
        if (phase == 1) {
            for (player in room.players) {
                BarUtil.setBar(player, "§fPhase $phase - Waiting shrink", 100f)
            }
        }
        updateBar(room, waitingTime)

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
            if (room.taskID != -1) {
                Bukkit.getScheduler().cancelTask(room.taskID)
                room.taskID = -1
                for (player in room.players) {
                    BarUtil.removeBar(player)
                    BarUtil.setBar(player, "§fPhase $phase - Shrinking", 100f)
                }

                moveWorldBorder(worldBorder, size, shrinkTime, damageAmount, warningDistance)

                updateBar(room, shrinkTime)

                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
                    if (room.taskID != -1) {
                        Bukkit.getScheduler().cancelTask(room.taskID)
                        room.taskID = -1
                        for (player in room.players) {
                            BarUtil.removeBar(player)
                            if (phase != 6) {
                                BarUtil.setBar(player, "§fPhase ${phase + 1} - Waiting shrink", 100f)
                            }
                        }
                        changeWorldBorder(room, world, phase + 1)
                    }
                }, 20L * shrinkTime)
            }
        }, 20L * waitingTime)
    }

    private fun moveWorldBorder(worldBorder: WorldBorder, size: Double, shrinkTime: Long, damageAmount: Double, warningDistance: Int) {
        worldBorder.damageAmount = damageAmount
        worldBorder.warningDistance = warningDistance

        val renewalTime = (shrinkTime / 0.1).toInt() //       shrinkTime / 0.1 sec ==> shrink center renewal time
        val newCenter = newCenter(worldBorder.size, worldBorder.center, size)
        val x = worldBorder.center.x
        val z = worldBorder.center.z
        val renewalX = (worldBorder.center.x - newCenter.x) / renewalTime //      move center per 0.1 sec
        val renewalZ = (worldBorder.center.z - newCenter.z) / renewalTime

        worldBorder.setSize(size, shrinkTime)
        for (i in 1..renewalTime) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, {
                worldBorder.setCenter(x - (renewalX * i), z - (renewalZ * i))
            }, i * 2L)
        }
        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, {
            worldBorder.setCenter(newCenter.x, newCenter.z)
        }, shrinkTime * 20L)
    }

    private fun newCenter(size: Double, center: Location, newSize: Double): Location {
        val padding = (size - newSize) / 2
        val x1 = (center.x - padding).toInt()
        val z1 = (center.z - padding).toInt()
        val x2 = (center.x + padding).toInt()
        val z2 = (center.z + padding).toInt()
        center.x = (x1..x2).random().toDouble()
        center.z = (z1..z2).random().toDouble()
        return center
    }

    fun onKill(killer: Player, victim: Player) {
        val room = dataManager.getRoomWithPlayer(killer)
        spectate(victim)
        if (room != null) {
            room.playingPlayers.remove(victim)
            val scoreboard = getScoreboard(room.getMapName(), room.playingPlayers.size, null, "§fRemain Players: §a${room.playingPlayers.size}")
            firework(killer)
            for (player in room.players) {
                player.scoreboard = scoreboard
                player.sendMessage("§f${killer.name} §fhas killed §f${victim.name}")
            }
            if (room.playingPlayers.size == 1) {
                onWin(room)
            } else if (room.playingPlayers.size == 0) {
                dataManager.delRoom(room)
            }
        }
    }

    fun onDeath(cause: String, victim: Player) {
        val room = dataManager.getRoomWithPlayer(victim)
        spectate(victim)
        if (room != null) {
            room.playingPlayers.remove(victim)
            val scoreboard = getScoreboard(room.getMapName(), room.playingPlayers.size, null, "§fRemain Players: §a${room.playingPlayers.size}")
            for (player in room.players) {
                player.scoreboard = scoreboard
                player.sendMessage("§f${victim.name} §fhas died because of §f$cause")
            }
            if (room.playingPlayers.size == 1) {
                onWin(room)
            } else if (room.playingPlayers.size == 0) {
                dataManager.delRoom(room)
            }
        }
    }

    private fun spectate(player: Player) {
        player.gameMode = GameMode.ADVENTURE
        player.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false, false))
        player.allowFlight = true
        player.isFlying = true

        player.foodLevel = 20
        player.health = 20.0
        player.inventory.clear()
        player.inventory.armorContents = null
        dataManager.setGamePlayer(player, 3)

        giveItem(player)
    }

    private fun giveItem(player: Player, replay: Boolean = true) {
        if (replay) {
            val replayItem = ItemStack(Material.PAPER)
            replayItem.itemMeta.lore = listOf("§fClick to replay")
            replayItem.itemMeta.displayName = "§0Replay"
            player.inventory.setItem(6, replayItem)
        }

        val quitItem = ItemStack(Material.BED)
        quitItem.itemMeta.lore = listOf("§fClick to quit")
        quitItem.itemMeta.displayName = "§0Quit"
        player.inventory.setItem(8, quitItem)
    }
    
    private fun onWin(room: Room) {
        for (player in room.playingPlayers) {
            BarUtil.removeBar(player)
        }
        val winner = room.playingPlayers.first()
        winner.scoreboard = getScoreboard(room.getMapName(), room.playingPlayers.size, null, "§aYou Win!")
        winner.sendMessage("§aYou Win!")
        firework(winner, true)
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
            spectate(winner)
        }, 20L * 3)
    }

    private fun firework(player: Player, win: Boolean = false) {
        val firework = Bukkit.getWorld(player.world.name).spawn(player.location, org.bukkit.entity.Firework::class.java)
        val fw = firework.fireworkMeta
        fw.power = 1
        if (win) {
            fw.addEffect(FireworkEffect.builder().withColor(Color.GREEN).with(FireworkEffect.Type.CREEPER).build())
        } else {
            fw.addEffect(FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.STAR).build())
        }
        firework.fireworkMeta = fw
        firework.detonate()
    }

    private fun sendScoreboard(room: Room, scoreboard: Scoreboard, playSound: Boolean = false) {
        for (player in room.players) {
            player.scoreboard = scoreboard
            if (playSound) {
                player.playSound(player.location, Sound.ORB_PICKUP, 1.0f, 1.0f)
            }
        }
    }

    private fun getScoreboard(mapName: String, players: Int, remainTime: Long? = null, customText: String? = null): Scoreboard {
        val scoreboardWrapper = ScoreboardWrapper("§l§eBorderMC")
        scoreboardWrapper.addLine("§702/05/2022")
        scoreboardWrapper.addBlankSpace()
        scoreboardWrapper.addLine("§fMap: §a$mapName")
        if (customText === null) {
            scoreboardWrapper.addLine("§fPlayers: §a${players}/16")
            scoreboardWrapper.addBlankSpace()
            if (remainTime === null) {
                scoreboardWrapper.addLine("§fWaiting for players...")
            } else {
                if (remainTime == 0L) {
                    scoreboardWrapper.addLine("§eGo!")
                } else {
                    scoreboardWrapper.addLine("§eStart at $remainTime")
                }
            }
        } else {
            scoreboardWrapper.addBlankSpace()
            scoreboardWrapper.addLine(customText)
        }
        scoreboardWrapper.addBlankSpace()
        scoreboardWrapper.addLine("§ebordermc.games")
        return scoreboardWrapper.getScoreboard()
    }
}