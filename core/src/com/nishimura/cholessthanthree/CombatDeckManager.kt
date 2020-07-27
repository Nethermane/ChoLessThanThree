package com.nishimura.cholessthanthree

@ExperimentalStdlibApi
class CombatDeckManager(private val drawPile: ArrayList<Card>, private val currentHand: ArrayList<Card> = ArrayList(), private val discardPile: ArrayList<Card> = ArrayList(), private var handSize: Int, private var drawAmount: Int) {
    fun beginTurn() {
        drawPhase@ for (i in 1..drawAmount) {
            if (drawPile.isEmpty()) {
                if (discardPile.isNotEmpty()) {
                    drawPile.addAll(discardPile)
                    drawPile.shuffle()
                    discardPile.clear()
                } else {
                    //There is nothing left to draw
                    break@drawPhase
                }
            }
            //Draw to hand if there is room
            if (currentHand.size < handSize) {
                val cardToDraw = drawPile.removeFirst()
                currentHand.add(cardToDraw)
                cardToDraw.onDraw(requestTarget(cardToDraw))
            }
            //Otherwise draw rest of cards expected to discard and end turn
            else {
                for (j in i..drawAmount)
                    drawPile.removeFirstOrNull()?.let {
                        discardPile.add(it)
                        //May remove this later depending on if overdraw is considered discarding
                        it.onDiscard(requestTarget(it))
                    }
                break@drawPhase
            }
        }
    }

    private fun requestTarget(cardToDraw: Card): Targetable? {
        //TODO: Actually request target
        return null
    }
}
