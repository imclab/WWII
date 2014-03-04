package com.glevel.wwii.game;

import java.util.HashMap;

import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.content.Context;

import com.glevel.wwii.game.models.Battle;
import com.glevel.wwii.game.models.GameElement;
import com.glevel.wwii.game.models.Player;
import com.glevel.wwii.game.models.units.Tank;

public class GraphicsFactory {

    private Context mContext;
    private TextureManager mTextureManager;
    private BitmapTextureAtlas mTexture;

    public static HashMap<String, TextureRegion> mGfxMap = new HashMap<String, TextureRegion>();
    public static HashMap<String, TiledTextureRegion> mTiledGfxMap = new HashMap<String, TiledTextureRegion>();

    public GraphicsFactory(Context context, VertexBufferObjectManager vertexBufferObjectManager,
            TextureManager textureManager) {
        mContext = context;
        mTextureManager = textureManager;
    }

    public void initGraphics(Battle battle) {
        mGfxMap = new HashMap<String, TextureRegion>();
        mTiledGfxMap = new HashMap<String, TiledTextureRegion>();

        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        // load all game elements graphics
        for (Player player : battle.getPlayers()) {
            for (GameElement gameElement : player.getUnits()) {
                loadGfxFromAssets(164, 164, gameElement.getSpriteName());
                if (gameElement instanceof Tank) {
                    loadGfxFromAssets(164, 164, gameElement.getSpriteName().replace(".png", "") + "_turret.png");
                }
            }
        }

        // stuff to load
        loadGfxFromAssets(128, 128, "selection.png");
        loadGfxFromAssets(128, 128, "crosshair.png");
        loadGfxFromAssets(64, 64, "muzzle_flash.png");
        loadGfxFromAssets(128, 128, "protection.png");
        loadTiledTextureGfxFromAssets(256, 256, 4, 4, "explosion.png");
        loadTiledTextureGfxFromAssets(512, 256, 4, 2, "smoke.png");
        loadTiledTextureGfxFromAssets(512, 256, 4, 2, "tank_move_smoke.png");
        loadTiledTextureGfxFromAssets(312, 50, 6, 1, "blood.png");
    }

    private void loadGfxFromAssets(int textureWidth, int textureHeight, String imageName) {
        if (mGfxMap.get(imageName) == null) {
            mTexture = new BitmapTextureAtlas(mTextureManager, textureWidth, textureHeight, TextureOptions.DEFAULT);
            TextureRegion textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mTexture, mContext,
                    imageName, 0, 0);
            mTexture.load();
            mGfxMap.put(imageName, textureRegion);
        }
    }

    private void loadTiledTextureGfxFromAssets(int textureWidth, int textureHeight, int x, int y, String spriteName) {
        if (mGfxMap.get(spriteName) == null) {
            mTexture = new BitmapTextureAtlas(mTextureManager, textureWidth, textureHeight, TextureOptions.DEFAULT);
            TiledTextureRegion tiledTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mTexture,
                    mContext.getAssets(), spriteName, 0, 0, x, y);
            mTexture.load();
            mTiledGfxMap.put(spriteName, tiledTexture);
        }
    }

    public void onPause() {
        mGfxMap = new HashMap<String, TextureRegion>();
        mTiledGfxMap = new HashMap<String, TiledTextureRegion>();
    }

}
