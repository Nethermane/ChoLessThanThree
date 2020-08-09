package com.nishimura.cholessthanthree.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.nishimura.cholessthanthree.Targetable
import com.nishimura.cholessthanthree.actors.Enemy
import kotlin.properties.Delegates
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
                private var _onPlay: List<Effect> = emptyList(),
                private var _onDiscard: List<Effect> = emptyList(),
                private var _onDraw: List<Effect> = emptyList(),
                var color: CardColor = CardColor.NEUTRAL,
                var rarity: CardRarity = CardRarity.COMMON) {
    companion object {
        val allCards = let {
            val json = Json()
            json.fromJson<ListOfCards>(ListOfCards::class.java,
                    Gdx.files.internal("cardData/CardData.json")).cards
        }
    }


    var onDraw: List<Effect> by Delegates.observable(_onDraw) { property, oldValue, newValue ->
        internalDesccription = newValue.joinToString(separator = " and ") {
            it.description.replace("{{{0}}}",it.power.toString())
        } + onPlay.joinToString(separator = " and ") {
            it.description.replace("{{{0}}}",it.power.toString())
        } + onDiscard.joinToString(separator = " and ") {
            it.description.replace("{{{0}}}",it.power.toString())
        }
    }
    var onPlay: List<Effect> by Delegates.observable(_onPlay) { property, oldValue, newValue ->
        internalDesccription = onDraw.joinToString(separator = " and ") {
            it.description.replace("{{{0}}}",it.power.toString())
        } + newValue.joinToString(separator = " and ") {
            it.description.replace("{{{0}}}",it.power.toString())
        } + onDiscard.joinToString(separator = " and ") {
            it.description.replace("{{{0}}}",it.power.toString())
        }
    }
    var onDiscard: List<Effect> by Delegates.observable(_onDiscard) { property, oldValue, newValue ->
        internalDesccription = onDraw.joinToString(separator = " and ") {
            it.description.replace("{{{0}}}",it.power.toString())
        } + onPlay.joinToString(separator = " and ") {
            it.description.replace("{{{0}}}",it.power.toString())
        } + newValue.joinToString(separator = " and ") {
            it.description.replace("{{{0}}}",it.power.toString())
        }
    }
    //I'm like 90% sure this has to be below the others so leave it here
    var internalDesccription = let {
        onDraw.joinToString(separator = " and ") {
            it.description.replace("{{{0}}}",it.power.toString())
        } + onPlay.joinToString(separator = " and ") {
            it.description.replace("{{{0}}}",it.power.toString())
        } + onDiscard.joinToString(separator = " and ") {
            it.description.replace("{{{0}}}",it.power.toString())
        }
    }
        private set
    enum class CardColor {
        NEUTRAL
    }

    enum class CardRarity {
        COMMON
    }

    class Effect() {
        constructor(effectType: EffectType, power: Int, targets: Targets?, description: String) : this() {
            this.effectType = effectType
            this.power = power
            this.targets = targets
            this.description = description
        }

        lateinit var effectType: EffectType
        var description = ""
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