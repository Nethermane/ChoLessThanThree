package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.nishimura.cholessthanthree.*
import com.nishimura.cholessthanthree.player.AnimState


object Player : Actor(), Targetable,Damageable {
    override var maxHealth = 70
    override var currentHealth = maxHealth
    override var isDead = false
    val runFastAnim = Animation<TextureRegion>(0.02f, Assets.atlas.findRegions("runFast/runFast"),
            PlayMode.LOOP)
    private val pendingStates = ArrayList<AnimState>()
    var entered = false
    val idleTexture = Assets.atlas.findRegion(Assets.character)
    fun executeStates(states: List<AnimState>, clear:Boolean = false) {
        if(clear)
            pendingStates.clear()
        pendingStates.addAll(states)
    }

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


    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.color = color
        //Properly jump animations based on time if frames were to drop
        while (pendingStates.isNotEmpty() && pendingStates.first().isDone()) {
            val oldState = pendingStates.removeAt(0)
            if (pendingStates.isNotEmpty()) {
                pendingStates.first().animTime += oldState.getOverFlowedTime()
            }
        }
        if (pendingStates.isNotEmpty()) {
            with(pendingStates.first()) {
                batch?.draw(this.state?.animation?.getKeyFrame(this.animTime, true), x, y, width,
                        height)
            }
        } else {
            batch?.draw(idleTexture, x, y, width, height)
        }
    }

    override fun hit(x: Float, y: Float): Boolean {
        return  (x >= this.x && x < width+this.x && y >= this.y && y < height+this.y)
    }
}