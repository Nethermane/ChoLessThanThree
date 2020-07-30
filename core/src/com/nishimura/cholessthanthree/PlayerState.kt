package com.nishimura.cholessthanthree

import com.nishimura.cholessthanthree.actors.Card
import com.nishimura.cholessthanthree.effects.Stat
import com.nishimura.cholessthanthree.effects.Status
import kotlin.properties.Delegates


object PlayerState {
    val maxHealthlisteners = ArrayList<() -> Unit>()
    val healthListeners = ArrayList<() -> Unit>()
    val currencyListeners = ArrayList<(old: Int, new: Int) -> Unit>()
    val manaListeners = ArrayList<(old: Int, new: Int) -> Unit>()
    val maxManaListeners = ArrayList<(old: Int, new: Int) -> Unit>()
    val drawPileListeners = ArrayList<(old: List<Card>, new: List<Card>) -> Unit>()
    val discardPileListeners = ArrayList<(old: List<Card>, new: List<Card>) -> Unit>()
    val currentHandListeners = ArrayList<(old: List<Card>, new: List<Card>) -> Unit>()
    val deckListener = ArrayList<(oldDeck: List<Card>, newDeck: List<Card>) -> Unit>()



    private val _currentHand: ArrayList<Card> = ArrayList()
    private val _discardPile: ArrayList<Card> = ArrayList()
    private val _drawPile: ArrayList<Card> = DeckManager.getCardsForPlayDeckManager()

    var deck: List<Card> by Delegates.observable(DeckManager.cards) { property, oldValue, newValue ->
        // do your stuff here
        deckListener.forEach {
            it(oldValue,newValue)
        }
    }
    var drawPile: List<Card> by Delegates.observable<List<Card>>(_drawPile) { property, oldValue, newValue ->
        drawPileListeners.forEach {
            it(oldValue,newValue)
        }
    }
    private set
    var discardPile: List<Card> by Delegates.observable<List<Card>>(
            _discardPile) { property, oldValue, newValue ->
        discardPileListeners.forEach {
            it(oldValue,newValue)
        }
    }
        private set
    var currentHand: List<Card> by Delegates.observable<List<Card>>(
            _currentHand) { property, oldValue, newValue ->
        currentHandListeners.forEach {
            it(oldValue,newValue)
        }
    }
        private set

    val handSize: Int = 12
    var baseDrawAmount: Int = 5
    val drawModifiers: List<Status> = emptyList()
    fun getPlayerDraw() = baseDrawAmount + drawModifiers.sumBy { it.intensity }
    fun shuffleDiscardIntoDraw() {
        _drawPile.addAll(discardPile)
        _drawPile.shuffle()
        drawPile = _drawPile
        _discardPile.clear()
        discardPile = _discardPile
    }

    fun addTopDrawCardToHand() {
        _currentHand.add(_drawPile.removeAt(_drawPile.size-1))
        currentHand = _currentHand
        drawPile = _drawPile
    }

    fun drawToDiscard() {
        _discardPile.add(_drawPile.removeAt(_drawPile.size-1))
        discardPile = _discardPile
        drawPile = _drawPile
    }


    // fires off every time value of the property changes
    var maxHealth: Int by Delegates.observable(120) { property, oldValue, newValue ->
        // do your stuff here
        maxHealthlisteners.forEach {
            it()
        }
    }
    var health: Int by Delegates.observable(120) { property, oldValue, newValue ->
        // do your stuff here
        healthListeners.forEach {
            it()
        }
    }
    var currency: Int by Delegates.observable(0) { property, oldValue, newValue ->
        // do your stuff here
        currencyListeners.forEach {
            it(oldValue, newValue)
        }
    }
    var maxMana: Int by Delegates.observable(5) { property, oldValue, newValue ->
        // do your stuff here
        maxManaListeners.forEach {
            it(oldValue, newValue)
        }
    }
    var mana: Int by Delegates.observable(0) { property, oldValue, newValue ->
        // do your stuff here
        manaListeners.forEach {
            it(oldValue, newValue)
        }
    }
}