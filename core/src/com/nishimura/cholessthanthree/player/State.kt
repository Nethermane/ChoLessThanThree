package com.nishimura.cholessthanthree.player

import com.badlogic.gdx.graphics.g2d.Animation
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.nishimura.cholessthanthree.Assets

enum class State(val animation: Animation<TextureRegion>) {
    Running(Animation<TextureRegion>(
            (1f / 48f),
            Assets.atlas.findRegions("run/run"),
            Animation.PlayMode.LOOP)),
    Dancing(Animation<TextureRegion>(
            (1f / 24f), Assets.atlas.findRegions(
            "dance/dance"),
            Animation.PlayMode.LOOP)),
    Punching(
            Animation<TextureRegion>(
                    (1f / 24f),
                    Assets.atlas.findRegions(
                            "rightPunch/rightPunch"),
                    Animation.PlayMode.LOOP)),
    Block(
            Animation<TextureRegion>(
                    (1f / 24f),
                    Assets.atlas.findRegions(
                            "enter_block/enter_block"),
                    Animation.PlayMode.LOOP));

    open operator fun next(): State {
        // No bounds checking required here, because the last instance overrides
        return values()[(ordinal + 1) % values().size]
    }
}