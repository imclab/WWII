package com.glevel.wwii.activities;

import org.andengine.audio.sound.Sound;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSCounter;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.debug.Debug;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.glevel.wwii.R;

public class GameActivity extends SimpleBaseGameActivity implements IOnSceneTouchListener, IScrollDetectorListener,
        IPinchZoomDetectorListener {

    private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;
    private static final float CAMERA_ZOOM_MAX = 2.0f;
    private static final float CAMERA_ZOOM_MIN = 0.5f;

    private Scene mScene;
    private ZoomCamera mCamera;
    private SurfaceScrollDetector mScrollDetector;
    private PinchZoomDetector mPinchZoomDetector;
    private float mPinchZoomStartedCameraZoomFactor;

    private Dialog mLoadingScreen;

    private TMXTiledMap mTMXTiledMap;

    @Override
    public EngineOptions onCreateEngineOptions() {
        this.mCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, CAMERA_ZOOM_MIN, CAMERA_ZOOM_MAX);
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
                new FillResolutionPolicy(), mCamera);
        return engineOptions;
    }

    @Override
    protected void onCreateResources() {
        loadGraphics();
        loadSounds();
        this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
        this.mFont.load();
        mLoadingScreen.dismiss();
    }

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);

        // setup loading screen
        mLoadingScreen = new Dialog(this, R.style.FullScreenDialog);
        mLoadingScreen.setContentView(R.layout.dialog_game_loading);
        mLoadingScreen.setCancelable(false);
        mLoadingScreen.setCanceledOnTouchOutside(false);
        // add random quote
        // TextView quote = (TextView) mLoadingScreen.findViewById(R.id.quote);
        // quote.setText(R.string.about);
        // animate loading dots
        Animation loadingDotsAnimation = AnimationUtils.loadAnimation(this, R.anim.loading_dots);
        ((TextView) mLoadingScreen.findViewById(R.id.loadingDots)).startAnimation(loadingDotsAnimation);
        mLoadingScreen.show();
    }

    private Font mFont;
    private Text fpsText;

    @Override
    protected Scene onCreateScene() {

        mScene = new Scene();
        mScene.setOnAreaTouchTraversalFrontToBack();

        mScene.setBackground(new Background(0, 0, 0));

        this.mScrollDetector = new SurfaceScrollDetector(this);
        this.mPinchZoomDetector = new PinchZoomDetector(this);

        this.mScene.setOnSceneTouchListener(this);
        this.mScene.setTouchAreaBindingOnActionDownEnabled(true);

        try {
            final TMXLoader tmxLoader = new TMXLoader(this.getAssets(), this.mEngine.getTextureManager(),
                    TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getVertexBufferObjectManager(),
                    new ITMXTilePropertiesListener() {
                        @Override
                        public void onTMXTileWithPropertiesCreated(final TMXTiledMap pTMXTiledMap,
                                final TMXLayer pTMXLayer, final TMXTile pTMXTile,
                                final TMXProperties<TMXTileProperty> pTMXTileProperties) {
                        }
                    });
            this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/desert.tmx");

        } catch (final TMXLoadException e) {
            Debug.e(e);
        }

        final TMXLayer tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
        mScene.attachChild(tmxLayer);

        /* Make the camera not exceed the bounds of the TMXEntity. */
        this.mCamera.setBounds(0, 0, tmxLayer.getHeight(), tmxLayer.getWidth());
        this.mCamera.setBoundsEnabled(true);

        final FPSCounter fpsCounter = new FPSCounter();
        this.mEngine.registerUpdateHandler(fpsCounter);
        fpsText = new Text(0, 0, mFont, "FPS:", 10, getVertexBufferObjectManager());
        mScene.registerUpdateHandler(new TimerHandler(0.1f, true, new ITimerCallback() {
            @Override
            public void onTimePassed(final TimerHandler pTimerHandler) {
                fpsText.setText("FPS: " + Math.round(fpsCounter.getFPS()));
            }
        }));
        mScene.attachChild(fpsText);

        Sprite yourSprite = new Sprite(20, 20, yourTextureRegion, getVertexBufferObjectManager());
        mScene.attachChild(yourSprite);

        return mScene;
    }

    private BitmapTextureAtlas yourTexture;
    private ITextureRegion yourTextureRegion;

    private void loadGraphics() {
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        yourTexture = new BitmapTextureAtlas(getTextureManager(), 256, 256, TextureOptions.DEFAULT);
        yourTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(yourTexture, this, "soldier.png", 0,
                0);
        yourTexture.load();
    }

    private Sound yourSound;

    public void loadSounds() {
        // try {
        // yourSound = SoundFactory.createSoundFromAsset(getEngine()
        // .getSoundManager(), this, "mfx/sound.ogg");
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }

    @Override
    public void onScrollStarted(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX,
            final float pDistanceY) {
        final float zoomFactor = this.mCamera.getZoomFactor();
        this.mCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
    }

    @Override
    public void onScroll(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX,
            final float pDistanceY) {
        final float zoomFactor = this.mCamera.getZoomFactor();
        this.mCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
    }

    @Override
    public void onScrollFinished(final ScrollDetector pScollDetector, final int pPointerID, final float pDistanceX,
            final float pDistanceY) {
        final float zoomFactor = this.mCamera.getZoomFactor();
        this.mCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY / zoomFactor);
    }

    @Override
    public void onPinchZoomStarted(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent) {
        this.mPinchZoomStartedCameraZoomFactor = this.mCamera.getZoomFactor();
    }

    @Override
    public void onPinchZoom(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent,
            final float pZoomFactor) {
        this.mCamera.setZoomFactor(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
    }

    @Override
    public void onPinchZoomFinished(final PinchZoomDetector pPinchZoomDetector, final TouchEvent pTouchEvent,
            final float pZoomFactor) {
        this.mCamera.setZoomFactor(this.mPinchZoomStartedCameraZoomFactor * pZoomFactor);
    }

    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
        this.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);

        if (this.mPinchZoomDetector.isZooming()) {
            this.mScrollDetector.setEnabled(false);
        } else {
            if (pSceneTouchEvent.isActionDown()) {
                this.mScrollDetector.setEnabled(true);
            }
            this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
        }

        return true;
    }

}
