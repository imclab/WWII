package com.glevel.wwii.game.graphics;

import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.sprite.vbo.ISpriteVertexBufferObject;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class CenteredSprite extends Sprite {

    public CenteredSprite(float pX, float pY, float pWidth, float pHeight, ITextureRegion pTextureRegion,
            ISpriteVertexBufferObject pSpriteVertexBufferObject) {
        super(pX, pY, pWidth, pHeight, pTextureRegion, pSpriteVertexBufferObject);
    }

    public CenteredSprite(float pX, float pY, ITextureRegion pTextureRegion,
            VertexBufferObjectManager pVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, pVertexBufferObjectManager);
    }

    @Override
    public float getX() {
        return super.getX() + this.getWidth() / 2.0f;
    }

    @Override
    public float getY() {
        return super.getY() + this.getHeight() / 2.0f;
    }

    @Override
    public void setPosition(float pX, float pY) {
        super.setPosition(pX - this.getWidth() / 2.0f, pY - this.getHeight() / 2.0f);
    }

}
