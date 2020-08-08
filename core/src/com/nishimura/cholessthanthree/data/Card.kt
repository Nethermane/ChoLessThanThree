package com.nishimura.cholessthanthree.data

import com.nishimura.cholessthanthree.Targetable
import kotlin.reflect.KClass

data class Card(var _cost: Int,
                var onPlay: Effect? = null,
                var onDraw: Effect? = null,
                var onDiscard: Effect? = null,
                val targets: List<KClass<out Targetable>> = emptyList()
) {
    abstract class Effect {
        open fun executeEffect(target: Targetable?) {}
    }

    class OffensiveEffect() : Effect()
    class DefensiveEffect() : Effect()
    class UtilityEffect() : Effect()

    //When this card is brought to the hand
    fun onDraw(target: Targetable?) {
        onDraw?.executeEffect(target)
    }

    //When this card is put into the discard
    fun onDiscard(target: Targetable?) {
        onDiscard?.executeEffect(target)
    }

    //When this card is played
    fun onPlay(target: Targetable?) {
        if (targets.any { clazz -> clazz.isInstance(target) } || targets.isEmpty()) {
            onPlay?.executeEffect(target)
        }
    }
}