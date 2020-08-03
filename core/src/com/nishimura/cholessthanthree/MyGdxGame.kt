package com.nishimura.cholessthanthree

import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.nishimura.cholessthanthree.screens.MenuScreen
import kotlin.properties.Delegates

class MyGdxGame : Game() {
    companion object {
        var graphicsPrefs: Preferences by Delegates.notNull()
        private set
        var WIDTH: Float by Delegates.notNull()
        private set
        var HEIGHT: Float by Delegates.notNull()
        private set
    }
    override fun create() {
        graphicsPrefs =  Gdx.app.getPreferences("Graphics")
        WIDTH = graphicsPrefs.getFloat("width", 1024f)
        HEIGHT = graphicsPrefs.getFloat("height", 768f)
        Gdx.graphics.setTitle("Stickman Roguelite Deckbuilder")
        this.setScreen(MenuScreen(this))
    }
}