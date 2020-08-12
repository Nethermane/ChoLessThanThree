package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.Damageable
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.Targetable

abstract class Enemy: Image(), Targetable, Damageable {
    override fun hit(x: Float, y: Float): Boolean {
        return  (x >= this.x && x < width+this.x && y >= this.y && y < height+this.y)
    }

    override fun getTargetX(): Float {
        return x
    }

    override fun getTargetY(): Float {
        return y
    }

}