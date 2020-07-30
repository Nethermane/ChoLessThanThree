package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.PlayerState.handSize
import com.nishimura.cholessthanthree.actions.flipIn
import com.nishimura.cholessthanthree.actions.flipOut
import kotlin.math.abs

class Hand {
    val cards = ArrayList<Card>()
    val width = MyGdxGame.WIDTH * 0.9f
    val height = (MyGdxGame.WIDTH * 0.9f / handSize)
    var anyDown = false
    var isTargetting = false
    val cardSelectAnimationDuration = 0.1f
    private val targettingCursor = Gdx.graphics.newCursor(Assets.targetCircle,
            Assets.targetCircle.width / 2,
            Assets.targetCircle.width / 2)

    fun setCards(newCards: List<Card>, stage: Stage) {
        cards.forEach { it.remove() }
        cards.clear()
        cards.addAll(newCards)
        //TODO: Refactor this all into the Card class
        cards.forEachIndexed { index, card ->
            val absIndexFromMiddle = abs(cards.size / 2 - index)
            val startingRotation = ((   180f / 16f * (-(index - cards.size / 2))))
            card.restingX = MyGdxGame.WIDTH * 0.3f + index.toFloat() / cards.size * MyGdxGame.WIDTH * 0.4f
            card.restingY = -absIndexFromMiddle * height / cards.size
            card.addListener(object : DragListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                   fromActor: Actor?) {
                    if (!anyDown && !card.isBeingDrawnFromDeck) {
                        card.addAction(
                                Actions.parallel(Actions.moveTo(card.restingX- Card.cardWidth/1.5f/2, 0f, cardSelectAnimationDuration),
                                        Actions.sizeTo(Card.cardWidth*1.5f, Card.cardHeight*1.5f, cardSelectAnimationDuration),
                                        Actions.rotateTo(0f, cardSelectAnimationDuration)))
                        card.zIndex += 1
                    }
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                  toActor: Actor?) {
                    if (!anyDown && !card.isBeingDrawnFromDeck) {
                        card.clearActions()
                        card.addAction(Actions.parallel(Actions.sizeTo(Card.cardWidth, Card.cardHeight, cardSelectAnimationDuration),
                                Actions.moveTo(card.restingX, card.restingY, cardSelectAnimationDuration),
                                Actions.rotateTo(startingRotation, cardSelectAnimationDuration)))
                        card.zIndex -= 1
                    }
                }

                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                     button: Int) {
                    anyDown = false
                    super.touchUp(event, x, y, pointer, button)
                }

                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                       button: Int): Boolean {
//                    Gdx.input.isCursorCatched = true
                    anyDown = true
                    card.isBeingDrawnFromDeck = false
                    card.isDown = true
                    card.clearActions()
                    card.toFront()
                    card.moveTo(event!!.stageX, event.stageY)
                    card.addAction(Actions.sizeTo(Card.cardWidth*1.5f, Card.cardHeight*1.5f))
                    card.rotation = 0f
                    return super.touchDown(event, x, y, pointer, button)
                }

                override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
//                    if (anyDown && event!!.stageY > MyGdxGame.HEIGHT * 0.3f && !isTargetting) {
//                        card.moveTo(event.stageX, event.stageY)
//                        isTargetting = true
////                        Gdx.input.isCursorCatched = false
//                        Gdx.graphics.setCursor(targettingCursor)
////                        Gdx.input.setCursorPosition(event.stageX.toInt(), event.stageY.toInt())
//                    } else if (anyDown && event!!.stageY <= MyGdxGame.HEIGHT * 0.3f && isTargetting) {
//                        isTargetting = false
//                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow)
//                    }
                    if (!isTargetting)
                        card.moveTo(event!!.stageX, event.stageY)
                    super.touchDragged(event, x, y, pointer)
                }
            })
            //region drawFromDeckActions
            val resolutionTime = 0.25f
            stage.addActor(card)
            card.setSize(0f,0f)
            val sizeUpAction = Actions.sizeTo(Card.cardWidth,Card.cardHeight, resolutionTime)
            val fadeInAction = Actions.fadeIn(resolutionTime)
            val moveAction = Actions.sequence(
                    Actions.moveTo(card.restingX / 2, card.restingY, resolutionTime / 2),
                    Actions.moveTo(card.restingX, card.restingY, resolutionTime / 2)
            )
            val rotationAction = Actions.rotateTo(startingRotation, resolutionTime)
            val waitAction = Actions.delay(resolutionTime * index)
            val combinedAction = Actions.sequence(waitAction,
                    Actions.parallel(moveAction, rotationAction, sizeUpAction, fadeInAction),
                    Actions.run{card.isBeingDrawnFromDeck = false}
            )
            card.addAction(combinedAction)
            //endregion
        }
    }

    fun resetCards() {
        for (card in cards) {
            card.toFront()
        }
    }
}