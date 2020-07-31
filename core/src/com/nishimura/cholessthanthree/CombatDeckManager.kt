package com.nishimura.cholessthanthree

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.nishimura.cholessthanthree.PlayerState.currentHand
import com.nishimura.cholessthanthree.PlayerState.discardPile
import com.nishimura.cholessthanthree.PlayerState.drawPile
import com.nishimura.cholessthanthree.PlayerState.getPlayerDraw
import com.nishimura.cholessthanthree.PlayerState.handSize
import com.nishimura.cholessthanthree.actors.*


object CombatDeckManager : Group() {
    val turnEndListener = { oldTurn: Int, newTurn: Int ->
        val repeat = Actions.sequence(
                Actions.run{Hand.isDiscarding = true},
                Actions.repeat(currentHand.size, Actions.sequence(Actions.run {
                    PlayerState.discardLastCardInHand()
                }, Actions.delay(Card.resolutionTime))),
                Actions.run {
                    Hand.isDiscarding = false
                    beginTurn()
                }
        )
        addAction(repeat)
    }

    init {
        addActor(DeckButton)
        addActor(DiscardButton)
        addActor(EndTurnButton)
        addActor(Hand)
        PlayerState.turnListeners.add(turnEndListener)
    }

    fun beginTurn() {
        var repeat: RepeatAction? = null
        var repeated = 0
        repeat = Actions.repeat(getPlayerDraw(), Actions.sequence(Actions.run {
            repeated++
            if (drawPile.isEmpty()) {
                if (discardPile.isNotEmpty()) {
                    PlayerState.shuffleDiscardIntoDraw()
                } else {
                    //There is nothing left to draw
                    repeat!!.finish()
                    return@run
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
                repeat?.count
                for (j in repeated..repeat!!.count) {
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
                repeat?.finish()
            }
        }, Actions.delay(Card.resolutionTime * 2)))
        val seq = Actions.sequence(Actions.run{Hand.isDrawing = true},
            repeat, Actions.run{Hand.isDrawing = false})
        addAction(seq)
//
//        drawPhase@ for (i in 1..getPlayerDraw()) {
//            if (drawPile.isEmpty()) {
//                if (discardPile.isNotEmpty()) {
//                    PlayerState.shuffleDiscardIntoDraw()
//                } else {
//                    //There is nothing left to draw
//                    break@drawPhase
//                }
//            }
//            //Draw to hand if there is room
//            if (currentHand.size < handSize) {
////                val cardToDraw = drawPile.removeAt(0)
////                currentHand.add(cardToDraw)
//                PlayerState.addTopDrawCardToHand()
//                //Apply any on draw effects the card may have
//                currentHand.last().onDraw(requestTarget(currentHand.last()))
//            }
//            //Otherwise draw rest of cards expected to discard and end turn
//            else {
//                for (j in i..getPlayerDraw()) {
//                    if (drawPile.isNotEmpty()) {
//                        PlayerState.drawToDiscard()
//                        discardPile.last().onDiscard(requestTarget(discardPile.last()))
////                        with(drawPile.removeAt(0)) {
////                            discardPile.add(this)
////                            //May remove this later depending on if overdraw is considered discarding
////                            onDiscard(requestTarget(this))
////                        }
//                    }
//                }
//                break@drawPhase
//            }
//        }

    }

    private fun requestTarget(cardToDraw: Card): Targetable? {
        //TODO: Actually request target
        return null
    }
}
