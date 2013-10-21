package com.glevel.wwii.game.graphics;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Crosshair extends Sprite {

    private static final float ROTATION_SPEED = 0.2f;
    private static final float SCALE_ANIMATION_SPEED = 0.005f;
    private static final float SCALE_ANIMATION_LIMIT = 0.1f;
    private static final float INITIAL_SCALE = 0.9f;

    private boolean mIsGrowing = true;

    public Crosshair(final TextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(0, 0, pTextureRegion, pVertexBufferObjectManager);
        setScale(INITIAL_SCALE);
    }

    @Override
    protected void onManagedUpdate(final float pSecondsElapsed) {
        this.setRotation(getRotation() + ROTATION_SPEED);
        this.setScale(getScaleX() + SCALE_ANIMATION_SPEED * (mIsGrowing ? 1 : -1));
        if (Math.abs(getScaleX() - INITIAL_SCALE) > SCALE_ANIMATION_LIMIT) {
            mIsGrowing = !mIsGrowing;
        }
        super.onManagedUpdate(pSecondsElapsed);
    }
}
