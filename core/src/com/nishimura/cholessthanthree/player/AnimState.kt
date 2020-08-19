package com.nishimura.cholessthanthree.player

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.Targetable
import com.nishimura.cholessthanthree.toDegree
import kotlin.math.atan2

data class AnimState(val anim: Anim? = null, val repetitions: Int = 1,
                     val animActionType: AnimActionType = AnimActionType.NONE,
                     val animDirection: AnimDirection? = null, val reversed: Boolean = false,
                     val projectileData: ProjectileData? = null) {
    val projectile by lazy {
        projectileData?.let {
            Projectile(projectileData)
        }
    }
    var animTime: Float = 0f
    val totalDuration: Float
        get() = (anim?.animation?.animationDuration ?: 0f) * repetitions
    val duration: Float
        get() = (anim?.animation?.animationDuration ?: 0f)
    var target: Targetable? = null
    fun isDone(): Boolean = animTime >= totalDuration
    fun getOverFlowedTime(): Float = animTime - totalDuration

    //Angle is in degrees
    inner class Projectile(private val projectileData: ProjectileData)
        : Image(Assets.atlasSkin.getDrawable(projectileData.imageString)) {
        init {
            width = 100f
            height = 100f
            color = Color.RED
            setOrigin(width / 2, height / 2)
            debug = true
        }

        //Use Align.whatever for the align target
        fun setTarget(startCharacter: Actor, target: Targetable, projectileMovePattern: ProejctileMovePattern = ProejctileMovePattern.DIRECT, alignTarget: Int = Align.center) {
            startCharacter.stage.addActor(this)
            with(projectileData) {
                x = startingXPcnt * startCharacter.width + startCharacter.x - width/2
                y = startingYPcnt * startCharacter.height + startCharacter.y
                rotation = startCharacter.rotation + startingAngle
                val finalAngle = atan2(target.getTargetY() - y, target.getTargetX() - x).toDegree()
                for (projectileAction in this.preReleaseActions) {
                    val action = Actions.delay(projectileAction.delayPcnt * duration,
                            when (projectileAction.actionType) {
                                ProjectileAction.ProjectileActionType.SCALE -> {
                                    addAction(Actions.sequence(Actions.scaleTo(projectileAction.begin,projectileAction.begin)))
                                    Actions.scaleTo(projectileAction.end, projectileAction.end, projectileAction.durationPcnt * duration)
                                }
                                ProjectileAction.ProjectileActionType.MOVE_END_TO_END_X -> {
                                        Actions.moveTo(startCharacter.x+startCharacter.width-width/2,y,projectileAction.durationPcnt*duration)
                                }
                                else -> null
                            }
                    )
                    addAction(action)
                }
                val action = when (projectileMovePattern) {
                    ProejctileMovePattern.DIRECT -> {
                        Actions.delay(delayPcnt * totalDuration, Actions.parallel(Actions.moveToAligned(target.getTargetX(), target.getTargetY(), alignTarget, travelDuration),
                                Actions.rotateTo(finalAngle, travelDuration)))
                    }
                    //Same thing for now
                    else -> {
                        Actions.delay(delayPcnt * totalDuration, Actions.parallel(Actions.moveToAligned(target.getTargetX(), target.getTargetY(), alignTarget, travelDuration),
                                Actions.rotateTo(finalAngle, travelDuration)))
                    }
                }
                addAction(Actions.sequence(action, Actions.run {
                    remove()
                    clear()
                }))
            }
        }

    }

    enum class ProejctileMovePattern {
        DIRECT, PARABOLIC
    }
}