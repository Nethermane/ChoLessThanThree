package com.nishimura.cholessthanthree.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.Json
import com.nishimura.cholessthanthree.Damageable
import com.nishimura.cholessthanthree.PlayerState
import com.nishimura.cholessthanthree.Targetable
import com.nishimura.cholessthanthree.actors.Enemy
import com.nishimura.cholessthanthree.actors.Player
import com.nishimura.cholessthanthree.player.AnimState
import com.nishimura.cholessthanthree.sumByFloat
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
                var title: String = "",
                private var _onPlay: List<Effect> = emptyList(),
                private var _onDiscard: List<Effect> = emptyList(),
                private var _onDraw: List<Effect> = emptyList(),
                var color: CardColor = CardColor.NEUTRAL,
                var rarity: CardRarity = CardRarity.COMMON,
                var anim: List<AnimState>? = null) {
    companion object {
        val allCards = let {
            val json = Json()
            json.fromJson<ListOfCards>(ListOfCards::class.java,
                    Gdx.files.internal("cardData/CardData.json")).cards
        }
    }


    var onDraw: List<Effect> by Delegates.observable(_onDraw) { property, oldValue, newValue ->
        internalDesccription = internalDescription()
    }
    var onPlay: List<Effect> by Delegates.observable(_onPlay) { property, oldValue, newValue ->
        internalDesccription = internalDescription()
    }
    var onDiscard: List<Effect> by Delegates.observable(
            _onDiscard) { property, oldValue, newValue ->
        internalDesccription = internalDescription()
    }

    //I'm like 90% sure this has to be below the others so leave it here
    var internalDesccription = internalDescription()
            private set

    fun internalDescription(): String =
            onDraw.joinToString(separator = " and ") {
                it.description.replace("{{{power}}}", it.power.toString())
                        .replace("{{{repeat}}}", it.repeat.toString())

            } + onPlay.joinToString(separator = " and ") {
                it.description.replace("{{{power}}}", it.power.toString())
                        .replace("{{{repeat}}}", it.repeat.toString())

            } + onDiscard.joinToString(separator = " and ") {
                it.description.replace("{{{power}}}", it.power.toString())
                        .replace("{{{repeat}}}", it.repeat.toString())

            }

    enum class CardColor {
        NEUTRAL
    }

    enum class CardRarity {
        COMMON
    }

    class Effect() {
        constructor(effectType: EffectType, power: Int, targets: Targets?, description: String,
                    repeat: Int?, anim: List<AnimState>?) : this() {
            this.effectType = effectType
            this.power = power
            this.targets = targets
            this.description = description
            this.repeat = repeat
            this.anim = anim
        }

        lateinit var effectType: EffectType
        var description = ""
        var power: Int = 0
        var repeat: Int? = null
        var targets: Targets? = null
        var anim: List<AnimState>? = null

        enum class EffectType {
            DAMAGE, BLOCK, DAMAGE_FRONT
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
            anim?.let {
                Player.executeStates(it)
            }
            val totalTime: Float = anim?.sumByFloat { it.totalDuration*it.repetitions } ?: 0f
            Player.addAction(Actions.delay(totalTime, Actions.run {
            when(this.effectType) {
                EffectType.DAMAGE -> {
                    if(target is Damageable) {
                        target.currentHealth -= this.power
                    } else {
                        throw GdxRuntimeException("Attempting to damage the undamageable class " + target?.javaClass?.name)
                    }
                }
            }
            }))
        }
    }

    //When this card is brought to the hand
    fun onDraw(target: Targetable?) {
        onDraw.forEach { it.executeEffect(target) }
    }

    //When this card is put into the discard
    fun onDiscard(target: Targetable?) {
        if(PlayerState.ignoreDiscard)
            return
        onDiscard.forEach { it.executeEffect(target) }
    }

    //When this card is played
    fun onPlay(target: Targetable?) {
        onPlay.forEach { it.executeEffect(target) }
    }
}