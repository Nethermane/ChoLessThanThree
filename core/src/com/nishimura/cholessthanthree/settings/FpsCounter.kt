package com.nishimura.cholessthanthree.settings

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable
import com.badlogic.gdx.utils.TimeUtils


/**
 * A nicer class for showing framerate that doesn't spam the console
 * like Logger.log()
 *
 * @author William Hartman
 */
class FpsCounter : Disposable {
    private val font: BitmapFont = BitmapFont().also {
        it.color = Color.GREEN
    }
    private val batch: SpriteBatch = SpriteBatch()
    private var cam: OrthographicCamera = OrthographicCamera(Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat())
    fun resize(screenWidth: Int, screenHeight: Int) {
        cam = OrthographicCamera(screenWidth.toFloat(), screenHeight.toFloat())
        cam.translate(screenWidth / 2.toFloat(), screenHeight / 2.toFloat())
        cam.update()
        batch.projectionMatrix = cam.combined
    }
    fun render() {
        batch.begin()
        font.draw(batch, Gdx.graphics.framesPerSecond.toString(), 3f,
                Gdx.graphics.height - 3.toFloat())
        batch.end()
    }

    override fun dispose() {
        font.dispose()
        batch.dispose()
    }
}