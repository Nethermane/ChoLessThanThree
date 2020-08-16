package com.nishimura.cholessthanthree.player

import com.nishimura.cholessthanthree.Targetable

data class AnimState(val anim: Anim? = null, val repetitions: Int = 1,
                     val animActionType: AnimActionType = AnimActionType.NONE,
                     val animDirection: AnimDirection? = null, val reversed: Boolean = false) {
    var animTime: Float = 0f
    val totalDuration: Float
        get() = (anim?.animation?.animationDuration ?: 0f) * repetitions

    var target: Targetable? = null
    fun isDone(): Boolean = animTime >= totalDuration
    fun getOverFlowedTime(): Float = animTime - totalDuration
}