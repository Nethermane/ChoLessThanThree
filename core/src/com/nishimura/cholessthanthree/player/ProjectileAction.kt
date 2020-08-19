package com.nishimura.cholessthanthree.player

data class ProjectileAction (var actionType: ProjectileActionType? = null, var delayPcnt: Float = 0f, var durationPcnt: Float = 0f, var begin: Float = 0f, var end: Float = 0f) {

    enum class ProjectileActionType {
        MOVE_END_TO_END_X, SCALE
    }
}