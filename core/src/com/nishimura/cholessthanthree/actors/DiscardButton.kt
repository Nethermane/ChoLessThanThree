package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.PlayerState
import com.nishimura.cholessthanthree.data.Card


//TODO: Make the asset a cool S
object DiscardButton: Group() {
    val background = Image(Assets.atlasSkin.getDrawable("cardBack"))
    val label = Label(
            PlayerState.discardPile.size.toString(), Label.LabelStyle(Assets.healthFont, Color.RED).also{it.background = Assets.atlasSkin.getDrawable(Assets.drawPileBackground)
    })
    val discardPileListener =  {old:List<Card>, new:List<Card> -> label.setText(new.size.toString())}
    init {
        setSize(CardView.cardWidth/2,CardView.cardHeight/2)
        background.setSize(width,height)
        label.setSize(width,height/2)
        label.setPosition(width-label.width,0f)
        label.setAlignment(Align.center)
        addActor(background)
        addActor(label)
        PlayerState.discardPileListeners.add(discardPileListener)
        addListener( object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                CardGroupDisplay.setCards(PlayerState.discardPile)
                stage.addActor(CardGroupDisplay)

            }
        })
        setPosition(MyGdxGame.WIDTH*0.9f-width, height/2)
    }

}