package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Bezier
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.PlayerState
import com.nishimura.cholessthanthree.PlayerState.currentHand
import com.nishimura.cholessthanthree.PlayerState.handSize
import com.nishimura.cholessthanthree.PlayerState.targetableEntities
import com.nishimura.cholessthanthree.actors.Card.Companion.cardWidth
import com.nishimura.cholessthanthree.actors.Card.Companion.resolutionTime
import com.nishimura.cholessthanthree.toInsideStagePosition
import kotlin.math.abs


object Hand : Group() {
    private var isOnTarget = false
    var anyDown = false
    var isTargetting = false
    val cardSelectAnimationDuration = 0.1f
    var isDrawing = false
    var isDiscarding = false
    val cardDrawnListener: (List<Card>, List<Card>) -> Unit = { oldHand: List<Card>, newHand: List<Card> ->
        val newCards = newHand.minus(oldHand)
        newCards.takeIf { it.size == 1 }?.first()?.run {
            drawNewCard(this)
            reorganizeHand()
        }
    }
    val cardDiscardListener: (List<Card>, List<Card>) -> Unit = { oldDiscard: List<Card>, newDiscard: List<Card> ->
        val cardsToDiscard = newDiscard.minus(oldDiscard)
        cardsToDiscard.takeIf { it.size == 1 }?.first()?.run {
            discardCard(this)
            reorganizeHand()
        }
    }
    private val path1: Bezier<Vector2> = Bezier()
    private val sr: ShapeRenderer = ShapeRenderer()
    private val targettingCursor = Gdx.graphics.newCursor(Assets.targetCircle,
            Assets.targetCircle.width / 2,
            Assets.targetCircle.width / 2)

    init {
        width = MyGdxGame.WIDTH * 0.9f
        height = (MyGdxGame.WIDTH * 0.9f / handSize)
        this.debugAll()
        PlayerState.currentHandListeners.add(cardDrawnListener)
        PlayerState.discardPileListeners.add(cardDiscardListener)
    }


    fun reorganizeHand() {
        LoopCards@ for ((index, card) in currentHand.withIndex()) {
            if (card.isBeingDrawnFromDeck || card.isBeingDiscarded)
                continue
            val absIndexFromMiddle = abs(currentHand.size / 2 - index)
            with(card) {
                restingRotation = -((180f / 16f * (-(index - currentHand.size / 2))))
                restingX = MyGdxGame.WIDTH * 0.5f + (currentHand.size / 2 - index) * cardWidth
                restingY = -(Card.cardHeight / 3f + absIndexFromMiddle * Card.cardHeight / currentHand.size / 2f)
                //Set it's position to return to but don't add action to card being held
            }
            if (card.isDown)
                continue@LoopCards
            with(card) {
                val moveAction = Actions.moveTo(restingX, restingY, resolutionTime)
                val rotationAction = Actions.rotateTo(restingRotation, resolutionTime)
                addAction(Actions.parallel(moveAction, rotationAction))
            }
        }
    }

    fun drawNewCard(card: Card) {
        with(card) {
            val absIndexFromMiddle = abs(currentHand.size / 2)
            restingX = MyGdxGame.WIDTH * 0.5f + (currentHand.size / 2 - (currentHand.size - 1)) * Card.cardWidth
            restingY = -(Card.cardHeight / 3f + absIndexFromMiddle * Card.cardHeight / currentHand.size / 2f)
            restingRotation = -((180f / 16f * (-currentHand.size / 2)))
            addListener(object : DragListener() {
                override fun drag(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                    val clampedPositon = Vector2(event!!.stageX,event.stageY).toInsideStagePosition()
                    if (targets.isEmpty() && card.isDown) {
                        moveTo(event!!.stageX, event.stageY)
                    } else if (card.isDown && !isOnTarget) {
                        isTargetting = true
                        val controlPoints: Array<Vector2> = arrayOf<Vector2>(
                                Vector2(MyGdxGame.WIDTH / 2, Card.cardHeight * 1.5f),
                                Vector2(MyGdxGame.WIDTH / 2, clampedPositon.y),
                                Vector2(clampedPositon.x, clampedPositon.y)
                        )
                        path1.set(*controlPoints)

                    }
                    return super.drag(event, x, y, pointer)
                }

                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                   fromActor: Actor?) {
                    if (!anyDown && !isBeingDrawnFromDeck) {
                        addAction(
                                Actions.parallel(
                                        Actions.moveTo(restingX - Card.cardWidth / 1.5f / 2, 0f,
                                                cardSelectAnimationDuration),
                                        Actions.sizeTo(Card.cardWidth * 1.5f,
                                                Card.cardHeight * 1.5f,
                                                cardSelectAnimationDuration),
                                        Actions.rotateTo(0f, cardSelectAnimationDuration)))
                    }
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                  toActor: Actor?) {
                    if (!anyDown && !isBeingDrawnFromDeck) {
                        clearActions()
                        addAction(
                                Actions.parallel(Actions.sizeTo(Card.cardWidth, Card.cardHeight,
                                        cardSelectAnimationDuration),
                                        Actions.moveTo(restingX, restingY,
                                                cardSelectAnimationDuration),
                                        Actions.rotateTo(restingRotation,
                                                cardSelectAnimationDuration)))
                    }
                }

                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                     button: Int) {
                    anyDown = false
                    isTargetting = false
                    if (isOnTarget) {
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow)
                        isOnTarget = false
                    }
                    if (!anyDown && !isBeingDrawnFromDeck) {
                        clearActions()
                        addAction(
                                Actions.parallel(Actions.sizeTo(Card.cardWidth, Card.cardHeight,
                                        cardSelectAnimationDuration),
                                        Actions.moveTo(restingX, restingY,
                                                cardSelectAnimationDuration),
                                        Actions.rotateTo(restingRotation,
                                                cardSelectAnimationDuration)))
                    }
                    super.touchUp(event, x, y, pointer, button)
                }

                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                       button: Int): Boolean {
                    anyDown = true
                    isBeingDrawnFromDeck = false
                    isDown = true
                    clearActions()
                    toFront()
                    addAction(Actions.sizeTo(Card.cardWidth * 1.5f, Card.cardHeight * 1.5f))
                    if (card.targets.isEmpty())
                        moveTo(event!!.stageX, event.stageY)
                    else
                        moveTo(MyGdxGame.WIDTH / 2, 0f)
                    rotation = 0f
                    return super.touchDown(event, x, y, pointer, button)
                }

            })
            setPosition(DeckButton.x, DeckButton.y)
            setSize(0f, 0f)
            rotation = 0f
            this@Hand.addActor(this)
            val sizeUpAction = Actions.sizeTo(Card.cardWidth, Card.cardHeight, resolutionTime)
            val fadeInAction = Actions.fadeIn(resolutionTime)
            val moveAction = Actions.sequence(
                    Actions.moveTo(restingX / 2, restingY, resolutionTime / 2),
                    Actions.moveTo(restingX, restingY, resolutionTime / 2)
            )
            val rotationAction = Actions.rotateTo(restingRotation, resolutionTime)
            val combinedAction = Actions.sequence(
                    Actions.parallel(moveAction, rotationAction, sizeUpAction, fadeInAction),
                    Actions.run { isBeingDrawnFromDeck = false }
            )
            addAction(combinedAction)
        }
    }

    fun discardCard(card: Card) {
        with(card) {
            isBeingDiscarded = true
            clearListeners()
            val sizeDownAction = Actions.sizeTo(0f, 0f, resolutionTime)
            val fadeOutAction = Actions.fadeOut(resolutionTime)
            val moveAction = Actions.moveTo(DiscardButton.x, DiscardButton.y, resolutionTime)
            val rotationAction = Actions.rotateTo(0f, resolutionTime)
            val combinedAction = Actions.sequence(
                    Actions.parallel(moveAction, rotationAction, sizeDownAction, fadeOutAction),
                    Actions.run {
                        remove()
                        isBeingDiscarded = false
                    }
            )
            addAction(combinedAction)
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        if (anyDown && isTargetting) {
            batch?.end()
            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            sr.begin(ShapeRenderer.ShapeType.Filled)
            sr.color = Color.CLEAR
            sr.projectionMatrix = batch?.projectionMatrix
            //draw path1

            //draw path1
            val k = 1000
            val endOffset = 1f / k
            val radius = MyGdxGame.WIDTH / 50
            var finalPointValue = (k - 1f) / k
            val mousePos = screenToLocalCoordinates(Vector2(Gdx.input.x.toFloat(),
                    Gdx.input.y.toFloat()))
            val mousePosInHitBox = targetableEntities.any{it.hit(mousePos.x, mousePos.y)}
            for (i in 0 until k) {
                val t = (i.toFloat()) / k
                sr.color.add(0f, 0f, 0f, 2f / k)
                // create vectors to store start and end points of this section of the curve
                val st = Vector2()
                val end = Vector2()
                // get the start point of this curve section
                path1.valueAt(st, t)
                // get the next start point(this point's end)
                path1.valueAt(end, t - endOffset)
                if (mousePosInHitBox && targetableEntities.any{it.hit(end.x, end.y)}) {
                    finalPointValue = t
                    if (!isOnTarget) {
                        isOnTarget = true
                        Gdx.graphics.setCursor(targettingCursor)
                    }
                    break
                }
                // draw the curve
                sr.rectLine(st.x, st.y, end.x, end.y, radius)
            }

            //Was on target, but is no longer
            if (isOnTarget && !mousePosInHitBox) {
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow)
                isOnTarget = false
            }
            val circlePos = Vector2()
            // get the start point of this curve section
            path1.valueAt(circlePos, finalPointValue)
            sr.circle(circlePos.x, circlePos.y, radius / 2)
            sr.end()
            batch?.begin()
            Gdx.gl.glDisable(GL20.GL_BLEND)
        }
    }
}
