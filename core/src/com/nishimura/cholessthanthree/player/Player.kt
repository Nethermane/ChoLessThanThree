package com.nishimura.cholessthanthree.player

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.Damageable
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.Targetable


object Player : Actor(), Targetable,Damageable {
    override fun getTargetX(): Float {
        return x
    }

    override fun getTargetY(): Float {
        return y
    }
    override var maxHealth = 70
    override var currentHealth = maxHealth
    override var isDead = false
    override var block = 0
    val runFastAnim = Animation<TextureRegion>(0.02f, Assets.atlas.findRegions("runFast/runFast"),
            PlayMode.LOOP)
    private val pendingStates = ArrayList<AnimState>()
    var entered = false
    val idleTexture = Assets.atlas.findRegion(Assets.character)
    fun executeStates(states: List<AnimState>, target:Targetable?, clear:Boolean = false) {
        if(clear)
            pendingStates.clear()
            clearActions()
            setPosition(0f, MyGdxGame.HEIGHT * 0.4f)
        states.forEach { it.target = target }
        if(pendingStates.isEmpty() && states.isNotEmpty()) {
            animDirection = states.first().animDirection ?: AnimDirection.RIGHT
            addActionForAnimActionType(states.first())
        }
        pendingStates.addAll(states)
    }
    var animDirection = AnimDirection.RIGHT


    init {
        debug= true
        val width = MyGdxGame.WIDTH * 0.3f
        setSize(width, width * 1.06640625f)
        setPosition(0f, MyGdxGame.HEIGHT * 0.4f)
        color = Color.BLACK
        addListener(object : ClickListener() {

            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int,
                               fromActor: Actor?) {
                super.enter(event, x, y, pointer, fromActor)
                entered = true
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int,
                              toActor: Actor?) {
                super.exit(event, x, y, pointer, toActor)
                entered = false

            }
        })
    }

    override fun act(delta: Float) {
        super.act(delta)
        println(pendingStates.size)
        //Increment animation time based on world time
        pendingStates.firstOrNull()?.let {
            it.animTime += delta
        }
        //Change animation to next
        while (pendingStates.isNotEmpty() && pendingStates.first().isDone()) {
            val oldState = pendingStates.removeAt(0)
            pendingStates.firstOrNull()?.let {
                it.animTime = oldState.getOverFlowedTime()
                addActionForAnimActionType(it)
                animDirection = it.animDirection ?: AnimDirection.RIGHT
            } ?: run {
                animDirection = AnimDirection.RIGHT

            }
        }
    }
    fun addActionForAnimActionType(animState: AnimState) {
        when(animState.animActionType) {
            AnimActionType.MOVE_TO_ENEMY -> addAction(Actions.moveTo(animState.target!!.getTargetX()-width,y, animState.totalDuration))
            AnimActionType.MOVE_TO_REST -> addAction(Actions.moveTo(0f, MyGdxGame.HEIGHT * 0.4f, animState.totalDuration))
            AnimActionType.NONE -> {}
        }
    }
    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.color = color
        //Properly jump animations based on time if frames were to drop

        if (pendingStates.isNotEmpty()) {
            with(pendingStates.first()) {
                if(this.reversed) {
                    this.state?.animation?.playMode = PlayMode.LOOP_REVERSED
                }
                val frame = this.state?.animation?.getKeyFrame(this.animTime, true)
                when(Player.animDirection) {
                    AnimDirection.LEFT -> batch?.draw(frame, x + width, y, -width , height)
                    AnimDirection.RIGHT -> batch?.draw(frame, x, y, width,
                            height)
                }
                this.state?.animation?.playMode = PlayMode.LOOP
            }
        } else {
            when(animDirection) {
                AnimDirection.LEFT -> batch?.draw(idleTexture, x + width, y, -width , height)
                AnimDirection.RIGHT -> batch?.draw(idleTexture, x, y, width,
                        height)
            }
        }
    }

    override fun hit(x: Float, y: Float): Boolean {
        return  (x >= this.x && x < width+this.x && y >= this.y && y < height+this.y)
    }
}