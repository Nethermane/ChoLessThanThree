package com.nishimura.cholessthanthree

import com.nishimura.cholessthanthree.actors.Card
import com.nishimura.cholessthanthree.effects.Status
import java.util.*
import kotlin.collections.ArrayList
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
    val turnListeners = ArrayList<(oldTurnNum: Int, newTurnNum: Int) -> Unit>()


    private val _currentHand: Stack<Card> = Stack()
    private val _discardPile: Stack<Card> = Stack()
    private val _drawPile: ArrayList<Card> = DeckManager.getCardsForPlayDeckManager()

    var deck: List<Card> by Delegates.observable(DeckManager.cards.toList()) { property, oldValue, newValue ->
        // do your stuff here
        deckListener.forEach {
            it(oldValue,newValue)
        }
    }
    var drawPile: List<Card> by Delegates.observable(_drawPile.toList()) { property, oldValue, newValue ->
        drawPileListeners.forEach {
            it(oldValue,newValue)
        }
    }
    private set
    var discardPile: List<Card> by Delegates.observable(
            _discardPile.toList()) { property, oldValue, newValue ->
        discardPileListeners.forEach {
            it(oldValue,newValue)
        }
    }
        private set
    var currentHand: List<Card> by Delegates.observable(
            _currentHand.toList()) { property, oldValue, newValue ->
        currentHandListeners.forEach {
            it(oldValue,newValue)
        }
    }
        private set

    var turnNumber: Int by Delegates.observable(0) { property, oldValue, newValue ->
        turnListeners.forEach {
            it(oldValue,newValue)
        }
    }
    val handSize: Int = 12
    var baseDrawAmount: Int = 5
    val drawModifiers: List<Status> = emptyList()
    fun getPlayerDraw() = baseDrawAmount + drawModifiers.sumBy { it.intensity }
    fun shuffleDiscardIntoDraw() {
        _drawPile.addAll(discardPile)
        _drawPile.shuffle()
        drawPile = _drawPile.toList()
        _discardPile.clear()
        discardPile = _discardPile.toList()
    }
    fun shuffleDiscard() {
        _discardPile.shuffle()
        discardPile = _discardPile.toList()
    }
    fun moveTopDiscardToDraw() {
        println(discardPile)
        _drawPile.add(_discardPile.pop())
        drawPile = _drawPile.toList()
        discardPile = _discardPile.toList()
    }

    fun addTopDrawCardToHand() {
        _currentHand.add(_drawPile.removeAt(_drawPile.size-1))
        currentHand = _currentHand.toList()
        drawPile = _drawPile.toList()
    }

    fun drawToDiscard() {
        _discardPile.add(_drawPile.removeAt(_drawPile.size-1))
        discardPile = _discardPile.toList()
        drawPile = _drawPile.toList()
    }
    fun discardLastCardInHand() {
        _discardPile.add(_currentHand.removeAt(0))
        discardPile = _discardPile.toList()
        currentHand = _currentHand.toList()
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