package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.utils.Align
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.PlayerState.handSize
import com.nishimura.cholessthanthree.Targetable
import kotlin.math.max
import kotlin.math.min


data class Card(private var cost: Int,
                private var onPlay: Effect? = null,
                private var onDraw: Effect? = null,
                private var onDiscard: Effect? = null,
                private var targets: List<Class<Targetable>> = emptyList()
): Targetable, Image(Assets.card) {
    var isDown = false
    init {
        setSize((MyGdxGame.WIDTH * 0.9f / handSize),(MyGdxGame.WIDTH * 0.9f / handSize * 2))
        setOrigin(width/2f,height/2f)

    }

    //When this card is brought to the hand
    fun onDraw(target: Targetable?) {
        onDraw?.executeEffect(target)
    }

    //When this card is put into the discard
    fun onDiscard(target: Targetable?) {
        onDiscard?.executeEffect(target)
    }

    //When this card is played
    fun onPlay(target: Targetable?) {
        if(targets.any { clazz -> clazz.isInstance(target)} || targets.isEmpty()) {
            onPlay?.executeEffect(target)
        }
    }

    abstract class Effect {
        open fun executeEffect(target: Targetable?) {}
    }

    class OffensiveEffect() : Effect()
    class DefensiveEffect() : Effect()
    class UtilityEffect() : Effect()

    fun moveTo(x: Float, y: Float) {
        val minX = width*scaleX/2
        val maxX: Float = MyGdxGame.WIDTH - (width*scaleX)/2
        val minY = height*scaleY/2
        val maxY: Float = MyGdxGame.HEIGHT - (height*scaleY)/2
        val newX = min(maxX, max(x, minX))
        val newY = min(maxY, max(y, minY))
        println("height: $height width: $width x:$newX  y: $newY mouseX: $x mouseY: $y")
        setPosition(newX, newY, Align.center)
    }
}
