package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.PlayerState
import kotlin.properties.Delegates

class Bird : Enemy(Image(Assets.atlasSkin.getDrawable(Assets.bird)).apply{
    color = Color.BLACK
    setSize(MyGdxGame.WIDTH * 0.2f, MyGdxGame.WIDTH * 0.05f)
    setOrigin(width / 2, height / 2)
}) {
    override fun playExitAnimationAndActions() {
        addAction(Actions.sequence(
                Actions.parallel(Actions.moveTo(MyGdxGame.WIDTH, MyGdxGame.HEIGHT, 1.5f,
                        Interpolation.swingIn),
                        Actions.rotateBy(3600f, 1.5f)),
                Actions.removeActor()))
        healthBarText.isVisible = false
        healthBarFront.isVisible = false
        healthBarBack.isVisible = false
    }
    override var isDead = false
    init {
        maxHealth = 6
        currentHealth = 6
        val flap = Actions.forever(
                Actions.sequence(Actions.moveBy(0f, -50f, 1.5f, Interpolation.exp5),
                        Actions.moveBy(0f, 50f, 1.5f)))

        addAction(flap)
        setPosition(MyGdxGame.WIDTH * 0.8f - width, MyGdxGame.HEIGHT * 0.6f)
    }
}