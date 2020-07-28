package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.nishimura.cholessthanthree.DeckManager
import com.nishimura.cholessthanthree.PlayerState.getPlayerDraw
import com.nishimura.cholessthanthree.PlayerState.handSize
import com.nishimura.cholessthanthree.Targetable

class CombatDeckManager(private val currentHand: ArrayList<Card> = ArrayList(),
                        private val discardPile: ArrayList<Card> = ArrayList()) {
    val hand = Hand()
    private val drawPile: ArrayList<Card> = DeckManager.getCardsForPlayDeckManager()
    fun beginTurn() {
        drawPhase@ for (i in 1..getPlayerDraw()) {
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
                val cardToDraw = drawPile.removeAt(0)
                currentHand.add(cardToDraw)
                cardToDraw.onDraw(requestTarget(cardToDraw))
            }
            //Otherwise draw rest of cards expected to discard and end turn
            else {
                for (j in i..getPlayerDraw()) {
                    if(drawPile.isNotEmpty()) {
                        with(drawPile.removeAt(0)) {
                            discardPile.add(this)
                            //May remove this later depending on if overdraw is considered discarding
                            onDiscard(requestTarget(this))
                        }
                    }
                }
                break@drawPhase
            }
        }
        hand.setCards(currentHand)
    }

    private fun requestTarget(cardToDraw: Card): Targetable? {
        //TODO: Actually request target
        return null
    }
}
