package com.nishimura.cholessthanthree.screens

import com.badlogic.gdx.*
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.viewport.FitViewport
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.CombatDeckManager
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.actors.HealthBar
import com.nishimura.cholessthanthree.player.Player
import ktx.app.KtxScreen


class CombatScreen(val game: Game) : KtxScreen {
    val viewport = FitViewport(MyGdxGame.WIDTH, MyGdxGame.HEIGHT)
    val stage = Stage(viewport, PolygonSpriteBatch())
    val deckTextButton: TextButton = TextButton("Deck", TextButton.TextButtonStyle(null, null, null, Assets.font).apply{this.fontColor= Color.BLACK}).also {
        it.setPosition(0f, MyGdxGame.HEIGHT - it.height)
    }
    var health: HealthBar = HealthBar().also {
        it.setPosition(deckTextButton.x + deckTextButton.width, MyGdxGame.HEIGHT - it.height)

    }
    val combatDeckManager = CombatDeckManager
    init {
        deckTextButton.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                Player.currentHealth--
            }
        })
        stage.addActor(Image(Assets.atlasSkin.getDrawable("paper")).also { it.setSize(MyGdxGame.WIDTH, MyGdxGame.HEIGHT) })
        stage.addActor(deckTextButton)
        stage.addActor(health)
        stage.addActor(combatDeckManager)
        combatDeckManager.beginTurn()
        val multiplexer = InputMultiplexer()
        Gdx.input.inputProcessor = multiplexer
        multiplexer.addProcessor(object: InputProcessor {
            override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                return false
            }

            override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
                return false
            }

            override fun keyTyped(character: Char): Boolean {
                return false
            }

            override fun scrolled(amount: Int): Boolean {
                return false
            }

            override fun keyUp(keycode: Int): Boolean {
                return false
            }

            override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
                return false
            }

            override fun keyDown(keycode: Int): Boolean {
                if(keycode == Input.Keys.A) {
                    Player.setPosition(0f, Player.y)
                }
                return false
            }

            override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
                return false
            }

        })
        multiplexer.addProcessor(stage)
    }


    override fun render(delta: Float) {

        //Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT or if (Gdx.graphics.bufferFormat.coverageSampling) GL20.GL_COVERAGE_BUFFER_BIT_NV else 0)
        stage.act()
        stage.draw()
    }

    override fun resize(width: Int, height: Int) {
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
        stage.dispose()
    }

}