package com.nishimura.cholessthanthree

data class Card(private var cost: Int,
                private var onPlay: Effect? = null,
                private var onDraw: Effect? = null,
                private var onDiscard: Effect? = null,
                private var targets: List<Class<Targetable>> = emptyList()
): Targetable {
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
        if(targets.any { clazz -> clazz.isInstance(target)} || targets.isEmpty()) {
            onPlay?.executeEffect(target)
        }
    }

    abstract class Effect {
        open fun executeEffect(target: Targetable?) {}
    }

    class OffensiveEffect() : Effect()
    class DefensiveEffect() : Effect()
    class UtilityEffect() : Effect()
}
