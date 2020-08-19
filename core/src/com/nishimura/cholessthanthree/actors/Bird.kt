package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame

class Bird : Enemy() {
    override var maxHealth = 50
    override var currentHealth = 50
    override var isDead = false
    init {
        color = Color.BLACK
        val flap = Actions.forever(
                Actions.sequence(Actions.moveBy(0f, -50f, 1.5f, Interpolation.exp5),
                        Actions.moveBy(0f, 50f, 1.5f)))
        this.setDrawable(Assets.atlasSkin.getDrawable(Assets.bird))
        setSize(MyGdxGame.WIDTH * 0.2f, MyGdxGame.WIDTH * 0.05f)
        setPosition(MyGdxGame.WIDTH * 0.8f - width, MyGdxGame.HEIGHT * 0.6f)
        setOrigin(width / 2, height / 2)
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                removeAction(flap)
                addAction(Actions.sequence(
                        Actions.parallel(Actions.moveTo(MyGdxGame.WIDTH, MyGdxGame.HEIGHT, 1.5f,
                                Interpolation.swingIn),
                                Actions.rotateBy(3600f, 1.5f)),
                        Actions.removeActor()
                ))
            }
        })
        addAction(flap)

    }
}