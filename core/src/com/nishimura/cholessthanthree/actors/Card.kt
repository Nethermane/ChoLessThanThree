package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShaderProgram
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
import com.badlogic.gdx.utils.GdxRuntimeException
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.PlayerState.handSize
import com.nishimura.cholessthanthree.Targetable
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates
import kotlin.reflect.KClass


data class Card(private var _cost: Int,
                private var onPlay: Effect? = null,
                private var onDraw: Effect? = null,
                private var onDiscard: Effect? = null,
                val targets: List<KClass<out Targetable>> = emptyList()
) : Targetable, Group() {
    override fun hit(x: Float, y: Float): Boolean {
        return (x >= this.x && x < width + this.x && y >= this.y && y < height + this.y)
    }

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

    var cost by Delegates.observable(_cost) { property, oldValue, newValue ->
        manaCostLabel.setText(newValue.toString())
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
    val cardBackground = Image(
            Assets.combinedCard).also { it.setFillParent(true) }
    val manaCostLabel = Label(_cost.toString(),
            Label.LabelStyle(Assets.manaFont, Color.RED)).also {
        it.setAlignment(Align.center)
    }

    override fun setSize(width: Float, height: Float) {
        super.setSize(width, height)
        with(manaCostLabel) {
            val size = cardWidth / 3
            setSize(size, size)
            setPosition(width/2 - size / 2, height - size)
        }
    }
    var shaderOutline: ShaderProgram? = null

    init {
        setSize(cardWidth, cardHeight)
        setOrigin(width / 2f, height / 2f)
        addActor(cardBackground)
        addActor(manaCostLabel)
        loadShader()
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        shaderOutline?.let {
            batch!!.end()
            it.bind()
            manaCostLabel.isVisible = false
            it.setUniformf("u_viewportInverse",
                    Vector2(1f / stage!!.viewport.worldWidth, 1f / stage!!.viewport.worldHeight))
            it.setUniformf("u_offset", 24f)
            it.setUniformf("u_step", 8f)
            it.setUniformf("u_color", Vector3(0f, 1f, 0f))
//            it.setUniformf("color", Color.GREEN)
//            it.setUniformf("iTime", 0f)
            //it.setUniformf("inv_viewport", Vector2(1 / MyGdxGame.WIDTH, 1 / MyGdxGame.HEIGHT))
            //it.setUniformMatrix("u_projTrans", batch.projectionMatrix)
            //it.setUniformi("u_texture", 0)
//            it.setUniformf("width", MyGdxGame.WIDTH)
//            it.setUniformf("height", MyGdxGame.HEIGHT)
//            it.setUniformf("backColor", Color.RED)
            batch.shader = it
            batch.begin()
            super.draw(batch, parentAlpha)
            batch.end()
            batch.shader = null
            batch.begin()
        }
        manaCostLabel.isVisible = true
        super.applyTransform(batch, super.computeTransform())
        manaCostLabel.draw(batch, parentAlpha)
        super.resetTransform(batch)
        //super.draw(batch, parentAlpha)

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

    fun loadShader() {
        val vertexShader: String
        val fragmentShader: String
        vertexShader = Gdx.files.internal("shader/df_vertex.glsl").readString()
        fragmentShader = Gdx.files.internal("shader/outline.glsl").readString()
        shaderOutline = ShaderProgram(vertexShader, fragmentShader)
//        shaderOutline = SpriteBatch.createDefaultShader()
//        shaderOutline = Gaussian.createBlurShader(MyGdxGame.WIDTH.toInt(), MyGdxGame.HEIGHT.toInt())
        ShaderProgram.pedantic = false
        //shaderOutline = ShaderProgram(vertexShader, fragmentShader)
        if (!shaderOutline!!.isCompiled()) throw GdxRuntimeException(
                "Couldn't compile shader: " + shaderOutline!!.getLog())
    }
}
