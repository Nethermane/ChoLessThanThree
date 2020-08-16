package com.nishimura.cholessthanthree.card

import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.GdxRuntimeException
import com.nishimura.cholessthanthree.Damageable
import com.nishimura.cholessthanthree.Targetable
import com.nishimura.cholessthanthree.actors.Enemy
import com.nishimura.cholessthanthree.player.Player
import com.nishimura.cholessthanthree.player.AnimState
import com.nishimura.cholessthanthree.sumByFloat
import kotlin.reflect.KClass

class Effect() {
    constructor(effectType: EffectType, power: Int, targets: Targets?, description: String,
                repeat: Int?, animations: List<AnimState>?) : this() {
        this.effectType = effectType
        this.power = power
        this.targets = targets
        this.description = description
        this.repeat = repeat
        this.animations = animations
    }

    lateinit var effectType: EffectType
    var description = ""
    var power: Int = 0
    var repeat: Int? = null
    var targets: Targets? = null
    var animations: List<AnimState>? = null

    enum class EffectType {
        DAMAGE, BLOCK_SELF, DAMAGE_FRONT
    }

    enum class Targets {
        ENEMY;

        fun getClassesForTarget(): List<KClass<out Targetable>> {
            return when (this) {
                ENEMY -> {
                    listOf(Enemy::class)
                }
            }
        }
    }

    fun executeEffect(target: Targetable?) {
        animations?.let {
            Player.executeStates(it, target)
        }
        val totalTime: Float = animations?.sumByFloat { it.totalDuration*it.repetitions } ?: 0f
        Player.addAction(Actions.delay(totalTime, Actions.run {
            when (this.effectType) {
                EffectType.DAMAGE -> {
                    if (target is Damageable) {
                        target.currentHealth -= this.power
                    } else {
                        throw GdxRuntimeException("Attempting to damage the undamageable class " + target?.javaClass?.name)
                    }
                }
                EffectType.BLOCK_SELF -> {
                    Player.block += this.power
                }
            }
        }))
    }
}