package com.glevel.wwii.activities;

import java.util.ArrayList;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.primitive.Line;
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
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.ui.activity.LayoutGameActivity;
import org.andengine.util.debug.Debug;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.glevel.wwii.R;
import com.glevel.wwii.game.GraphicElementFactory;
import com.glevel.wwii.game.InputManager;
import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.data.UnitsData;
import com.glevel.wwii.game.graphics.SelectionCircle;
import com.glevel.wwii.game.model.Battle;
import com.glevel.wwii.game.model.GameElement;
import com.glevel.wwii.game.model.GameSprite;
import com.glevel.wwii.game.model.Player;
import com.glevel.wwii.game.model.map.Tile;
import com.glevel.wwii.game.model.orders.DefendOrder;
import com.glevel.wwii.game.model.orders.FireOrder;
import com.glevel.wwii.game.model.orders.MoveOrder;
import com.glevel.wwii.game.model.orders.RunOrder;
import com.glevel.wwii.game.model.units.Soldier;
import com.glevel.wwii.game.model.units.Unit;
import com.glevel.wwii.game.model.units.Unit.Experience;
import com.glevel.wwii.game.model.units.Weapon;
import com.glevel.wwii.views.CustomAlertDialog;

public class GameActivity extends LayoutGameActivity {

    private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;
    private static final float CAMERA_ZOOM_MAX = 2.0f;
    private static final float CAMERA_ZOOM_MIN = 0.5f;

    private Scene mScene;
    private ZoomCamera mCamera;

    private Dialog mLoadingScreen;

    private TMXTiledMap mTMXTiledMap;

    public Sprite selectionCircle;
    public Line orderLine;

    private Battle battle;
    public ViewGroup mSelectedUnitLayout;

    @Override
    public EngineOptions onCreateEngineOptions() {
        this.mCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT, CAMERA_ZOOM_MIN, CAMERA_ZOOM_MAX);
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
                new FillResolutionPolicy(), mCamera);
        return engineOptions;
    }

    private void createTestData() {
        battle = new Battle();
        Player p = new Player();
        ArrayList<Unit> lstUnits = new ArrayList<Unit>();
        Tile t = new Tile();
        t.setxPosition(100);
        t.setyPosition(100);
        Unit e = UnitsData.buildScout(ArmiesData.USA, Experience.elite).copy();
        e.setTilePosition(t);
        lstUnits.add(e);
        p.setUnits(lstUnits);
        battle.getPlayers().add(p);
    }

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        createTestData();
        setupUI();

        // allow user to change the music volume with his phone's buttons
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void setupUI() {
        // setup loading screen
        mLoadingScreen = new Dialog(this, R.style.FullScreenDialog);
        mLoadingScreen.setContentView(R.layout.dialog_game_loading);
        mLoadingScreen.setCancelable(false);
        mLoadingScreen.setCanceledOnTouchOutside(false);
        // animate loading dots
        Animation loadingDotsAnimation = AnimationUtils.loadAnimation(this, R.anim.loading_dots);
        ((TextView) mLoadingScreen.findViewById(R.id.loadingDots)).startAnimation(loadingDotsAnimation);
        mLoadingScreen.show();

        // setup selected unit layout
        mSelectedUnitLayout = (ViewGroup) findViewById(R.id.selectedUnit);
    }

    public void updateSelectedElementLayout(final GameSprite selectedElement) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (selectedElement == null) {
                    mSelectedUnitLayout.setVisibility(View.GONE);
                    return;
                }
                Unit unit = (Unit) selectedElement.getGameElement();

                // image
                ((ImageView) mSelectedUnitLayout.findViewById(R.id.unitImage)).setImageResource(unit.getImage());

                // name
                if (unit instanceof Soldier) {
                    // display real name
                    ((TextView) mSelectedUnitLayout.findViewById(R.id.unitName))
                            .setText(((Soldier) unit).getRealName());
                } else {
                    ((TextView) mSelectedUnitLayout.findViewById(R.id.unitName)).setText(unit.getName());
                }

                // health
                ((TextView) mSelectedUnitLayout.findViewById(R.id.unitName)).setTextColor(getResources().getColor(
                        unit.getHealth().getColor()));

                // experience
                ((TextView) mSelectedUnitLayout.findViewById(R.id.unitExperience)).setText(unit.getExperience().name());
                ((TextView) mSelectedUnitLayout.findViewById(R.id.unitExperience)).setTextColor(getResources()
                        .getColor(unit.getExperience().getColor()));

                // weapons
                // main weapon
                Weapon mainWeapon = unit.getWeapons().get(0);
                ((TextView) mSelectedUnitLayout.findViewById(R.id.unitMainWeaponName)).setText(mainWeapon.getName());
                ((TextView) mSelectedUnitLayout.findViewById(R.id.unitMainWeaponName))
                        .setCompoundDrawablesWithIntrinsicBounds(mainWeapon.getImage(), 0, 0, 0);
                ((TextView) mSelectedUnitLayout.findViewById(R.id.unitMainWeaponAP)).setBackgroundResource(mainWeapon
                        .getAPColorEfficiency());
                ((TextView) mSelectedUnitLayout.findViewById(R.id.unitMainWeaponAT)).setBackgroundResource(mainWeapon
                        .getATColorEfficiency());
                ((TextView) mSelectedUnitLayout.findViewById(R.id.unitMainWeaponAmmo)).setText(""
                        + mainWeapon.getAmmoAmount());

                // secondary weapon
                if (unit.getWeapons().size() > 1) {
                    ((ViewGroup) mSelectedUnitLayout.findViewById(R.id.unitSecondaryWeapon))
                            .setVisibility(View.VISIBLE);
                    Weapon secondaryWeapon = unit.getWeapons().get(1);
                    ((TextView) mSelectedUnitLayout.findViewById(R.id.unitSecondaryWeaponName)).setText(secondaryWeapon
                            .getName());
                    ((TextView) mSelectedUnitLayout.findViewById(R.id.unitSecondaryWeaponName))
                            .setCompoundDrawablesWithIntrinsicBounds(secondaryWeapon.getImage(), 0, 0, 0);
                    ((TextView) mSelectedUnitLayout.findViewById(R.id.unitSecondaryWeaponAP))
                            .setBackgroundResource(secondaryWeapon.getAPColorEfficiency());
                    ((TextView) mSelectedUnitLayout.findViewById(R.id.unitSecondaryWeaponAT))
                            .setBackgroundResource(secondaryWeapon.getATColorEfficiency());
                    ((TextView) mSelectedUnitLayout.findViewById(R.id.unitSecondaryWeaponAmmo)).setText(""
                            + secondaryWeapon.getAmmoAmount());
                } else {
                    ((ViewGroup) mSelectedUnitLayout.findViewById(R.id.unitSecondaryWeapon)).setVisibility(View.GONE);
                }
                // frags
                ((TextView) mSelectedUnitLayout.findViewById(R.id.unitFrags)).setText(getString(R.string.frags_number,
                        unit.getFrags()));

                // current action
                ((TextView) mSelectedUnitLayout.findViewById(R.id.unitAction)).setText(unit.getCurrentAction().name());

                mSelectedUnitLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private Font mFont;
    private Text fpsText;
    private GraphicElementFactory gameElementFactory;
    private InputManager mInputManager;
    private Dialog mGameMenuDialog;

    private void updateGame() {
        for (Player player : battle.getPlayers()) {
            for (Unit unit : player.getUnits()) {
                if (unit.getOrder() != null) {
                    // resolve unit action
                    resolveUnitOrder(unit);
                } else {
                    // no order : take initiative
                    makeUnitTakeInitiative(unit);
                }
            }
        }
        updateSelectedElementLayout(mInputManager.selectedElement);
    }

    private void resolveUnitOrder(Unit unit) {
        if (unit.getPanic() > 0) {
            // test if the unit can react
            if (unit.getExperience().ordinal() < unit.getPanic()) {
                // the unit is hiding
                unit.hide();
                return;
            }
        }

        if (unit.getOrder() instanceof MoveOrder) {
            // unit is moving
            unit.move(false);
        } else if (unit.getOrder() instanceof RunOrder) {
            // unit is running
            unit.move(true);
        } else if (unit.getOrder() instanceof DefendOrder) {
            // TODO check if there are enemies to be shot
            unit.hide();
        } else if (unit.getOrder() instanceof FireOrder) {
            // TODO check if unit can still shoot on enemy
            unit.hide();
            unit.setOrder(new DefendOrder(unit));
        }
    }

    private void makeUnitTakeInitiative(Unit unit) {
        // TODO
        // add order
        unit.setOrder(new DefendOrder(unit));
        resolveUnitOrder(unit);
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_game;
    }

    @Override
    protected int getRenderSurfaceViewID() {
        return R.id.surfaceView;
    }

    @Override
    public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws Exception {
        // init game element factory
        gameElementFactory = new GraphicElementFactory(this, getVertexBufferObjectManager(), getTextureManager());

        gameElementFactory.initGraphics(battle);

        this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
        this.mFont.load();
        mLoadingScreen.dismiss();
        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
        mScene = new Scene();

        // update game logic loop
        TimerHandler spriteTimerHandler;
        float mEffectSpawnDelay = 0.1f;
        spriteTimerHandler = new TimerHandler(mEffectSpawnDelay, true, new ITimerCallback() {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                updateGame();
            }
        });
        getEngine().registerUpdateHandler(spriteTimerHandler);

        mScene.setOnAreaTouchTraversalFrontToBack();

        mScene.setBackground(new Background(0, 0, 0));

        mInputManager = new InputManager(this, mCamera);
        this.mScene.setOnSceneTouchListener(mInputManager);
        this.mScene.setTouchAreaBindingOnActionDownEnabled(true);

        // tile map
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

        for (Player player : battle.getPlayers()) {
            for (GameElement gameElement : player.getUnits()) {
                gameElementFactory.addGameElement(mScene, gameElement, mInputManager);
            }
        }

        selectionCircle = new SelectionCircle(gameElementFactory.mGfxMap.get("selection.png"),
                getVertexBufferObjectManager());

        orderLine = new Line(0, 0, 0, 0, getVertexBufferObjectManager());
        orderLine.setColor(0.5f, 1f, 0.3f);
        orderLine.setLineWidth(50.0f);
        mScene.attachChild(orderLine);
        pOnCreateSceneCallback.onCreateSceneFinished(mScene);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
        pOnPopulateSceneCallback.onPopulateSceneFinished();
    }

    @Override
    public void onBackPressed() {
        openGameMenu();
    }

    private void openGameMenu() {
        mGameMenuDialog = new Dialog(this, R.style.FullScreenDialog);
        mGameMenuDialog.setContentView(R.layout.dialog_game_menu);
        mGameMenuDialog.setCancelable(true);
        // surrender button
        Animation a = AnimationUtils.loadAnimation(this, R.anim.bottom_in);
        mGameMenuDialog.findViewById(R.id.surrenderButton).setAnimation(a);
        mGameMenuDialog.findViewById(R.id.surrenderButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog confirmDialog = new CustomAlertDialog(GameActivity.this, R.style.Dialog,
                        getString(R.string.confirm_surrender_message), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == R.id.okButton) {
                                    finish();
                                }
                                dialog.dismiss();
                            }
                        });
                confirmDialog.show();
            }
        });
        // resume game button
        mGameMenuDialog.findViewById(R.id.resumeGameButton).setAnimation(a);
        mGameMenuDialog.findViewById(R.id.resumeGameButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameMenuDialog.dismiss();
            }
        });
        // exit button
        mGameMenuDialog.findViewById(R.id.exitButton).setAnimation(a);
        mGameMenuDialog.findViewById(R.id.exitButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO save
                startActivity(new Intent(GameActivity.this, HomeActivity.class));
                finish();
            }
        });
        mGameMenuDialog.show();
        a.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGameMenuDialog != null) {
            mGameMenuDialog.dismiss();
        }
        if (mLoadingScreen != null) {
            mLoadingScreen.dismiss();
        }
    }

}
