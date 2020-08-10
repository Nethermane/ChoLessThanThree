package com.nishimura.cholessthanthree.player

data class AnimState(val state: State? = null, val repetitions: Int = 1) {
    var animTime: Float = 0f
    val totalDuration: Float
        get() = (state?.animation?.animationDuration ?: 0f) * repetitions

    fun isDone(): Boolean = totalDuration >= animTime
    fun getOverFlowedTime(): Float = totalDuration - animTime
}