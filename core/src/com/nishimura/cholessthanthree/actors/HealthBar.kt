package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.PlayerState


class HealthBar(style: LabelStyle) : Label("Health: " + PlayerState.health + "/" + PlayerState.maxHealth, style) {
    private val renderer = ShapeRenderer()
    val healthListener = {old: Int, new:Int -> setText("Health: " + PlayerState.health + "/" + PlayerState.maxHealth)}
    init {
        PlayerState.healthListeners.add(healthListener)
        PlayerState.maxHealthlisteners.add(healthListener)
    }
    override fun draw(batch: Batch, parentAlpha: Float) {
        batch.end()
        val progress = PlayerState.health.toFloat() / PlayerState.maxHealth
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = Color.BLUE
        renderer.ellipse(x,y,glyphLayout.width*scaleX,glyphLayout.height*scaleY)
        renderer.color = Color.RED
        renderer.ellipse(x,y,progress*glyphLayout.width*scaleX,glyphLayout.height*scaleY)
        renderer.end()
        batch.begin()
        super.draw(batch, parentAlpha)
    }

    fun dispose() {
        PlayerState.healthListeners.remove(healthListener)
        PlayerState.maxHealthlisteners.remove(healthListener)
    }
}