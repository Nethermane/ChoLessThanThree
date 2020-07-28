package com.nishimura.cholessthanthree

import com.nishimura.cholessthanthree.actors.Card

/**
 * Class to manage the deck as an abstraction outside of combat
 */
object DeckManager {
    private val cards = ArrayList<Card>()
    init {
        makeDefaultDeck()
    }
    private fun makeDefaultDeck() {
        cards.clear()
        for(i in 1 .. 3) {
            cards.add(Card(1, Card.OffensiveEffect()))
        }
        for(i in 1 .. 3) {
            cards.add(Card(1, Card.DefensiveEffect()))
        }
        for(i in 1 .. 2) {
            cards.add(Card(1, Card.UtilityEffect()))
        }
    }
    fun getCardsForPlayDeckManager(): ArrayList<Card> {
        return ArrayList(cards.shuffled())
    }
    fun addCard(card: Card) = cards.add(card)
    fun removeCard(card: Card) = cards.remove(card)
}
