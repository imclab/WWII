package com.glevel.wwii.game;

import java.util.HashMap;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;

import com.glevel.wwii.game.model.Battle;
import com.glevel.wwii.game.model.GameElement;
import com.glevel.wwii.game.model.GameSprite;
import com.glevel.wwii.game.model.Player;

public class GraphicElementFactory {

    private Context mContext;
    private VertexBufferObjectManager mVertexBufferObjectManager;
    private TextureManager mTextureManager;

    private BitmapTextureAtlas mTexture;
    public HashMap<String, TextureRegion> mGfxMap = new HashMap<String, TextureRegion>();

    public GraphicElementFactory(Context context, VertexBufferObjectManager vertexBufferObjectManager,
            TextureManager textureManager) {
        mContext = context;
        mVertexBufferObjectManager = vertexBufferObjectManager;
        mTextureManager = textureManager;
    }

    public void initGraphics(Battle battle) {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        // load all game elements graphics
        for (Player player : battle.getPlayers()) {
            for (GameElement gameElement : player.getUnits()) {
                loadGfx(128, 128, gameElement.getSpriteName());
            }
        }

        // selection circle
        loadGfx(128, 128, "selection.png");
    }

    private void loadGfx(int textureWidth, int textureHeight, String imageName) {
        if (mGfxMap.get(imageName) == null) {
            mTexture = new BitmapTextureAtlas(mTextureManager, textureWidth, textureHeight, TextureOptions.DEFAULT);
            TextureRegion textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mTexture, mContext,
                    imageName, 0, 0);
            mTexture.load();
            mGfxMap.put(imageName, textureRegion);
        }
    }

    public void addGameElement(Scene scene, GameElement gameElement, InputManager inputManager) {
        // create sprite
        final Sprite sprite = new GameSprite(gameElement, inputManager, gameElement.getTilePosition().getXPosition(),
                gameElement.getTilePosition().getYPosition(), mGfxMap.get(gameElement.getSpriteName()),
                mVertexBufferObjectManager);
        gameElement.setSprite(sprite);
        scene.attachChild(sprite);
        scene.registerTouchArea(sprite);
    }

    public Sprite createSprite(float x, float y, String spriteName) {
        return new Sprite(x, y, mGfxMap.get(spriteName), mVertexBufferObjectManager);
    }

}
