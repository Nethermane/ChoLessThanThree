package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Bezier
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align
import com.nishimura.cholessthanthree.*
import com.nishimura.cholessthanthree.PlayerState.handSize
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates
import com.nishimura.cholessthanthree.data.Card


class CardView(val card: Card) : Targetable, Group() {
    override fun hit(x: Float, y: Float): Boolean {
        return (x >= this.x && x < width + this.x && y >= this.y && y < height + this.y)
    }

    enum class CardDisplayState {
        UNKNOWN, DRAWING, RESTING, HOVERING, DISCARDING, TARGETTING, PLAYED_TO_DISCARD, GONE,
        TRANSITIONING
    }

    companion object {
        //Constants related to the sizing of the card and animation speed
        val cardWidth = (MyGdxGame.WIDTH * 0.9f / handSize)
        val cardHeight = cardWidth * 2
        val cardGroupDisplayWidth = (MyGdxGame.WIDTH / 8f)
        val cardGroupDisplayHeight = (MyGdxGame.WIDTH / 4f)
        val resolutionTime = 0.25f
        var focused: CardView? = null
        var touchDown: Boolean = false
        var pathFromCardToMouse: Bezier<Vector2> = Bezier()
    }

    var cost: Int by Delegates.observable(card.cost) { property, oldValue, newValue ->
        manaCostLabel.setText(newValue.toString())
    }

    //The place this card will return to when in hand
    var restingX = 0f
    var restingY = 0f

    //The rotation this card will return to when in hand
    var restingRotation = 0f

    //State of the card (to determine what actions are acceptable
    var cardDisplayState: CardDisplayState by Delegates.observable(
            CardDisplayState.UNKNOWN) { property, oldValue, newValue ->
        when (newValue) {
            CardDisplayState.DRAWING -> {
                val absIndexFromMiddle = abs(PlayerState.currentHand.size / 2)
                restingX = MyGdxGame.WIDTH * 0.5f + (PlayerState.currentHand.size / 2 - (PlayerState.currentHand.size - 1)) * CardView.cardWidth
                restingY = -(cardHeight / 3f + absIndexFromMiddle * cardHeight / PlayerState.currentHand.size / 2f)
                restingRotation = -((180f / 16f * (-PlayerState.currentHand.size / 2)))
                setPosition(DeckButton.x, DeckButton.y)
                setScale(0f, 0f)
                rotation = 0f
                Hand.addActor(this)
                val sizeUpAction = Actions.scaleTo(1f, 1f, resolutionTime)
                val fadeInAction = Actions.fadeIn(resolutionTime)
                val moveAction = Actions.sequence(
                        Actions.moveTo(restingX / 2, restingY, resolutionTime / 2),
                        Actions.moveTo(restingX, restingY, resolutionTime / 2)
                )
                val rotationAction = Actions.rotateTo(restingRotation, resolutionTime)
                val combinedAction = Actions.sequence(
                        Actions.parallel(moveAction, rotationAction, sizeUpAction, fadeInAction),
                        Actions.run { cardDisplayState = CardDisplayState.RESTING }
                )
                addAction(combinedAction)
            }
            CardDisplayState.DISCARDING -> {
                //Once you start discarding don't let anyone affect this card
                clearActions()
                clearListeners()
                val sizeDownAction = Actions.scaleTo(0f, 0f, resolutionTime)
                val fadeOutAction = Actions.fadeOut(resolutionTime)
                val moveAction = Actions.moveTo(DiscardButton.x, DiscardButton.y, resolutionTime)
                val rotationAction = Actions.rotateTo(0f, resolutionTime)
                val combinedAction = Actions.sequence(
                        Actions.parallel(moveAction, rotationAction, sizeDownAction, fadeOutAction),
                        Actions.run {
                            Hand.cardsInHand.remove(this)
                            this.remove()
                            this.clear()
                        }
                )
                addAction(combinedAction)
            }
            CardDisplayState.HOVERING -> {
                addAction(
                        Actions.parallel(
                                Actions.moveTo(restingX,
                                        (cardHeight * 1.5f - cardHeight) / 2,
                                        Hand.cardSelectAnimationDuration),
                                Actions.scaleTo(1.5f, 1.5f,
                                        Hand.cardSelectAnimationDuration),
                                Actions.rotateTo(0f, Hand.cardSelectAnimationDuration)))
                toFront()
            }
            CardDisplayState.RESTING -> {
                clearActions()
                //If you finished drawing do the move instantly
                if (oldValue == CardDisplayState.DRAWING)
                    addAction(Actions.parallel(Actions.scaleTo(1f, 1f),
                                    Actions.moveTo(restingX, restingY),
                                    Actions.rotateTo(restingRotation)))
                //Otherwise, interpolate and return to hand slowly
                else
                    addAction(
                            Actions.parallel(Actions.scaleTo(1f, 1f,
                                    Hand.cardSelectAnimationDuration),
                                    Actions.moveTo(restingX, restingY,
                                            Hand.cardSelectAnimationDuration),
                                    Actions.rotateTo(restingRotation,
                                            Hand.cardSelectAnimationDuration)))
                Hand.cardsInHand.forEach { it.toFront() }
            }
            CardDisplayState.TARGETTING -> {
                clearActions()
                toFront()
                addAction(Actions.scaleTo(1.5f, 1.5f))
                if (card.onPlay?.first()?.targets != null)
                    moveTo(MyGdxGame.WIDTH / 2, 0f)
//                else
//                    moveTo(0f, 0f)
                rotation = 0f
            }

        }
    }

    override fun act(delta: Float) {
        super.act(delta)
        if (cardDisplayState == CardDisplayState.TARGETTING)
            if (card.onPlay?.first()?.targets == null)
                moveTo(stage.screenToStageCoordinates(Vector2(Gdx.input.x.toFloat(),
                        Gdx.input.y.toFloat())))
        if (this@CardView == focused && touchDown && this@CardView.card.onPlay?.first()?.targets != null) {
            //Lock the targetting to inside the screen
            val clampedPositon = stage.screenToStageCoordinates(Vector2(Gdx.input.x.toFloat(),
                    Gdx.input.y.toFloat())).toInsideStagePosition()
            val controlPoints: Array<Vector2> = arrayOf(
                    Vector2(MyGdxGame.WIDTH / 2, cardHeight * 1.5f),
                    Vector2(MyGdxGame.WIDTH / 2, clampedPositon.y),
                    Vector2(clampedPositon.x, clampedPositon.y)
            )
            pathFromCardToMouse.set(*controlPoints)
        }
    }

    /**
     * Creates a new card with the same values as this card
     */
    fun toDisplayCard(): CardView =
            with(CardView(card)) {
                setSize(cardGroupDisplayWidth, cardGroupDisplayHeight)
                setOrigin(width / 2f, height / 2f)
                addListener(object : ClickListener() {
                    override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                       fromActor: Actor?) {
                        clearActions()
                        addAction(Actions.parallel(
                                Actions.scaleTo(1.5f, 1.5f, resolutionTime)))
                        zIndex += 1
                    }

                    override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                      toActor: Actor?) {
                        clearActions()
                        addAction(Actions.parallel(
                                Actions.scaleTo(1f, 1f, resolutionTime)))
                        zIndex += 1
                    }
                })
                return this
            }

    //The image to be displayed by the card
    val cardBackground = Image(Assets.combinedCard).also{it.setFillParent(true)}

    //The label of the mana cost
    val manaCostLabel = Label(cost.toString(),
            Label.LabelStyle(Assets.manaFont, Color.RED)).also {
        it.setAlignment(Align.center)
        val size = cardWidth / 3
        it.setSize(size, size)
        it.setPosition(cardWidth / 2 - size / 2, cardHeight - size)
    }

    init {
        setSize(cardWidth, cardHeight)
        setOrigin(width / 2f, height / 2f)
        addActor(cardBackground)
        addActor(manaCostLabel)
        //The general case listener for cards being in the hand
        addListener(object : ClickListener() {
            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int,
                               fromActor: Actor?) {
                //If no card is presently being held, hovering does it's growing thing
                if (!touchDown) {
                    //Move the old  focused card back to it's resting position
                    focused?.cardDisplayState = CardDisplayState.RESTING
                    focused = this@CardView.also {
                        it.cardDisplayState = CardDisplayState.HOVERING
                    }
                }
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int,
                              toActor: Actor?) {
                //If no card is presently being held, hovering does it's growing thing
                if (!touchDown) {
                    //Move the old  focused card back to it's resting position
                    focused?.cardDisplayState = CardDisplayState.RESTING
                    focused = null
                }
            }

            override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                 button: Int) {
                touchDown = false
                focused?.cardDisplayState = CardDisplayState.RESTING
                focused = null
                Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow)
                super.touchUp(event, x, y, pointer, button)
            }

            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                   button: Int): Boolean {
                touchDown = true
                focused?.cardDisplayState = CardDisplayState.TARGETTING
                return super.touchDown(event, x, y, pointer, button)
            }

        })
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        if(card.cost <= PlayerState.mana && cardDisplayState != CardDisplayState.DISCARDING) {
            renderWithGreenOutline(batch,parentAlpha)
        } else {
            super.draw(batch, parentAlpha)
        }

    }
    private fun renderWithGreenOutline(batch: Batch?, parentAlpha: Float) {
        Assets.outlineShader.let {
            batch!!.end()
            it.bind()
            manaCostLabel.isVisible = false
            it.setUniformf("u_viewportInverse",
                    Vector2(1f / stage!!.viewport.worldWidth, 1f / stage!!.viewport.worldHeight))
            it.setUniformf("u_offset", 24f)
            it.setUniformf("u_step", 8f)
            it.setUniformf("u_color", Vector3(0f, 1f, 0f))
            batch.shader = it
            batch.begin()
            super.draw(batch, parentAlpha)
            batch.end()
            batch.shader = null
            batch.begin()
        }
        //Draw the text for the mana cost
        manaCostLabel.isVisible = true
        super.applyTransform(batch, super.computeTransform())
        manaCostLabel.draw(batch, parentAlpha)
        super.resetTransform(batch)
    }

    //Helper function that moves the center of a card to a point
    fun moveTo(x: Float, y: Float) {
        val minX = width * scaleX / 2
        val maxX: Float = MyGdxGame.WIDTH - (width * scaleX) / 2
        val minY = height * scaleY / 2
        val maxY: Float = MyGdxGame.HEIGHT - (height * scaleY) / 2
        val newX = min(maxX, max(x, minX))
        val newY = min(maxY, max(y, minY))
        setPosition(newX, newY, Align.center)
    }

    fun moveTo(position: Vector2) {
        val minX = width * scaleX / 2
        val maxX: Float = MyGdxGame.WIDTH - (width * scaleX) / 2
        val minY = height * scaleY / 2
        val maxY: Float = MyGdxGame.HEIGHT - (height * scaleY) / 2
        val newX = min(maxX, max(position.x, minX))
        val newY = min(maxY, max(position.y, minY))
        setPosition(newX, newY, Align.center)
    }
}
