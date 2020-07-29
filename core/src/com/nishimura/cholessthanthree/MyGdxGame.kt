package com.nishimura.cholessthanthree

import com.badlogic.gdx.Game
import com.nishimura.cholessthanthree.screens.MenuScreen

class MyGdxGame : Game() {
    companion object {
        const val WIDTH = 1024f
        const val HEIGHT = 768f
    }
    override fun create() {
        this.setScreen(MenuScreen(this))
    }
}