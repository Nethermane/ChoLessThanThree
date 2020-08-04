package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
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

    enum class State(val animation: Animation<TextureRegion>?) {
        Still(null),
        Running(Animation<TextureRegion>(0.02f, Assets.atlas.findRegions("run/run"),
                PlayMode.LOOP)),
        Dancing(Animation<TextureRegion>(0.053f, Assets.atlas.findRegions("idle/idle"),
                PlayMode.LOOP)),
        Punching(Animation<TextureRegion>(0.033f, Assets.atlas.findRegions("punch/punch"),
                PlayMode.LOOP)),
        LeftPunching(Animation<TextureRegion>(0.033f, Assets.atlas.findRegions("leftPunch/leftPunch"),
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
        })
    }

    override fun act(delta: Float) {
        super.act(delta)
        animTime += delta
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        state.animation?.let {
            batch?.draw(it.getKeyFrame(animTime, true), x, y, width, height)
        } ?: run {
            super.draw(batch, parentAlpha)
        }
    }
}