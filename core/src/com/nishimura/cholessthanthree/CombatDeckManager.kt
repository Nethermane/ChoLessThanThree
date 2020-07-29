package com.nishimura.cholessthanthree

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.nishimura.cholessthanthree.PlayerState.getPlayerDraw
import com.nishimura.cholessthanthree.PlayerState.handSize
import com.nishimura.cholessthanthree.actions.flipIn
import com.nishimura.cholessthanthree.actions.flipOut
import com.nishimura.cholessthanthree.actors.Card
import com.nishimura.cholessthanthree.actors.Hand

class CombatDeckManager(val stage: Stage, private val currentHand: ArrayList<Card> = ArrayList(),
                        private val discardPile: ArrayList<Card> = ArrayList()) {
    val drawPileButton = Image(Assets.card).apply {
        setSize(Card.cardWidth, Card.cardHeight)
        setPosition(width / 4, width / 4)
    }
    private var drawPile: ArrayList<Card> = DeckManager.getCardsForPlayDeckManager()
    val drawPileLabel = Label(drawPile.size.toString(),
            Label.LabelStyle(Assets.healthFont, Color.RED)).apply {
        setPosition(drawPileButton.x + drawPileButton.width / 2 - width / 2,
                drawPileButton.y + drawPileButton.height / 2 - height / 2)
    }


    val hand = Hand()


    init {
        stage.addActor(drawPileButton)
        stage.addActor(drawPileLabel)
    }

    fun beginTurn() {
        drawPileButton.toFront()
        drawPileLabel.toFront()
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
                    if (drawPile.isNotEmpty()) {
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
        currentHand.forEach { it.setPosition(drawPileButton.x,drawPileButton.y) }
        hand.setCards(currentHand, stage)
    }

    private fun requestTarget(cardToDraw: Card): Targetable? {
        //TODO: Actually request target
        return null
    }
}
