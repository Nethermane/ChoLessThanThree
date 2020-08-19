package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.Damageable
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.Targetable

abstract class Enemy(val image: Image) : Group(), Targetable, Damageable {
    companion object {
        val healthBarWidth = MyGdxGame.WIDTH * 0.05f
        val healthBarHeight = healthBarWidth / 4
    }

    init {
        this.setSize(image.width, image.height + healthBarHeight)
        //Leave room for health bar below
        image.setPosition(0f, healthBarHeight)
        this.addActor(image)
    }
    override var block = 0
    override fun hit(x: Float, y: Float): Boolean {
        return (x >= this.x && x < width + this.x && y >= this.y && y < height + this.y)
    }

    override fun getTargetX(): Float {
        return x + width / 2
    }

    override fun getTargetY(): Float {
        return y + height / 2
    }

    val healthBarBack = Image(Assets.atlasSkin.getRegion("health_empty")).apply {
        setPosition(width / 2 - healthBarWidth / 2, 0f)
        setSize(healthBarWidth, healthBarHeight)
        addActor(this)
    }
    val healthBarFront = Image(Assets.atlasSkin.getRegion("health_full")).apply {
        setPosition(width / 2 - healthBarWidth / 2, 0f)
        setSize(healthBarWidth, healthBarHeight)
        addActor(this)
    }



    final override fun addActor(actor: Actor?) {
        super.addActor(actor)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.color = Color.WHITE
        super.draw(batch, parentAlpha)
        if (currentHealth > 0) {
            if (this.clipBegin(x+healthBarFront.x, y + healthBarFront.y, healthBarWidth * currentHealth / maxHealth, healthBarFront.height)) {
                children.filterNot { it == healthBarFront }.forEach { it.isVisible = false }
                super.draw(batch, parentAlpha)
                children.filterNot { it == healthBarFront }.forEach { it.isVisible = true }
                clipEnd()
            } else {
                println("clip fail")
            }
        }

    }

}