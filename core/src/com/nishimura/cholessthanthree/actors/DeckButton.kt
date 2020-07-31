package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.PlayerState
import com.nishimura.cholessthanthree.PlayerState.deck
import com.nishimura.cholessthanthree.PlayerState.drawPile

//TODO: Make the asset a cool S
object DeckButton: Group() {
    val background = Image(Assets.card)
    val label = Label(drawPile.size.toString(), Label.LabelStyle(Assets.healthFont, Color.RED).also{it.background = TextureRegionDrawable(Assets.cardLabelBackground)})
    val drawPileListener =  {old:List<Card>, new:List<Card> -> label.setText(new.size.toString())}
    init {
        setSize(Card.cardWidth/2,Card.cardHeight/2)
        background.setSize(width,height)
        label.setSize(width,height/2)
        label.setPosition(width-label.width,0f)
        label.setAlignment(Align.center)
        addActor(background)
        addActor(label)
        PlayerState.drawPileListeners.add(drawPileListener)
        addListener( object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                CardGroupDisplay.setCards(drawPile)
                stage.addActor(CardGroupDisplay)

            }
        })
        setPosition(MyGdxGame.WIDTH*0.1f,height/2)
    }

}