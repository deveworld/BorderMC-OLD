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
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerAnimationEvent
import org.bukkit.event.player.PlayerAnimationType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack


class Menu(private val plugin: BordermcPlugin): Listener {
    private val gameManager = plugin.gameManager
    private val dataManager = plugin.dataManager

    @EventHandler
    fun onInteract(event: PlayerInteractEvent) {
        val data = dataManager.getGamePlayer(event.player)
        if (data != 2) {
            val player = event.player
            val action = event.action
            event.isCancelled = true
            if (event.item != null) {
                val item = event.item.type
                if (action != Action.LEFT_CLICK_BLOCK && action != Action.LEFT_CLICK_AIR) {
                    onItemClick(player, data, item)
                }
            }
        }
    }

    private fun onItemClick(player: Player, data: Int, item: Material) {
        if (data == 3 || data == 1) {
            if (item == PAPER) {
                onItemClick(player, 3)
            } else if (item == BED) {
                onItemClick(player, 1)
            }
        } else if (data == 4 || data == 5) {
            if (item == PAPER || item == BED) {
                onItemClick(player, 4)
            }
        } else if (data == 0) {
            if (item == COMPASS) {
                onItemClick(player, 2)
            }
        }
    }

    @EventHandler
    fun onClick(event: PlayerAnimationEvent) {
        if (event.animationType == PlayerAnimationType.ARM_SWING) {
            val player = event.player
            val data = dataManager.getGamePlayer(player)
            if (data != 2) {
                if (player.itemInHand != null) {
                    val item = player.itemInHand.type
                    onItemClick(player, data, item)
                }
            }
        }
    }

    /**
     * @param item 1: Bed, 2: Compass, 3: Paper, 4: Cancel
     */
    private fun onItemClick(player: Player, item: Int) {
        val data = dataManager.getGamePlayer(player)
        when (item) {
            1 -> {
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
            2 -> {
                sendGuiMenu(player)
            }
            3 -> {
                dataManager.setGamePlayer(player, 4)
                ActionBar(plugin).sendActionBar(player, "§aPlease re click to cancel.", 20 * 3)
                val taskID = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, {
                    dataManager.respawn(player, false)
                    gameManager.joinGame(player)
                }, 20L * 3)
                dataManager.setTaskID(player, taskID)
            }
            4 -> {
                Bukkit.getScheduler().cancelTask(dataManager.getTaskID(player))
                if (data == 4) {
                    dataManager.setGamePlayer(player, 3)
                } else {
                    dataManager.setGamePlayer(player, 1)
                }
                ActionBar(plugin).sendActionBar(player, "§4Canceled.", 20)
            }
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
                player.closeInventory()
                if (event.currentItem != null) {
                    when (event.currentItem.type) {
                        BARRIER -> gameManager.joinGame(player)
                        else -> return
                    }
                }
            }
        }
    }
}