package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.utils.Align
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.PlayerState
import com.nishimura.cholessthanthree.PlayerState.handSize
import kotlin.math.PI
import kotlin.math.abs

class Hand {
    val cards = ArrayList<Card>()
    val width = MyGdxGame.WIDTH * 0.9f
    val height = (MyGdxGame.WIDTH * 0.9f / handSize)
    var anyDown = false

    fun setCards(newCards: ArrayList<Card>) {
        cards.clear()
        cards.addAll(newCards)
        for ((index,card) in cards.withIndex()) {
            val absIndexFromMiddle = abs(cards.size/2 - index)
            card.setOrigin(card.width/2f,card.height/2f)
            val startingRotation = ((180f/16f*(-(index-cards.size/2))))
            card.rotation = startingRotation
            card.zIndex = 10+index

//            add(card).width((MyGdxGame.WIDTH * 0.9f / PlayerState.handSize))
//                    .height(MyGdxGame.WIDTH * 0.9f / handSize * 2).padLeft(-5f).padRight(-5f)
            card.setPosition(MyGdxGame.WIDTH * 0.3f + index.toFloat()/cards.size * MyGdxGame.WIDTH * 0.4f , -absIndexFromMiddle*height/ cards.size)
            val startingX = card.x
            val startingY = card.y
            card.addListener(object: DragListener() {
                override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                   fromActor: Actor?) {
                    if(!anyDown) {
                        card.addAction(
                                Actions.parallel(Actions.scaleTo(1.5f, 1.5f),
                                        Actions.moveTo(startingX, startingY + MyGdxGame.WIDTH * 0.05f),
                                        Actions.rotateTo(0f)))
                        card.zIndex += 1
                    }
                }

                override fun exit(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                  toActor: Actor?) {
                    if(!anyDown) {
                        card.addAction(Actions.parallel(Actions.scaleTo(1f, 1f),
                                Actions.moveTo(startingX, startingY),
                                Actions.rotateTo(startingRotation)))
                        card.zIndex -= 1
                    }
                }
                override fun touchUp(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                     button: Int) {
                    if(card.isDown) {
                        Gdx.input.isCursorCatched = false
                        val releasePosition = card.localToScreenCoordinates(
                                Vector2(x + card.width / 2, y + card.width / 2))
                        Gdx.input.setCursorPosition(releasePosition.x.toInt(),
                                releasePosition.y.toInt())
                        card.setPosition(startingX, startingY)
                        card.addAction(Actions.parallel(Actions.scaleTo(1f, 1f),
                                Actions.moveTo(startingX, startingY),
                                Actions.rotateTo(startingRotation)))
                        resetCards()
                    }
                    anyDown = false
                    super.touchUp(event, x, y, pointer, button)
                }

                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                       button: Int): Boolean {
                    Gdx.input.isCursorCatched = true
                    anyDown = true
                    card.isDown = true
                    card.toFront()

                    card.addAction(Actions.moveTo(event!!.stageX,event.stageY))
                    return super.touchDown(event, x, y, pointer, button)
                }
                override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                    card.addAction(Actions.moveBy(x,y))
                    super.touchDragged(event, x, y, pointer)
                }
            })
        }
    }
    fun resetCards() {
        for(card in cards) {
            card.toFront()
        }
    }
}