package com.nishimura.cholessthanthree.effects

/**
 * Class to represent a status that afflicts anything
 */
data class Status(val stat: Stat, val intensity: Int, var duration: Int = 0, var downTick: Int = 1, var permanent:Boolean = false) {
    fun endTurn() {
        if(!permanent) {
            duration -= downTick
            if (duration < 0)
                duration = 0
        }
    }
    fun isOver(): Boolean {
        return !permanent && duration == 0
    }
}