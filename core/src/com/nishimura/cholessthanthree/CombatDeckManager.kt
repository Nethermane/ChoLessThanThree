package com.nishimura.cholessthanthree

import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction
import com.nishimura.cholessthanthree.PlayerState.currentHand
import com.nishimura.cholessthanthree.PlayerState.discardPile
import com.nishimura.cholessthanthree.PlayerState.drawPile
import com.nishimura.cholessthanthree.PlayerState.getPlayerDraw
import com.nishimura.cholessthanthree.PlayerState.handSize
import com.nishimura.cholessthanthree.PlayerState.targetableEntities
import com.nishimura.cholessthanthree.actors.*
import com.nishimura.cholessthanthree.data.Card


object CombatDeckManager : Group() {
    const val shuffleTime = 0.25f
    val turnEndListener = { oldTurn: Int, newTurn: Int ->
        val repeat = Actions.sequence(
                Actions.run { Hand.isDiscarding = true },
                Actions.repeat(currentHand.size, Actions.sequence(Actions.run {
                    PlayerState.discardLastCardInHand()
                }, Actions.delay(CardView.resolutionTime))),
                Actions.run {
                    Hand.isDiscarding = false
                    beginTurn()
                }
        )
        addAction(repeat)
    }
    private var waitForShuffle = false
    private var repeat: RepeatAction? = null
    private var repeated = 0

    init {
        addActor(DeckButton)
        addActor(DiscardButton)
        addActor(EndTurnButton)
        addActor(Hand)
        addActor(Player)
        val aBird = Bird()
        addActor(aBird)
        targetableEntities.add(aBird)
        PlayerState.turnListeners.add(turnEndListener)
    }

    fun getShuffleAction(): SequenceAction {
        val sequence = Actions.sequence()
        val waitTimePer = getIndividualWaitTime()
        if (waitForShuffle) {
            sequence.addAction(Actions.run { PlayerState.shuffleDiscard() })
            sequence.addAction(Actions.repeat(discardPile.size,
                    Actions.sequence(Actions.delay(waitTimePer),
                            Actions.run {
                                PlayerState.moveTopDiscardToDraw()
                            })))
        }
        return sequence
    }

    fun getdDelaySequenceDrawing() = Actions.delay(0f, Actions.sequence(Actions.run {
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
            PlayerState.addTopDrawCardToHand()
            //Apply any on draw effects the card may have
            currentHand.last().onDraw(requestTarget(currentHand.last()))
        }
        //Otherwise draw rest of cards expected to discard and end turn
        else {
            for (j in repeated..repeat!!.count) {
                if (drawPile.isNotEmpty()) {
                    PlayerState.drawToDiscard()
                    discardPile.last().onDiscard(requestTarget(discardPile.last()))
                }
            }
            repeat?.finish()
        }
    }, Actions.delay(CardView.resolutionTime * 2)))

    fun beginTurn() {
        repeated = 0
        PlayerState.mana = PlayerState.maxMana
        Hand.startTurn()
        val myDelayAction = getdDelaySequenceDrawing()
        repeat = Actions.repeat(getPlayerDraw(), Actions.sequence(
                /**
                 * Check if you need to shuffle and increment repeat counter
                 */
                Actions.run {
                    waitForShuffle = false
                    repeated++
                    if (drawPile.isEmpty() && discardPile.isNotEmpty()) {
                        waitForShuffle = true
                        addAction(getShuffleAction())
                        myDelayAction.duration = shuffleTime
                    } else {
                        myDelayAction.duration = 0f
                    }
                },
                myDelayAction
        ))
        val seq = Actions.sequence(Actions.run { Hand.isDrawing = true },
                repeat, Actions.run { Hand.isDrawing = false })
        addAction(seq)
    }

    fun getIndividualWaitTime() = shuffleTime / discardPile.size
    private fun requestTarget(cardToDraw: Card): Targetable? {
        //TODO: Actually request target
        return null
    }
}
