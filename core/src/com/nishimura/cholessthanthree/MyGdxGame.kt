package com.nishimura.cholessthanthree

import com.badlogic.gdx.Game
import com.nishimura.cholessthanthree.screens.MenuScreen

class MyGdxGame : Game() {
    companion object {
        const val WIDTH = 640f
        const val HEIGHT = 480f
    }
    override fun create() {
        this.setScreen(MenuScreen(this))
    }
}