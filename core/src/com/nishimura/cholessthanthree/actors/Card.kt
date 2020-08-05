package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.utils.Align
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.PlayerState.handSize
import com.nishimura.cholessthanthree.Targetable
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KClass


data class Card(private var cost: Int,
                private var onPlay: Effect? = null,
                private var onDraw: Effect? = null,
                private var onDiscard: Effect? = null,
                val targets: List<KClass<out Targetable>> = emptyList()
) : Targetable, Image(Assets.atlasSkin.getDrawable("cardBack")) {
    enum class CardDisplayState {
        DRAWING, RESTING, HOVERING, DISCARDING, CLICKED, RETURN_FROM_CLICKED, PLAYED_TO_DISCARD, GONE
    }

    companion object {
        val cardWidth = (MyGdxGame.WIDTH * 0.9f / handSize)
        val cardHeight = cardWidth * 2
        val cardGroupDisplayWidth = (MyGdxGame.WIDTH / 8f)
        val cardGroupDisplayHeight = (MyGdxGame.WIDTH / 4f)
        val resolutionTime = 0.25f
    }

    var restingX = 0f
    var restingY = 0f
    var restingRotation = 0f

    /**
     * Creates a new card with the same values as this card
     */
    fun toDisplayCard(): Card =
            with(Card(this)) {
                setSize(cardGroupDisplayWidth, cardGroupDisplayHeight)
                setOrigin(width / 2f, height / 2f)
                align = Align.center
                addListener(object : ClickListener() {
                    override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                       fromActor: Actor?) {
                        clearActions()
                        addAction(Actions.parallel(
                                Actions.scaleTo(1.5f,1.5f, resolutionTime)))
                        zIndex += 1
                    }

                    override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                      toActor: Actor?) {
                        clearActions()
                        addAction(Actions.parallel(
                                Actions.scaleTo(1f,1f, resolutionTime)))
                        zIndex += 1
                    }
                })
                return this
            }

    constructor(card: Card) : this(card.cost, card.onPlay, card.onDraw, card.onDiscard,
            card.targets)
//    var displayState: CardDisplayState by Delegates.observable(CardDisplayState.GONE, { property, oldValue, newValue ->
//        when(newValue) {
//            CardDisplayState.DRAWING -> {
//                setSize(0f,0f)
//                setPosition()
//                val sizeUpAction = Actions.sizeTo(Card.cardWidth,Card.cardHeight, resolutionTime)
//                val fadeInAction = Actions.fadeIn(resolutionTime)
//                val moveAction = Actions.sequence(
//                        Actions.moveTo(restingX / 2, restingY, resolutionTime / 2),
//                        Actions.moveTo(restingX, restingY, resolutionTime / 2)
//                )
//                val rotationAction = Actions.rotateTo(startingRotation, resolutionTime)
//                val waitAction = Actions.delay(resolutionTime * index)
//                val combinedAction = Actions.sequence(waitAction,
//                        Actions.parallel(moveAction, rotationAction, sizeUpAction, fadeInAction),
//                        Actions.run{card.isBeingDrawnFromDeck = false}
//                )
//            }
//        }
//    })

    var isDown = false
    var isBeingDrawnFromDeck = true
    var isBeingDiscarded = false

    init {
        setSize(cardWidth, cardHeight)
        setOrigin(width / 2f, height / 2f)

    }

    //When this card is brought to the hand
    fun onDraw(target: Targetable?) {
        onDraw?.executeEffect(target)
    }

    //When this card is put into the discard
    fun onDiscard(target: Targetable?) {
        onDiscard?.executeEffect(target)
    }

    //When this card is played
    fun onPlay(target: Targetable?) {
        if (targets.any { clazz -> clazz.isInstance(target) } || targets.isEmpty()) {
            onPlay?.executeEffect(target)
        }
    }

    abstract class Effect {
        open fun executeEffect(target: Targetable?) {}
    }

    class OffensiveEffect() : Effect()
    class DefensiveEffect() : Effect()
    class UtilityEffect() : Effect()

    fun moveTo(x: Float, y: Float) {
        val minX = width * scaleX / 2
        val maxX: Float = MyGdxGame.WIDTH - (width * scaleX) / 2
        val minY = height * scaleY / 2
        val maxY: Float = MyGdxGame.HEIGHT - (height * scaleY) / 2
        val newX = min(maxX, max(x, minX))
        val newY = min(maxY, max(y, minY))
        setPosition(newX, newY, Align.center)
    }
}
