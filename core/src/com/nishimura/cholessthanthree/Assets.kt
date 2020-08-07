package com.nishimura.cholessthanthree


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.assets.load
import ktx.freetype.registerFreeTypeFontLoaders


object Assets {
    const val imagesPack = "output/atlas_name.atlas"
    private const val mainFontString = "fonts/stickman.ttf"
    private const val timesFontString = "fonts/times_400.ttf"

    private const val smallFont = "stickman_small.ttf"
    private const val bigFont = "stickman_big.ttf"
    private const val defaultFontInternalName = "times_400.ttf"
    private const val manaFontInternal = "mana_font_times.ttf"


    const val backgroundString = "paper"
    const val cardString = "cardBack"
    const val cursorString = "color_cursor"
    const val cardFrontString = "cardTemp"
    const val drawPileBackground = "drawPileLabelBackground"
    const val backleftLabelString = "back_label_left"
    const val blackCircleString = "blackCircle"
    const val character = "character"
    const val bird = "bird"
    val assetManager: AssetManager = AssetManager().also { it.registerFreeTypeFontLoaders() }.also {
        it.load<TextureAtlas>(imagesPack)
    }
    val atlas: TextureAtlas = assetManager.finishLoadingAsset(imagesPack)
    val atlasSkin = Skin(Gdx.files.internal("raw/skin.json"),atlas)
    val targetCircle by lazy {
        assetManager.finishLoadingAsset<TextureAtlas>(imagesPack)
        extractPixmapFromTextureRegion(atlas.findRegion(cursorString))
    }
    val font: BitmapFont by lazy {
        val robotoFontBigParam = FreeTypeFontLoaderParameter()
        robotoFontBigParam.fontFileName = mainFontString
        robotoFontBigParam.fontParameters.size = 128
        robotoFontBigParam.fontParameters.magFilter = Texture.TextureFilter.Linear
        robotoFontBigParam.fontParameters.minFilter = Texture.TextureFilter.Linear
        assetManager.load<BitmapFont>(bigFont, robotoFontBigParam)
        assetManager.finishLoadingAsset<BitmapFont>(bigFont)
        assetManager.get<BitmapFont>(bigFont)
    }
    val healthFont: BitmapFont by lazy {
        val robotoFontBigParam = FreeTypeFontLoaderParameter()
        robotoFontBigParam.fontFileName = mainFontString
        robotoFontBigParam.fontParameters.size = 40
        robotoFontBigParam.fontParameters.magFilter = Texture.TextureFilter.Linear
        robotoFontBigParam.fontParameters.minFilter = Texture.TextureFilter.Linear
        assetManager.load<BitmapFont>(smallFont, robotoFontBigParam)
        assetManager.finishLoadingAsset<BitmapFont>(smallFont)
        assetManager.get<BitmapFont>(smallFont)
    }
    val defaultFont: BitmapFont by lazy {
        val robotoFontBigParam = FreeTypeFontLoaderParameter()
        robotoFontBigParam.fontFileName = timesFontString
        robotoFontBigParam.fontParameters.size = 12
        robotoFontBigParam.fontParameters.magFilter = Texture.TextureFilter.Linear
        robotoFontBigParam.fontParameters.minFilter = Texture.TextureFilter.Linear
        assetManager.load<BitmapFont>(defaultFontInternalName, robotoFontBigParam)
        assetManager.finishLoadingAsset<BitmapFont>(defaultFontInternalName)
        assetManager.get<BitmapFont>(defaultFontInternalName)
    }
    val manaFont: BitmapFont by lazy {
        val robotoFontBigParam = FreeTypeFontLoaderParameter()
        robotoFontBigParam.fontFileName = timesFontString
        robotoFontBigParam.fontParameters.size = 30
        robotoFontBigParam.fontParameters.magFilter = Texture.TextureFilter.Linear
        robotoFontBigParam.fontParameters.minFilter = Texture.TextureFilter.Linear
        assetManager.load<BitmapFont>(manaFontInternal, robotoFontBigParam)
        assetManager.finishLoadingAsset<BitmapFont>(manaFontInternal)
        assetManager.get<BitmapFont>(manaFontInternal)
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
        return pixmap
    }
    val combinedCard by lazy {
        val cardBack = atlasSkin.getRegion("cardBack")
        val manaIcon = atlasSkin.getRegion("mana_icon")

        manaIcon.texture.textureData.prepare()
        val manaPixmap = manaIcon.texture.textureData.consumePixmap()
        cardBack.texture.textureData.prepare() // The api-doc says this is needed

        val cardbackPixmap = cardBack.texture.textureData.consumePixmap() // Strange name, but gives the pixmap of the texture. Remember to dispose this also
        val manaIconDimensions = cardBack.regionWidth/3
        val combinedPixmap = Pixmap(cardBack.regionWidth, (cardBack.regionHeight+manaIconDimensions/2),
                Pixmap.Format.RGBA8888) // Remember to dispose

// We want the center point coordinates of the image region as the circle origo is at the center and drawn by the radius
        combinedPixmap.drawPixmap(cardbackPixmap,0,manaIconDimensions/2, cardBack.regionX, cardBack.regionY,cardBack.regionWidth,cardBack.regionHeight)
        combinedPixmap.drawPixmap(manaPixmap,manaIcon.regionX,manaIcon.regionY,manaIcon.regionWidth,manaIcon.regionHeight,cardBack.regionWidth/2-manaIconDimensions/2,0,manaIconDimensions,manaIconDimensions)
// TADA! New combined texture
// TADA! New combined texture
        val tex = Texture(combinedPixmap)
// These are not needed anymore
        combinedPixmap.dispose()
        cardbackPixmap.dispose()
        manaPixmap.dispose()
        tex
    }

}