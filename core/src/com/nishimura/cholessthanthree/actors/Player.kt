package com.nishimura.cholessthanthree.actors

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
import com.nishimura.cholessthanthree.player.AnimActionType
import com.nishimura.cholessthanthree.player.AnimDirection
import com.nishimura.cholessthanthree.player.AnimState


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
        if(pendingStates.isEmpty() && states.isNotEmpty()) {
            this.animDirection = states.first().animDirection ?: AnimDirection.RIGHT
            states.forEach { it.target = target }
            when(states.first().animActionType) {
                AnimActionType.MOVE_TO_ENEMY -> addAction(Actions.moveTo(states.first().target!!.getTargetX()-width,y, states.first().totalDuration))
                AnimActionType.MOVE_TO_REST -> addAction(Actions.moveTo(0f, MyGdxGame.HEIGHT * 0.4f, states.first().totalDuration))
                AnimActionType.NONE -> {}
            }
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
        //Increment animation time based on world time
        pendingStates.firstOrNull()?.let {
            it.animTime += delta
        }
        //Change animation to next
        while (pendingStates.isNotEmpty() && pendingStates.first().isDone()) {
            val oldState = pendingStates.removeAt(0)
            pendingStates.firstOrNull()?.let {
                it.animTime += oldState.getOverFlowedTime()
                when(it.animActionType) {
                    AnimActionType.MOVE_TO_ENEMY -> addAction(Actions.moveTo(it.target!!.getTargetX()-width,y, it.totalDuration))
                    AnimActionType.MOVE_TO_REST -> addAction(Actions.moveTo(0f, MyGdxGame.HEIGHT * 0.4f, it.totalDuration))
                    AnimActionType.NONE -> {}
                }
                this.animDirection = it.animDirection ?: AnimDirection.RIGHT
            } ?: run {
                this.animDirection = AnimDirection.RIGHT

            }
        }
    }
    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.color = color
        //Properly jump animations based on time if frames were to drop

        if (pendingStates.isNotEmpty()) {
            with(pendingStates.first()) {
                when(this@Player.animDirection) {
                    AnimDirection.LEFT -> batch?.draw(this.state?.animation?.getKeyFrame(this.animTime, true), x + width, y, -width , height)
                    AnimDirection.RIGHT -> batch?.draw(this.state?.animation?.getKeyFrame(this.animTime, true), x, y, width,
                            height)
                }
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