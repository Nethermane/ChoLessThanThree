package com.nishimura.cholessthanthree


import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
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
    const val backgroundString = "paper"
    const val cardString = "cardBack"
    const val cursorString = "color_cursor"
    const val cardFrontString = "cardTemp"
    const val drawPileBackground = "drawPileLabelBackground"
    const val backleftLabelString = "back_label_left"
    const val blackCircleString = "blackCircle"

    val assetManager: AssetManager = AssetManager().also { it.registerFreeTypeFontLoaders() }
    val atlas: TextureAtlas by assetManager.load(imagesPack)
    val background by lazy {
        assetManager.finishLoadingAsset<TextureAtlas>(imagesPack)
        atlas.findRegion(backgroundString)
    }
    val card by lazy {
        assetManager.finishLoadingAsset<TextureAtlas>(imagesPack)
        atlas.findRegion(cardString)
    }
    val cardFront by lazy {
        assetManager.finishLoadingAsset<TextureAtlas>(imagesPack)
        atlas.findRegion(cardFrontString)
    }
    val cardLabelBackground by lazy {
        assetManager.finishLoadingAsset<TextureAtlas>(imagesPack)
        atlas.findRegion(drawPileBackground)
    }
    val backLeftLabel by lazy {
        assetManager.finishLoadingAsset<TextureAtlas>(imagesPack)
        atlas.findRegion(backleftLabelString)
    }
    val targetCircle by lazy {
        assetManager.finishLoadingAsset<TextureAtlas>(imagesPack)
        extractPixmapFromTextureRegion(atlas.findRegion(cursorString))
    }
    val blackCircle by lazy {
        assetManager.finishLoadingAsset<TextureAtlas>(imagesPack)
        atlas.findRegion(blackCircleString)
    }
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
        assetManager.setLoader(FreeTypeFontGenerator::class.java,
                FreeTypeFontGeneratorLoader(resolver))
        assetManager.setLoader(BitmapFont::class.java, ".ttf", FreetypeFontLoader(resolver))

    }

    fun extractPixmapFromTextureRegion(textureRegion: TextureRegion): Pixmap {
        val textureData = textureRegion.texture.textureData
        if (!textureData.isPrepared) {
            textureData.prepare()
        }
        val pixmap = Pixmap(
                (MyGdxGame.WIDTH / 8).toInt(),
                (MyGdxGame.WIDTH / 8).toInt(),
                textureData.format
        )
        /**
         * @param pixmap The other Pixmap
         * @param srcx The source x-coordinate (top left corner)
         * @param srcy The source y-coordinate (top left corner);
         * @param srcWidth The width of the area from the other Pixmap in pixels
         * @param srcHeight The height of the area from the other Pixmap in pixels
         * @param dstx The target x-coordinate (top left corner)
         * @param dsty The target y-coordinate (top left corner)
         * @param dstWidth The target width
         * @param dstHeight the target height */
        pixmap.drawPixmap(
                textureData.consumePixmap(),
                textureRegion.regionX,
                textureRegion.regionY,
                textureRegion.regionWidth,
                textureRegion.regionHeight,
                 0,
                 0,
                 (MyGdxGame.WIDTH / 8).toInt(),
                 (MyGdxGame.WIDTH / 8).toInt()
        )
        println(pixmap.format.name)
        return pixmap
    }

}