package com.github.mcsim415.bordermc.utils;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Bukkit/Spigot/Bungee Java Plugin Maker.
 * EnervateD. All rights reserved.
 */
public class BarUtil {

    private static final Map<String, EntityEnderDragon> dragons = new ConcurrentHashMap<>();

    public static void setBar(Player p, String text, float healthPercent) {
        WorldServer world = ((CraftWorld) p.getLocation().getWorld()).getHandle();

        EntityEnderDragon dragon = new EntityEnderDragon(world);
        dragon.setLocation(0, -80, 0, 0, 0);

        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(dragon);

        DataWatcher watcher = new DataWatcher(null);
        watcher.a(0, (byte) 0x20); // invisible
        watcher.a(6, (healthPercent / 100) * 200); // health
        watcher.a(10, text); // name
        watcher.a(2, text); // name
        watcher.a(11, (byte) 1); // IDK
        watcher.a(3, (byte) 1); // maybe nothing
        watcher.a(8, (byte) 0); // invisible, too..?

        /*
          Error
          java.lang.NullPointerException
                  at net.minecraft.server.v1_8_R3.DataWatcher.a(DataWatcher.java:33) ~[patched.jar:git-PaperSpigot-"4c7641d"]
                  at com.github.mcsim415.bordermc.utils.BarUtil.setBar(BarUtil.java:34) ~[?:?]
                  at com.github.mcsim415.bordermc.utils.BarUtil.updateBar(BarUtil.java:95) ~[?:?]
                  at com.github.mcsim415.bordermc.utils.BarUtil.updateHealth(BarUtil.java:75) ~[?:?]
                  at com.github.mcsim415.bordermc.game.GameManager.phase$lambda-2(GameManager.kt:146) ~[?:?]
                  at org.bukkit.craftbukkit.v1_8_R3.scheduler.CraftTask.run(CraftTask.java:59) ~[patched.jar:git-PaperSpigot-"4c7641d"]
                  at org.bukkit.craftbukkit.v1_8_R3.scheduler.CraftScheduler.mainThreadHeartbeat(CraftScheduler.java:352) [patched.jar:git-PaperSpigot-"4c7641d"]
                  at net.minecraft.server.v1_8_R3.MinecraftServer.B(MinecraftServer.java:783) [patched.jar:git-PaperSpigot-"4c7641d"]
                  at net.minecraft.server.v1_8_R3.DedicatedServer.B(DedicatedServer.java:378) [patched.jar:git-PaperSpigot-"4c7641d"]
                  at net.minecraft.server.v1_8_R3.MinecraftServer.A(MinecraftServer.java:713) [patched.jar:git-PaperSpigot-"4c7641d"]
                  at net.minecraft.server.v1_8_R3.MinecraftServer.run(MinecraftServer.java:616) [patched.jar:git-PaperSpigot-"4c7641d"]
                  at java.lang.Thread.run(Thread.java:748) [?:1.8.0_311]
         */

        try{
            Field t = PacketPlayOutSpawnEntityLiving.class.getDeclaredField("l");
            t.setAccessible(true);
            t.set(packet, watcher);
        } catch(Exception ex){
            ex.printStackTrace();
        }

        dragons.put(p.getUniqueId().toString(), dragon);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    public static void removeBar(Player p) {
        if(existsBar(p)) {
            PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(dragons.get(p.getUniqueId().toString()).getId());
            dragons.remove(p.getUniqueId().toString());
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public static Boolean existsBar(Player p) {
        return dragons.containsKey(p.getUniqueId().toString());
    }

    public static void updateHealth(Player p, float healthPercent) {
        updateBar(p, null, healthPercent);
    }

    public static void updateBar(Player p, String text, float healthPercent) {
        if(existsBar(p)) {
            DataWatcher watcher = new DataWatcher(null);
            watcher.a(0, (byte) 0x20); // invisible
            if (healthPercent != -1) {
                watcher.a(6, (healthPercent / 100) * 200);
            }
            if (text != null) {
                watcher.a(2, text);
            }
            watcher.a(11, (byte) 1);
            watcher.a(3, (byte) 1);
            watcher.a(8, (byte) 0);

            PacketPlayOutEntityMetadata packet = new PacketPlayOutEntityMetadata(dragons.get(p.getUniqueId().toString()).getId(), watcher, true);
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
        }
    }

}
