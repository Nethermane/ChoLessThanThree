package com.nishimura.cholessthanthree

import com.nishimura.cholessthanthree.actors.CombatDeckManager

@ExperimentalStdlibApi
class CombatManager(private val playerState: PlayerState, private val combatDeckManager: CombatDeckManager) {
    enum class STATE {
        DRAWING,REST,DISCARDING, PLAYING,AI_RESPONDING,
    }
    var state = STATE.DRAWING
    init {
        combatDeckManager.beginTurn()
    }
}