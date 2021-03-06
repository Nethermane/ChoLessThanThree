package com.nishimura.cholessthanthree

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.viewport.FitViewport
import com.nishimura.cholessthanthree.screens.MenuScreen
import com.nishimura.cholessthanthree.settings.FpsCounter
import kotlin.properties.Delegates

class MyGdxGame : Game() {
    companion object {
        const val gameName = "CHOLessThanThree"
        const val grpahicsPreferenceString = gameName +"GraphicsSettings"
        var graphicsPrefs: Preferences by Delegates.notNull()
        private set
        var WIDTH: Float by Delegates.notNull()
        private set
        var HEIGHT: Float by Delegates.notNull()
        private set
    }
    val fpsCounter by lazy{ FpsCounter() }

    override fun create() {
        graphicsPrefs =  Gdx.app.getPreferences("Graphics")
        graphicsPrefs.put(mapOf("test" to "huh"))
        graphicsPrefs.flush()
        WIDTH = graphicsPrefs.getFloat("width", 1024f)
        HEIGHT = graphicsPrefs.getFloat("height", 768f)
        Gdx.graphics.setTitle("Stickman Roguelite Deckbuilder")
        this.setScreen(MenuScreen(this))
        Gdx.app.log("KRU", "renderer: " + Gdx.gl.glGetString(GL20.GL_RENDERER));
        Gdx.app.log("KRU", "vendor: " + Gdx.gl.glGetString(GL20.GL_VENDOR));
        Gdx.app.log("KRU", "version: " + Gdx.gl.glGetString(GL20.GL_VERSION));
    }

    override fun render() {
        super.render()
        fpsCounter.render()
    }

    override fun dispose() {
        super.dispose()
        fpsCounter.dispose()
    }

    override fun resize(width: Int, height: Int) {
        super.resize(width, height)
        fpsCounter.resize(width,height)
    }
}