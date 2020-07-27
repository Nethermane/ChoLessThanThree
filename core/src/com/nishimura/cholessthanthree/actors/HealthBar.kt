package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.PlayerState


class HealthBar : Actor() {
    private var textToRender = "hp:0000/0000"
    private val healthListener = { textToRender = "hp:" + PlayerState.health + "/" + PlayerState.maxHealth }
    private val glyphlayout: GlyphLayout = GlyphLayout(Assets.healthFont, textToRender)
    private val textPaddingFromOvalVertical = MyGdxGame.HEIGHT/64f
    private val renderer = ShapeRenderer()
    init {
        PlayerState.healthListeners.add(healthListener)
        PlayerState.maxHealthlisteners.add(healthListener)
        width = MyGdxGame.WIDTH / 4
        //Ensure enough width to display font
        while (glyphlayout.width > width) {
            width+=MyGdxGame.WIDTH / 16
        }
        height = glyphlayout.height + textPaddingFromOvalVertical*2
        healthListener.invoke()

    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        val progress = PlayerState.health.toFloat() / PlayerState.maxHealth
        //Project on same matrix so the shape rendering lines up properly
        if(renderer.projectionMatrix != batch.projectionMatrix)
            renderer.projectionMatrix = batch.projectionMatrix
        batch.end()
        renderer.begin(ShapeRenderer.ShapeType.Filled)
        renderer.color = Color.BLUE
        renderer.ellipse(x, y, width * scaleX, (height) * scaleY)
        renderer.color = Color.RED
        renderer.ellipse(x, y, progress * width * scaleX, (height) * scaleY)
        renderer.end()
        batch.begin()
        Assets.healthFont.draw(batch, textToRender, x, y + height*scaleY - textPaddingFromOvalVertical, width*scaleY, Align.center, false)
        super.draw(batch, parentAlpha)
    }

    fun dispose() {
        PlayerState.healthListeners.remove(healthListener)
        PlayerState.maxHealthlisteners.remove(healthListener)
    }
}
