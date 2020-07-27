package com.nishimura.cholessthanthree

import kotlin.properties.Delegates
import kotlin.properties.ObservableProperty

object PlayerState {
    val maxHealthlisteners = ArrayList<(old: Int,new: Int) -> Unit>()
    val healthListeners = ArrayList<(old: Int, new: Int) -> Unit>()
    val currencyListeners = ArrayList<(old: Int, new: Int) -> Unit>()
    val manaListeners = ArrayList<(old: Int, new: Int) -> Unit>()
    val maxManaListeners = ArrayList<(old: Int, new: Int) -> Unit>()


    val deckListener = ArrayList<(oldDeck: ArrayList<Card>, newDeck: ArrayList<Card>) -> Unit>()

    // fires off every time value of the property changes
    var maxHealth: Int by Delegates.observable(120) { property, oldValue, newValue ->
        // do your stuff here
        maxHealthlisteners.forEach {
            it(oldValue,newValue)
        }
    }
    var health: Int by Delegates.observable(120) { property, oldValue, newValue ->
        // do your stuff here
        maxHealthlisteners.forEach {
            it(oldValue, newValue)
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
    var deck: ArrayList<Card> by Delegates.observable(ArrayList()) { property, oldValue, newValue ->
        // do your stuff here
        deckListener.forEach {
            it(oldValue,newValue)
        }
    }
}