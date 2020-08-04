package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame


object Player : Image(Assets.atlasSkin.getDrawable(Assets.character)) {
    val runFastAnim = Animation<TextureRegion>(0.02f, Assets.atlas.findRegions("runFast/runFast"),
            PlayMode.LOOP)
    var animTime = 0f
    var state: State = State.Still
    var entered = false
    val mySprite = Assets.atlas.findRegion(Assets.character)
    enum class State(val animation: Animation<TextureRegion>?) {
        Still(null),
        Running(Animation<TextureRegion>((1f/24f), Assets.atlas.findRegions("run/run"),
                PlayMode.LOOP)),
        Dancing(Animation<TextureRegion>((1f/24f), Assets.atlas.findRegions("dance/dance"),
                PlayMode.LOOP)),
        Punching(Animation<TextureRegion>((1f/24f), Assets.atlas.findRegions("rightPunch/rightPunch"),
                PlayMode.LOOP));

        open operator fun next(): State {
            // No bounds checking required here, because the last instance overrides
            return values()[(ordinal + 1) % values().size]
        }
    }

    init {
        val width = MyGdxGame.WIDTH * 0.3f
        setSize(width, width * 1.06640625f)
        setPosition(0f, MyGdxGame.HEIGHT * 0.4f)
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                state = state.next()
                animTime = 0f
            }

            override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int,
                               fromActor: Actor?) {
                super.enter(event, x, y, pointer, fromActor)
                entered = true
                println("enter")
            }

            override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int,
                              toActor: Actor?) {
                super.exit(event, x, y, pointer, toActor)
                entered = false
                println("exit")

            }
        })
    }

    override fun act(delta: Float) {
        super.act(delta)
        animTime += delta
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.color = Color.BLACK
        state.animation?.let {
            batch?.draw(it.getKeyFrame(animTime, true), x, y, width, height)
        } ?: run {
            batch?.draw(mySprite,x,y,width,height)
        }
    }
}