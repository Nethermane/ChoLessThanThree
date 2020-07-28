package com.nishimura.cholessthanthree


import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter
import ktx.assets.getValue
import ktx.assets.load
import ktx.freetype.registerFreeTypeFontLoaders


object Assets {
    const val imagesPack = "output/atlas_name.atlas"
    private const val mainFontString = "fonts/stickman.ttf"
    private const val smallFont = "stickman_small.ttf"
    private const val bigFont = "stickman_big.ttf"
    const val backgroundString = "test"
    const val cardString = "cardTemp"
    val assetManager: AssetManager = AssetManager().also {  it.registerFreeTypeFontLoaders()}
    val atlas: TextureAtlas by assetManager.load(imagesPack)
    val background by lazy {
        assetManager.finishLoadingAsset<TextureAtlas>(imagesPack)
        atlas.findRegion(backgroundString)
    }
    val card by lazy {
        assetManager.finishLoadingAsset<TextureAtlas>(imagesPack)
        atlas.findRegion(cardString)
    }
//    val font: BitmapFont by assetManager.loadFreeTypeFont(mainFontString) {
//        this.color = Color.BLACK
//        this.size = 100
//    }
//    val healthFont: BitmapFont by assetManager.loadFreeTypeFont(mainFontString) {
//        this.color = Color.RED
//        this.size = 20
//    }
    val font: BitmapFont by lazy {
        val robotoFontBigParam = FreeTypeFontLoaderParameter()
        robotoFontBigParam.fontFileName = mainFontString
        robotoFontBigParam.fontParameters.size = 100
        assetManager.load<BitmapFont>(bigFont, robotoFontBigParam)
        assetManager.finishLoadingAsset<BitmapFont>(bigFont)
        assetManager.get<BitmapFont>(bigFont)
    }
    val healthFont: BitmapFont by lazy {
        val robotoFontBigParam = FreeTypeFontLoaderParameter()

        robotoFontBigParam.fontFileName = mainFontString
        robotoFontBigParam.fontParameters.size = 40
        assetManager.load<BitmapFont>(smallFont, robotoFontBigParam)
        assetManager.finishLoadingAsset<BitmapFont>(smallFont)
        assetManager.get<BitmapFont>(smallFont)
    }


    init {
        val resolver: FileHandleResolver = InternalFileHandleResolver()
        assetManager.setLoader(FreeTypeFontGenerator::class.java, FreeTypeFontGeneratorLoader(resolver))
        assetManager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))

    }
}