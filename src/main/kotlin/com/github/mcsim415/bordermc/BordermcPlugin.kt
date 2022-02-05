package com.github.mcsim415.bordermc

import com.github.mcsim415.bordermc.command.BordermcCommand
import com.github.mcsim415.bordermc.data.DataManager
import com.github.mcsim415.bordermc.event.*
import com.github.mcsim415.bordermc.game.GameManager
import org.bukkit.*
import org.bukkit.command.TabCompleter
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class BordermcPlugin: JavaPlugin(), Listener {
    lateinit var dataManager: DataManager
    lateinit var gameManager: GameManager

    override fun onEnable() {
        dataManager = DataManager()
        gameManager = GameManager(this)

        server.pluginManager.registerEvents(this, this)
        server.pluginManager.registerEvents(MapRender(this), this)
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

//        val world: World = Bukkit.getWorld("world")!!
//        world.isAutoSave = false
    }

    override fun onDisable() {
        dataManager.removeAllRooms()
    }
}