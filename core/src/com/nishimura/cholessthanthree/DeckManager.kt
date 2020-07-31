package com.nishimura.cholessthanthree

import com.nishimura.cholessthanthree.actors.Card

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
        for(i in 1 .. 3) {
            _cards.add(Card(1, Card.OffensiveEffect()))
        }
        for(i in 1 .. 3) {
            _cards.add(Card(1, Card.DefensiveEffect()))
        }
        for(i in 1 .. 2) {
            _cards.add(Card(1, Card.UtilityEffect()))
        }
    }
    fun getCardsForPlayDeckManager(): ArrayList<Card> {
        return ArrayList(_cards.shuffled())
    }
    fun addCard(card: Card) = _cards.add(card)
    fun removeCard(card: Card) = _cards.remove(card)
}
