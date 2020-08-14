package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.PlayerState
import com.nishimura.cholessthanthree.PlayerState.targetableEntities
import com.nishimura.cholessthanthree.card.CardView.Companion.cardWidth
import com.nishimura.cholessthanthree.card.CardView.Companion.resolutionTime
import com.nishimura.cholessthanthree.card.Card
import com.nishimura.cholessthanthree.card.CardView
import kotlin.math.abs


object Hand : Group() {
    var isOnTarget = false
    val cardSelectAnimationDuration = 0.1f
    var isDrawing = false
    var isDiscarding = false
    val cardsInHand = ArrayList<CardView>()
    val cardDrawnListener: (List<Card>, List<Card>) -> Unit = { oldHand: List<Card>, newHand: List<Card> ->
        val newCards = newHand.filterNot { newCard ->
            oldHand.any { oldCard -> oldCard === newCard }
        }
        newCards.takeIf { it.size == 1 }?.first()?.run {
            drawNewCard(this)
            reorganizeHand()
        }
    }
    val cardDiscardListener: (List<Card>, List<Card>) -> Unit = { oldDiscard: List<Card>, newDiscard: List<Card> ->
        val cardsToDiscard = newDiscard.filterNot { newCard ->
            oldDiscard.any { oldCard -> oldCard === newCard }
        }
        cardsToDiscard.takeIf { it.size == 1 }?.first()?.run {
            discardCard(this)
            reorganizeHand()
        }
    }
    private val sr: ShapeRenderer = ShapeRenderer()
    private val targettingCursor = Gdx.graphics.newCursor(Assets.targetCircle,
            Assets.targetCircle.width / 2,
            Assets.targetCircle.width / 2)

    init {
        PlayerState.currentHandListeners.add(cardDrawnListener)
        PlayerState.discardPileListeners.add(cardDiscardListener)
    }

    fun startTurn() {
        cardsInHand.forEach { it.clear() }
        cardsInHand.clear()
    }

    fun reorganizeHand() {
        LoopCards@ for ((index, card) in cardsInHand.withIndex()) {
            if (card.cardDisplayState == CardView.CardDisplayState.DISCARDING)
                continue
            val absIndexFromMiddle = abs(cardsInHand.size / 2 - index)
            with(card) {
                restingRotation = -((180f / 16f * (-(index - cardsInHand.size / 2))))
                restingX = MyGdxGame.WIDTH * 0.5f + (cardsInHand.size / 2 - index) * cardWidth
                restingY = -(CardView.cardHeight / 3f + absIndexFromMiddle * CardView.cardHeight / cardsInHand.size / 2f)
                //Set it's position to return to but don't add action to card being held
            }
            if (CardView.focused == card && CardView.touchDown)
                continue@LoopCards
            with(card) {
                val moveAction = Actions.moveTo(restingX, restingY, resolutionTime)
                val rotationAction = Actions.rotateTo(restingRotation, resolutionTime)
                addAction(Actions.parallel(moveAction, rotationAction))
            }
        }
    }

    fun drawNewCard(cardData: Card) {
        val newCard = CardView(cardData)
        cardsInHand.add(newCard)
        newCard.cardDisplayState = CardView.CardDisplayState.DRAWING

    }

    fun discardCard(card: Card) {
        cardsInHand.filter { it.card === card }.forEach {
            it.cardDisplayState = CardView.CardDisplayState.DISCARDING
        }
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        if (CardView.focused != null && CardView.touchDown && CardView.focused?.card?.onPlay?.firstOrNull()?.targets != null) {
            batch?.end()
            Gdx.gl.glEnable(GL20.GL_BLEND)
            Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)
            sr.begin(ShapeRenderer.ShapeType.Filled)
            sr.color = Color.BLACK
            sr.projectionMatrix = batch?.projectionMatrix
            //draw path1

            //draw path1
            val k = 8
            val radius = MyGdxGame.WIDTH / 50
            val mousePos = screenToLocalCoordinates(Vector2(Gdx.input.x.toFloat(),
                    Gdx.input.y.toFloat()))
            val targetableClasses = CardView.focused?.card?.onPlay?.firstOrNull()?.targets?.getClassesForTarget()
            val mousePosInHitBox = targetableEntities.any { entity ->
                targetableClasses?.let { clzs ->
                    clzs.any { clz -> clz.isInstance(entity) }
                            && entity.hit(mousePos.x, mousePos.y)
                } ?: false
            }
            if (!isOnTarget)
                sr.color.sub(0f, 0f, 0f, 0.5f)
            for (i in 1..k) {
                val t = (i.toFloat()) / k
                // create vectors to store start and end points of this section of the curve
                val st = Vector2()
                // get the start point of this curve section
                CardView.pathFromCardToMouse.valueAt(st, t)
                // get the next start point(this point's end)
                if (mousePosInHitBox && targetableEntities.any { it.hit(st.x, st.y) }) {
                    if (!isOnTarget) {
                        isOnTarget = true
                        //Gdx.graphics.setCursor(targettingCursor)
                    }
                }
                // draw the curve
                sr.circle(st.x, st.y, radius)
            }

            //Was on target, but is no longer
            if (isOnTarget && !mousePosInHitBox) {
                //Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow)
                isOnTarget = false
            }
            sr.end()
            batch?.begin()
            Gdx.gl.glDisable(GL20.GL_BLEND)
        }
    }
}
