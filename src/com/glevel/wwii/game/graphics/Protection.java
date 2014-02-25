package com.glevel.wwii.game.graphics;

import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class Protection extends CenteredSprite {

    private static final float SKEW_ANIMATION_SPEED = 0.3f;
    private static final float SKEW_ANIMATION_LIMIT = 5.0f;
    private static final float INITIAL_SCALE = 1.7f;

    private boolean mIsFromLeft = true;

    public Protection(final TextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager) {
        super(0, 0, pTextureRegion, pVertexBufferObjectManager);
        this.setColor(0.2f, 0.2f, 0.7f, 0.7f);
        this.setScale(INITIAL_SCALE);
    }

    @Override
    protected void onManagedUpdate(final float pSecondsElapsed) {
        this.setSkewY(getSkewY() + SKEW_ANIMATION_SPEED * (mIsFromLeft ? 1 : -1));
        if (Math.abs(getSkewY() - 1.0f) > SKEW_ANIMATION_LIMIT) {
            mIsFromLeft = !mIsFromLeft;
        }
        super.onManagedUpdate(pSecondsElapsed);
    }

}
