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
        config.foregroundFPS = 0
        config.backgroundFPS = -1
        config.vSyncEnabled = false
        LwjglApplication(MyGdxGame(), config)
    }
}