package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.nishimura.cholessthanthree.MyGdxGame


object CardGroupDisplay : Group() {
    private val tableOfCards = Table().apply {
    }
    val cardDisplay = ScrollPane(tableOfCards).apply {
        setFillParent(true)
        val pixmap = Pixmap(1, 1, Pixmap.Format.RGBA8888)
        pixmap.setColor(Color(0f, 0f, 0f, 0.5f))
        pixmap.fillRectangle(0, 0, 1, 1)
        style = ScrollPane.ScrollPaneStyle(TextureRegionDrawable(Texture(pixmap)), null, null, null,
                null)
        pixmap.dispose()
    }

    init {
        setSize(MyGdxGame.WIDTH, MyGdxGame.HEIGHT)
        addActor(cardDisplay)

    }

    override fun draw(batch: Batch?, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
    }

    fun setCards(cards: List<Card>) {
        tableOfCards.reset()
        tableOfCards.debugAll()
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