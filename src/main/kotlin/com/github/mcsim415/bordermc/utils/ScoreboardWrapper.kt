package com.github.mcsim415.bordermc.utils

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard


/**
 * ScoreboardWrapper is a class that wraps Bukkit Scoreboard API
 * and makes your life easier.
 */
class ScoreboardWrapper(title: String?) {
    /**
     * Gets the Bukkit Scoreboard.
     */
    private val scoreboard: Scoreboard = Bukkit.getScoreboardManager().newScoreboard
    private val objective: Objective
    private val modifies: MutableList<String> = ArrayList(MAX_LINES)

    /**
     * Sets the scoreboard title.
     */
    fun setTitle(title: String?) {
        objective.displayName = title
    }

    /**
     * Modifies the line with §r strings in the way to add
     * a line equal to another.
     */
    private fun getLineCoded(line: String): String {
        var result = line
        while (modifies.contains(result)) result += ChatColor.RESET
        return result.substring(0, 40.coerceAtMost(result.length))
    }

    /**
     * Adds a new line to the scoreboard. Throw an error if the lines count are higher than 16.
     */
    fun addLine(line: String) {
        if (modifies.size > MAX_LINES) throw IndexOutOfBoundsException("You cannot add more than 16 lines.")
        val modified = getLineCoded(line)
        modifies.add(modified)
        objective.getScore(modified).score = -modifies.size
    }

    /**
     * Adds a blank space to the scoreboard.
     */
    fun addBlankSpace() {
        addLine(" ")
    }

    /**
     * Sets a scoreboard line to an exact index (between 0 and 15).
     */
    fun setLine(index: Int, line: String) {
        if (index < 0 || index >= MAX_LINES) throw IndexOutOfBoundsException("The index cannot be negative or higher than 15.")
        val oldModified = modifies[index]
        scoreboard.resetScores(oldModified)
        val modified = getLineCoded(line)
        modifies[index] = modified
        objective.getScore(modified).score = -(index + 1)
    }

    fun getScoreboard(): Scoreboard {
        return scoreboard
    }

    /**
     * Just for debug.
     */
    override fun toString(): String {
        var out = ""
        val i = 0
        for (string in modifies) out += (-(i + 1)).toString() + ")-> " + string + ";\n"
        return out
    }

    companion object {
        const val MAX_LINES = 16
    }

    /**
     * Instantiates a new ScoreboardWrapper with a default title.
     */
    init {
        objective = scoreboard.registerNewObjective(title, "dummy")
        objective.displayName = title
        objective.displaySlot = DisplaySlot.SIDEBAR
    }
}