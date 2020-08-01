package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Container
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame


object CardGroupDisplay : Group() {

    private val backButton = Label("Back", Label.LabelStyle(Assets.healthFont, Color.BLACK).apply{
        background = Assets.atlasSkin.getDrawable(Assets.backleftLabelString)
    }).apply {
        addListener(  object: ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                this@CardGroupDisplay.addAction(Actions.removeActor())
            }
        })
        setAlignment(Align.center)
        setFillParent(true)
    }
    private val backButtonTable = Table().apply {
        width = Card.cardGroupDisplayWidth/2 *1.5f
        height = backButton.prefHeight
        add(backButton).width(Card.cardGroupDisplayWidth/2*1.5f).fill().expand().left()
        y = MyGdxGame.HEIGHT/3
    }


    private val tableOfCards = Table()
    val cardDisplay = ScrollPane(tableOfCards).apply {
        setFillParent(true)
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color(0f, 0f, 0f, 0.5f))
        pixmap.fillRectangle(0, 0, 1, 1)
        style = ScrollPane.ScrollPaneStyle(TextureRegionDrawable(Texture(pixmap)), null, null, null,
                null)
        pixmap.dispose()
        setScrollingDisabled(true,false)
    }

    init {
        setSize(MyGdxGame.WIDTH, MyGdxGame.HEIGHT)
        addActor(cardDisplay)
        addActor(backButtonTable)


    }

    fun setCards(cards: List<Card>) {
        tableOfCards.clear()
        val cardsInARow = 5
        for ((index, card) in cards.withIndex()) {
            val clonedNewCard = card.toDisplayCard()
            val cell = tableOfCards.add(clonedNewCard)
                    .padTop(Card.cardGroupDisplayWidth / 2)
                    .width(clonedNewCard.width)
                    .height(clonedNewCard.height)
                    .expandX()
            if (index > 0 && index % (cardsInARow - 1) == 0) {
                cell.padRight(Card.cardGroupDisplayWidth)
                tableOfCards.row()
            } else if (index % cardsInARow == 0) {
                cell.padLeft(Card.cardGroupDisplayWidth)
            }
        }
        tableOfCards.width = MyGdxGame.WIDTH
        //tableOfCards.setPosition(0f, tableOfCards.minHeight/2f)
    }

}