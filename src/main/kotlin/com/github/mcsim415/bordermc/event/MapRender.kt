package com.github.mcsim415.bordermc.event

import com.github.mcsim415.bordermc.BordermcPlugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.MapInitializeEvent
import org.bukkit.map.MapView


class MapRender(plugin: BordermcPlugin): Listener {
    private val dataManager = plugin.dataManager

    @EventHandler
    fun onMapInitialize(event: MapInitializeEvent) {
        val map = event.map
        map.scale = MapView.Scale.NORMAL
//        map.addRenderer(object : MapRenderer() {
//            override fun render(map: MapView, canvas: MapCanvas, player: Player) {
//                val room = dataManager.getRoomWithPlayer(player)
//                if (room != null) {
//                    val world = Bukkit.getWorld(room.uuid)
//                    val center = world.worldBorder.center
//                    val radius = world.worldBorder.size / 2
//                    val startX = (center.x - radius).toInt()
//                    val startZ = (center.z - radius).toInt()
//                    val endX = (center.x + radius).toInt()
//                    val endZ = (center.z + radius).toInt()
//                    for (x in startX..startZ) {
//                        for (z in endX..endZ) {
//                            canvas.setPixel(x, z, MapPalette.resolveColor(0xFF0000))
//                        }
//                    }
//                    canvas.setPixel(x, y, MapPalette.resolveColor(color))
//                }
//            }
//        })
    }
}