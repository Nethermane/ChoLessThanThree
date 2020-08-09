package com.nishimura.cholessthanthree.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.nishimura.cholessthanthree.Enemy
import com.nishimura.cholessthanthree.Targetable
import kotlin.reflect.KClass

class ListOfCards() {
    lateinit var cards: ArrayList<Card>

    constructor(cards: ArrayList<Card>) : this() {
        this.cards = cards
    }
}

data class Card(var id: Int? = null,
                var cost: Int = 0,
                var title: String ="",
                var description: String = "",
                var onPlay: List<Effect> = emptyList(),
                var onDiscard: List<Effect> = emptyList(),
                var onDraw: List<Effect> = emptyList(),
                var color: CardColor = CardColor.NEUTRAL,
                var rarity: CardRarity = CardRarity.COMMON) {
    companion object {
        val allCards = let {
            val json = Json()
            json.fromJson<ListOfCards>(ListOfCards::class.java,
                    Gdx.files.internal("cardData/CardData.json")).cards
        }
    }

    enum class CardColor {
        NEUTRAL
    }

    enum class CardRarity {
        COMMON
    }

    class Effect() {
        constructor(effectType: EffectType, power: Int, targets: Targets?) : this() {
            this.effectType = effectType
            this.power = power
            this.targets = targets
        }

        lateinit var effectType: EffectType
        var power: Int? = null
        var targets: Targets? = null

        enum class EffectType {
            DAMAGE
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

        fun executeEffect(target: Targetable?) {}
    }

    //When this card is brought to the hand
    fun onDraw(target: Targetable?) {
        onDraw.forEach { it.executeEffect(target) }
    }

    //When this card is put into the discard
    fun onDiscard(target: Targetable?) {
        onDiscard.forEach { it.executeEffect(target) }
    }

    //When this card is played
    fun onPlay(target: Targetable?) {
        onPlay.forEach { it.executeEffect(target) }
    }
}