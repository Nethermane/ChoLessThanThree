package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.Actor
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame


object Player: Actor() {
    val idleAnim = Animation<TextureRegion>(0.033f, Assets.atlas.findRegions("idle/idle"), PlayMode.LOOP)
    var idleAnimTime = 0f
    init {
        val width = MyGdxGame.WIDTH*0.2f
        setSize(width, width*1.06640625f)
        setPosition(MyGdxGame.WIDTH*0.2f,MyGdxGame.HEIGHT*0.4f)
    }

    override fun act(delta: Float) {
        super.act(delta)
        idleAnimTime+=delta
    }
    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        batch?.draw(idleAnim.getKeyFrame(idleAnimTime,true),x,y,width,height)
    }
}