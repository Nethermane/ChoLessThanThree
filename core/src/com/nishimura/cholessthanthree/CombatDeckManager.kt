package com.nishimura.cholessthanthree

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.nishimura.cholessthanthree.PlayerState.currentHand
import com.nishimura.cholessthanthree.PlayerState.discardPile
import com.nishimura.cholessthanthree.PlayerState.drawPile
import com.nishimura.cholessthanthree.PlayerState.getPlayerDraw
import com.nishimura.cholessthanthree.PlayerState.handSize
import com.nishimura.cholessthanthree.actions.flipIn
import com.nishimura.cholessthanthree.actions.flipOut
import com.nishimura.cholessthanthree.actors.Card
import com.nishimura.cholessthanthree.actors.DeckButton
import com.nishimura.cholessthanthree.actors.Hand


class CombatDeckManager(val stage: Stage) {

    val hand = Hand()

    fun beginTurn() {
        stage.addActor(DeckButton)
        drawPhase@ for (i in 1..getPlayerDraw()) {
            if (drawPile.isEmpty()) {
                if (discardPile.isNotEmpty()) {
                    PlayerState.shuffleDiscardIntoDraw()
                } else {
                    //There is nothing left to draw
                    break@drawPhase
                }
            }
            //Draw to hand if there is room
            if (currentHand.size < handSize) {
//                val cardToDraw = drawPile.removeAt(0)
//                currentHand.add(cardToDraw)
                PlayerState.addTopDrawCardToHand()
                //Apply any on draw effects the card may have
                currentHand.last().onDraw(requestTarget(currentHand.last()))
            }
            //Otherwise draw rest of cards expected to discard and end turn
            else {
                for (j in i..getPlayerDraw()) {
                    if (drawPile.isNotEmpty()) {
                        PlayerState.drawToDiscard()
                        discardPile.last().onDiscard(requestTarget(discardPile.last()))
//                        with(drawPile.removeAt(0)) {
//                            discardPile.add(this)
//                            //May remove this later depending on if overdraw is considered discarding
//                            onDiscard(requestTarget(this))
//                        }
                    }
                }
                break@drawPhase
            }
        }
        currentHand.forEach { it.setPosition(DeckButton.x,DeckButton.y) }
        hand.setCards(currentHand, stage)
    }

    private fun requestTarget(cardToDraw: Card): Targetable? {
        //TODO: Actually request target
        return null
    }
}
