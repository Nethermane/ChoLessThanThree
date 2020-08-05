package com.nishimura.cholessthanthree.actors

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable
import com.badlogic.gdx.utils.Align
import com.nishimura.cholessthanthree.Assets
import com.nishimura.cholessthanthree.MyGdxGame
import com.nishimura.cholessthanthree.PlayerState

object EndTurnButton : Label("End\nTurn", Label.LabelStyle(Assets.healthFont, Color.RED).also {
    it.background = Assets.atlasSkin.getDrawable(Assets.blackCircleString)
}) {
    val glyph = GlyphLayout(Assets.healthFont, "End\nTurn")

    init {
        setSize(glyph.height * 2, glyph.height * 2)
        setAlignment(Align.center)
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent?, x: Float, y: Float) {
                super.clicked(event, x, y)
                if(!Hand.isDrawing && !Hand.isDiscarding)
                    PlayerState.turnNumber++
            }
        })
        setPosition(MyGdxGame.WIDTH-width*1.5f, DiscardButton.y + DiscardButton.height)
    }

}