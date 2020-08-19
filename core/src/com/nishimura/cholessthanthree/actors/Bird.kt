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
    override var maxHealth = 50
    override var currentHealth by Delegates.observable(maxHealth, { property, oldValue, newValue ->
        if(newValue <= 0) {
            clearActions()
            clearListeners()
            PlayerState.targetableEntities.remove(this)
            addAction(Actions.sequence(
                    Actions.parallel(Actions.moveTo(MyGdxGame.WIDTH, MyGdxGame.HEIGHT, 1.5f,
                            Interpolation.swingIn),
                            Actions.rotateBy(3600f, 1.5f)),
                    Actions.removeActor()))
        }
    })
    override var isDead = false
    init {
        val flap = Actions.forever(
                Actions.sequence(Actions.moveBy(0f, -50f, 1.5f, Interpolation.exp5),
                        Actions.moveBy(0f, 50f, 1.5f)))

        addAction(flap)
        setPosition(MyGdxGame.WIDTH * 0.8f - width, MyGdxGame.HEIGHT * 0.6f)
    }
}