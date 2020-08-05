package com.nishimura.cholessthanthree

import com.badlogic.gdx.scenes.scene2d.Actor

/**
 * Returns whether an xy pair IN THE PROJECTED SPACE is inside the actors bounding box
 */
fun Actor.hit(x: Float, y:Float): Boolean {
    return  (x >= this.x && x < width+this.x && y >= this.y && y < height+this.y)

}