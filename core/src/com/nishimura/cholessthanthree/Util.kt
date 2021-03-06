package com.nishimura.cholessthanthree

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Group

fun Vector2.toInsideStagePosition(): Vector2 {
    val newVector2 = Vector2(this)
    if(this.x < 0)
        newVector2.x = 0f
    else if (this.x > MyGdxGame.WIDTH) {
        newVector2.x = MyGdxGame.WIDTH
    }
    if(this.y < 0)
        newVector2.y = 0f
    else if (this.y > MyGdxGame.HEIGHT) {
        newVector2.y = MyGdxGame.HEIGHT
    }
    return newVector2
}

inline fun <T> Iterable<T>.sumByFloat(selector: (T) -> Float): Float {
    var sum = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
fun Float.toDegree(): Float = (this * 180f / Math.PI).toFloat()