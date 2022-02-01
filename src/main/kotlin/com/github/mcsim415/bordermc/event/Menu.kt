package com.github.mcsim415.bordermc.event

import com.github.mcsim415.bordermc.BordermcPlugin
import com.github.mcsim415.bordermc.utils.ActionBar
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Material.*
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

class Menu(private val plugin: BordermcPlugin): Listener {
    private val gameManager = plugin.gameManager
    private val dataManager = plugin.dataManager

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val data = dataManager.getGamePlayer(event.player)
        if (data == 3 || data == 1) {
            if (event.action != Action.PHYSICAL) {
                if (event.item == ItemStack(PAPER)) {
                    val player = event.player
                    dataManager.setGamePlayer(player, 4)
                    ActionBar(plugin).sendActionBar(player, "§aPlease re click to cancel.", 20 * 3)
                    val taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
                        dataManager.respawn(player, false)
                        gameManager.joinGame(player)
                    }, 20L * 3)
                    dataManager.setTaskID(player, taskID)
                } else if (event.item == ItemStack(BED)) {
                    val player = event.player
                    if (data == 1) {
                        dataManager.setGamePlayer(player, 5)
                    } else {
                        dataManager.setGamePlayer(player, 4)
                    }
                    ActionBar(plugin).sendActionBar(player, "§aPlease re click to cancel.", 20 * 3)
                    val taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
                        dataManager.respawn(player)
                    }, 20L * 3)
                    dataManager.setTaskID(player, taskID)
                }
            }
        } else if (data == 4 || data == 5) {
            if (event.action != Action.PHYSICAL) {
                if (event.item == ItemStack(PAPER) || event.item == ItemStack(BED)) {
                    val player = event.player
                    Bukkit.getScheduler().cancelTask(dataManager.getTaskID(player))
                    if (data == 4) {
                        dataManager.setGamePlayer(player, 3)
                    } else {
                        dataManager.setGamePlayer(player, 1)
                    }
                    ActionBar(plugin).sendActionBar(player, "§4Canceled.", 20)
                }
            }
        } else if (data == 0) {
            if (event.action != Action.PHYSICAL) {
                if (event.item == ItemStack(COMPASS)) {
                    val player = event.player
                    sendGuiMenu(player)
                }
            }
        } else if (data != 2) {
            event.isCancelled = true
        }
    }

    private fun sendGuiMenu(player: Player) {
        val gui = Bukkit.createInventory(player, 9, "§aBorderMC")

        val border = ItemStack(BARRIER)

        val borderMeta = border.itemMeta
        borderMeta.displayName = "§aWorld Border Game"
        val borderLore = listOf("§7BorderMC's Main Game.", "§1Click to join.")
        borderMeta.lore = borderLore
        border.itemMeta = borderMeta

        gui.setItem(4, border)
        player.openInventory(gui)
    }

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val data = dataManager.getGamePlayer(event.whoClicked as Player)
        if (data == 0 || data == 4) {
            event.isCancelled = true
            if (data == 0) {
                val player = event.whoClicked as Player
                when (event.currentItem.type) {
                    BARRIER -> gameManager.joinGame(player)
                    else -> return
                }
                player.closeInventory()
            }
        }
    }
}