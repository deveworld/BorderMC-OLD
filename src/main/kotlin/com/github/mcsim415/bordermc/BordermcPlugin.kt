package com.github.mcsim415.bordermc

import com.github.mcsim415.bordermc.command.BordermcCommand
import com.github.mcsim415.bordermc.data.DataManager
import com.github.mcsim415.bordermc.event.*
import com.github.mcsim415.bordermc.game.GameManager
import com.github.mcsim415.bordermc.utils.ActionBar
import com.github.mcsim415.bordermc.utils.BarUtil
import org.bukkit.*
import org.bukkit.block.Chest
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.*
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector

class BordermcPlugin: JavaPlugin(), Listener {
    lateinit var dataManager: DataManager
    lateinit var gameManager: GameManager

    override fun onEnable() {
        dataManager = DataManager()
        gameManager = GameManager(this)

        server.pluginManager.registerEvents(this, this)
        server.pluginManager.registerEvents(Menu(this), this)
        server.pluginManager.registerEvents(PlayerData(this), this)
        server.pluginManager.registerEvents(PlayerDeath(this), this)
        server.pluginManager.registerEvents(ServerLimit(this), this)
        server.pluginManager.registerEvents(ChestRandomItem(this), this)

        val voidTabCompleter = TabCompleter { _, _, _, _ -> mutableListOf() }
        getCommand("bm")!!.executor = BordermcCommand(this)
        getCommand("bm")!!.tabCompleter = voidTabCompleter
        getCommand("bmdev")!!.executor = BordermcCommand(this)
        getCommand("bmdev")!!.tabCompleter = voidTabCompleter

        val world: World = Bukkit.getWorld("world")!!
        world.isAutoSave = false
    }

    override fun onDisable() {
        dataManager.removeAllRooms()
    }
}