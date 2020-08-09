package com.nishimura.cholessthanthree

import com.nishimura.cholessthanthree.data.Card
import com.nishimura.cholessthanthree.actors.Player

/**
 * Class to manage the deck as an abstraction outside of combat
 */
object DeckManager {
    private val _cards = ArrayList<Card>()
    val cards: List<Card> = _cards
    init {
        makeDefaultDeck()
    }
    private fun makeDefaultDeck() {
        _cards.clear()
        for(i in 0..10)
            _cards.add(Card.allCards.first().copy())
    }
    fun getCardsForPlayDeckManager(): ArrayList<Card> {
        return ArrayList(_cards.shuffled())
    }
    fun addCard(card: Card) = _cards.add(card)
    fun removeCard(card: Card) = _cards.remove(card)
}
