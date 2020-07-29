package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Cursor
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.DragListener
import com.badlogic.gdx.utils.Align
import com.nishimura.cholessthanthree.Assets
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
    var isTargetting = false
    private val targettingCursor = Gdx.graphics.newCursor(Assets.targetCircle,
            Assets.targetCircle.width/2,
            Assets.targetCircle.width/2)
    fun setCards(newCards: ArrayList<Card>) {
        cards.clear()
        cards.addAll(newCards)
        for ((index,card) in cards.withIndex()) {
            val absIndexFromMiddle = abs(cards.size/2 - index)
            val startingRotation = ((180f/16f*(-(index-cards.size/2))))
            card.rotation = startingRotation
            card.zIndex = 10+index
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
//                        Gdx.input.isCursorCatched = false
                        card.setPosition(startingX, startingY)
                        card.addAction(Actions.parallel(Actions.scaleTo(1f, 1f),
                                Actions.moveTo(startingX, startingY),
                                Actions.rotateTo(startingRotation)))
                        resetCards()
                        if(isTargetting) {
                            isTargetting = false
                            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow)
                        }
                    }
                    anyDown = false
                    super.touchUp(event, x, y, pointer, button)
                }

                override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int,
                                       button: Int): Boolean {
//                    Gdx.input.isCursorCatched = true
                    anyDown = true
                    card.isDown = true
                    card.toFront()
                    card.moveTo(event!!.stageX,event.stageY)
                    return super.touchDown(event, x, y, pointer, button)
                }
                override fun touchDragged(event: InputEvent?, x: Float, y: Float, pointer: Int) {
                    if(anyDown && event!!.stageY > MyGdxGame.HEIGHT*0.3f && !isTargetting) {
                        card.moveTo(event.stageX,event.stageY)
                        isTargetting = true
//                        Gdx.input.isCursorCatched = false
                        Gdx.graphics.setCursor(targettingCursor)
//                        Gdx.input.setCursorPosition(event.stageX.toInt(), event.stageY.toInt())
                    } else if(anyDown && event!!.stageY <= MyGdxGame.HEIGHT*0.3f && isTargetting) {
                        isTargetting = false
                        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow)
                    }
                    if(!isTargetting)
                        card.moveTo(event!!.stageX,event.stageY)
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