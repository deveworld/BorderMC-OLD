package com.github.mcsim415.bordermc.event

import com.github.mcsim415.bordermc.BordermcPlugin
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkLoadEvent
import org.bukkit.inventory.ItemStack

class ChestRandomItem(private val plugin: BordermcPlugin): Listener {

    @EventHandler
    fun chunkLoad(event: ChunkLoadEvent) {
        if (event.world.name != "world") {
            Bukkit.getScheduler().runTaskAsynchronously(plugin) {
                for (state in event.chunk.tileEntities) {
                    if (state is Chest) {
                        if ((1..2).random() == 1) {
                            fillChest(state)
                        }
                    }
                }
            }
        }
    }

    private fun fillChest(chest: Chest) {
        val items = randomItem((3..5).random())
        chest.blockInventory.clear()
        val random = (0..26).toMutableList()
        for (item in items) {
            val index = random.random()
            random.remove(index)
            chest.blockInventory.setItem(index, item)
        }
    }

    private fun randomItem(amount: Int): MutableList<ItemStack> {
        val items: MutableList<ItemStack> = mutableListOf()
        var item: ItemStack
        for (i in 1 until amount) {
            item = when((1..6).random()) { // 무기, 방어, 아이템
                1 -> when((1..11).random()) { // 나무, 돌, 철, 금, 다이아
                    1, 2, 3 -> when((1..3).random()) { // 나무 검, 나무 도끼
                        1, 2 -> ItemStack(Material.WOOD_SWORD)
                        3 -> ItemStack(Material.WOOD_AXE)
                        else -> ItemStack(Material.AIR)
                    }
                    4, 5, 6 -> when((1..3).random()) { // 돌 검, 돌 도끼
                        1, 2 -> ItemStack(Material.STONE_SWORD)
                        3 -> ItemStack(Material.STONE_AXE)
                        else -> ItemStack(Material.AIR)
                    }
                    7, 8 -> when((1..3).random()) { // 철 검, 철 도끼
                        1, 2 -> ItemStack(Material.IRON_SWORD)
                        3 -> ItemStack(Material.IRON_AXE)
                        else -> ItemStack(Material.AIR)
                    }
                    9, 10 -> when((1..3).random()) { // 금 검, 금 도끼
                        1, 2 -> ItemStack(Material.GOLD_SWORD)
                        3 -> ItemStack(Material.GOLD_AXE)
                        else -> ItemStack(Material.AIR)
                    }
                    11 -> when((1..3).random()) { // 다이아 검, 다이아 도끼
                        1, 2 -> ItemStack(Material.DIAMOND_SWORD)
                        3 -> ItemStack(Material.DIAMOND_AXE)
                        else -> ItemStack(Material.AIR)
                    }
                    else -> ItemStack(Material.AIR)
                }
                2, 3 -> when((1..8).random()) { // 가죽, 금, 철, 다이아
                    1, 2, 3 -> when((1..7).random()) { // 가죽 헬멧, 가죽 경갑, 가죽 다리보호구, 가죽 부츠
                        1, 2, 3 -> ItemStack(Material.LEATHER_HELMET)
                        4 -> ItemStack(Material.LEATHER_CHESTPLATE)
                        5, 6 -> ItemStack(Material.LEATHER_LEGGINGS)
                        7 -> ItemStack(Material.LEATHER_BOOTS)
                        else -> ItemStack(Material.AIR)
                    }
                    4, 5 -> when((1..7).random()) { // 금 헬멧, 금 경갑, 금 다리보호구, 금 부츠
                        1, 2, 3 -> ItemStack(Material.GOLD_HELMET)
                        4 -> ItemStack(Material.GOLD_CHESTPLATE)
                        5, 6 -> ItemStack(Material.GOLD_LEGGINGS)
                        7 -> ItemStack(Material.GOLD_BOOTS)
                        else -> ItemStack(Material.AIR)
                    }
                    6, 7 -> when((1..7).random()) { // 철 헬멧, 철 경갑, 철 다리보호구, 철 부츠
                        1, 2, 3 -> ItemStack(Material.IRON_HELMET)
                        4 -> ItemStack(Material.IRON_CHESTPLATE)
                        5, 6 -> ItemStack(Material.IRON_LEGGINGS)
                        7 -> ItemStack(Material.IRON_BOOTS)
                        else -> ItemStack(Material.AIR)
                    }
                    8 -> when((1..7).random()) { // 다이아 헬멧, 다이아 경갑, 다이아 다리보호구, 다이아 부츠
                        1, 2, 3 -> ItemStack(Material.DIAMOND_HELMET)
                        4 -> ItemStack(Material.DIAMOND_CHESTPLATE)
                        5, 6 -> ItemStack(Material.DIAMOND_LEGGINGS)
                        7 -> ItemStack(Material.DIAMOND_BOOTS)
                        else -> ItemStack(Material.AIR)
                    }
                    else -> ItemStack(Material.AIR)
                }
                4, 5, 6 -> when((1..5).random()) {
                    1, 2 -> ItemStack(Material.PAPER)
                    3, 4 -> ItemStack(Material.COOKED_BEEF, 16)
                    5 -> ItemStack(Material.WOOL, 32)
                    else -> ItemStack(Material.AIR)
                }
                else -> ItemStack(Material.AIR)
            }
            items.add(item)
        }
        return items
    }

}