package com.github.mcsim415.bordermc.command

import com.github.mcsim415.bordermc.BordermcPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class BordermcCommand(plugin: BordermcPlugin): CommandExecutor {
    private val dataManager = plugin.dataManager
    private val gameManager = plugin.gameManager

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (label.equals("bm", true)) {
            if (dataManager.getGamePlayer(sender as Player) != 0) {
                sender.sendMessage("§4Already You Joined the game.")
            } else {
                gameManager.joinGame(sender)
                // https://helpch.at/docs/1.8.8/index.html?org/bukkit/WorldBorder.html
            }
        } else if (label.equals("bmdev", true)) {
            if (sender!!.isOp) {
                sender.sendMessage("§aBorderMC Debug command")
                sender.sendMessage(dataManager.getGamePlayer(sender as Player).toString())
                gameManager.startGame(dataManager.getRoomWithPlayer(sender)!!.uuid)
            } else {
                sender.sendMessage("§4This command needs OP to perform.")
            }
        }
        return true
    }
}