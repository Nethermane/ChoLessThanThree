package com.nishimura.cholessthanthree


import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import ktx.assets.*
import ktx.freetype.*

object Assets {
    const val imagesPack = "output/atlas_name.atlas"
    const val mainFontString = "fonts/stickman.ttf"
    const val backgroundString = "test"
    val assetManager: AssetManager = AssetManager().also {  it.registerFreeTypeFontLoaders()}
    val atlas: TextureAtlas by assetManager.load(imagesPack)
    val background by lazy {
        assetManager.finishLoadingAsset<TextureAtlas>(imagesPack)
        atlas.findRegion(backgroundString)
    }
    val font: BitmapFont by assetManager.loadFreeTypeFont(mainFontString) {
        this.color = Color.BLACK
        this.size = 100
    }

    init {
        assetManager.finishLoadingAsset<BitmapFont>(mainFontString)
    }
}