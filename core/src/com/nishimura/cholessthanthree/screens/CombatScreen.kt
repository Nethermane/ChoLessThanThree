package com.nishimura.cholessthanthree.screens

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.PlayerState
import com.nishimura.cholessthanthree.actors.HealthBar
import ktx.app.KtxScreen


class CombatScreen(val game: Game) : KtxScreen {
    val cam: OrthographicCamera = OrthographicCamera().apply {
        setToOrtho(false, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
    }
    val viewport = ExtendViewport(MyGdxGame.WIDTH, MyGdxGame.HEIGHT)
    val stage = Stage(viewport, PolygonSpriteBatch())
    val deckTextButton: TextButton = TextButton("Deck", TextButton.TextButtonStyle(null, null, null, Assets.font)).also {
        it.setPosition(0f, MyGdxGame.HEIGHT - it.height)
    }
    var health: HealthBar = HealthBar().also {
        it.setPosition(deckTextButton.x + deckTextButton.width, MyGdxGame.HEIGHT - it.height)

    }
    init {
        stage.isDebugAll = true
        deckTextButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                PlayerState.health--
            }
        })
        stage.addActor(Image(Assets.background).also { it.setSize(MyGdxGame.WIDTH, MyGdxGame.HEIGHT) })
        stage.addActor(deckTextButton)
        stage.addActor(health)
        Gdx.input.inputProcessor = stage
    }


    override fun render(delta: Float) {

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
        cam.setToOrtho(false, width.toFloat(), height.toFloat())
        stage.viewport.update(width, height)
    }

    override fun show() {
        // TODO Auto-generated method stub
    }

    override fun hide() {
        // TODO Auto-generated method stub
    }

    override fun pause() {
        // TODO Auto-generated method stub
    }

    override fun resume() {
        // TODO Auto-generated method stub
    }

    override fun dispose() {
        health.dispose()
        stage.dispose()
    }

}