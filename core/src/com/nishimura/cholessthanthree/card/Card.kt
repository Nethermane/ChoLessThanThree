package com.nishimura.cholessthanthree.card

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.Json
import com.nishimura.cholessthanthree.PlayerState
import com.nishimura.cholessthanthree.Targetable
import com.nishimura.cholessthanthree.player.AnimState
import kotlin.properties.Delegates

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
                var rarity: CardRarity = CardRarity.COMMON) {
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