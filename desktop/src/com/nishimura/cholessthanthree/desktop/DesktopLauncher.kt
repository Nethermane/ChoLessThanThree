package com.nishimura.cholessthanthree.desktop

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration
import com.nishimura.cholessthanthree.MyGdxGame

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.samples =16
        config.vSyncEnabled = false
        config.foregroundFPS = 0
        config.backgroundFPS = 0
        LwjglApplication(MyGdxGame(), config)
    }
}