package com.nishimura.cholessthanthree

/**
 * Class to manage the deck as an abstraction outside of combat
 */
class DeckManager {
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
