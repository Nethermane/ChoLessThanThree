package com.nishimura.cholessthanthree

import com.badlogic.gdx.scenes.scene2d.Actor

interface Targetable {
    fun hit(x: Float, y:Float): Boolean
}