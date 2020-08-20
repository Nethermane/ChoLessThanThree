package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.nishimura.cholessthanthree.*
import kotlin.math.max

abstract class Enemy(val image: Image) : Group(), Targetable, Damageable {
    companion object {
        val healthBarWidth = MyGdxGame.WIDTH * 0.05f
        val healthBarHeight = healthBarWidth / 4
    }

    init {
        this.setSize(image.width, image.height + healthBarHeight)
        image.setPosition(0f, healthBarHeight)
        this.addActor(image)
        this.setOrigin(Align.center)
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

    val healthBarBack = Image(Assets.atlasSkin.getRegion("health_empty")).also {
        it.setPosition(width / 2 - healthBarWidth / 2, 0f)
        it.setSize(healthBarWidth, healthBarHeight)
        addActor(it)
    }
    val healthBarFront = Image(Assets.atlasSkin.getRegion("health_full")).also {
        it.setPosition(width / 2 - healthBarWidth / 2, 0f)
        it.setSize(healthBarWidth, healthBarHeight)
        addActor(it)
    }
    val healthBarText = Label("t", Label.LabelStyle(Assets.manaFont, Color.BLACK)).also {
        it.width = width
        it.setAlignment(Align.center)
        it.y = -it.height
        addActor(it)
    }

    final override var maxHealth: Int = 1
        get() = field
        set(value) {
            field = value
            if(maxHealth > currentHealth) {
                currentHealth = maxHealth
            }
            healthBarText.setText("$value/$maxHealth")
        }
    final override var currentHealth = 1
        get() = field
        set(value) {
            field = value
            if(value <= 0) {
                field = 0
                clearActions()
                clearListeners()
                PlayerState.targetableEntities.remove(this)
                playExitAnimationAndActions()
            }
            healthBarText.setText("$value/$maxHealth")

        }
    abstract fun playExitAnimationAndActions()

    final override fun addActor(actor: Actor?) {
        super.addActor(actor)
    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        batch?.color = Color.WHITE
        super.draw(batch, parentAlpha)
        if (currentHealth > 0) {
            if (this.clipBegin(x + healthBarFront.x, y + healthBarFront.y, healthBarWidth * currentHealth / maxHealth, healthBarFront.height)) {
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