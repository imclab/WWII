package com.glevel.wwii.activities;

import java.util.ArrayList;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.IUpdateHandler;
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
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
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
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.glevel.wwii.R;
import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.GraphicElementFactory;
import com.glevel.wwii.game.InputManager;
import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.data.UnitsData;
import com.glevel.wwii.game.graphics.Crosshair;
import com.glevel.wwii.game.graphics.Protection;
import com.glevel.wwii.game.graphics.SelectionCircle;
import com.glevel.wwii.game.model.Battle;
import com.glevel.wwii.game.model.GameSprite;
import com.glevel.wwii.game.model.Player;
import com.glevel.wwii.game.model.GameElement.Rank;
import com.glevel.wwii.game.model.map.Map;
import com.glevel.wwii.game.model.map.Tile;
import com.glevel.wwii.game.model.orders.DefendOrder;
import com.glevel.wwii.game.model.orders.FireOrder;
import com.glevel.wwii.game.model.orders.MoveOrder;
import com.glevel.wwii.game.model.orders.Order;
import com.glevel.wwii.game.model.units.Soldier;
import com.glevel.wwii.game.model.units.Unit;
import com.glevel.wwii.game.model.units.Unit.Experience;
import com.glevel.wwii.game.model.units.Unit.InjuryState;
import com.glevel.wwii.game.model.units.Weapon;
import com.glevel.wwii.views.CustomAlertDialog;

public class GameActivity extends LayoutGameActivity {

    private static final int CAMERA_WIDTH = 800;
    private static final int CAMERA_HEIGHT = 480;
    private static final float CAMERA_ZOOM_MAX = 2.0f;
    private static final float CAMERA_ZOOM_MIN = 0.5f;

    public Scene mScene;
    private ZoomCamera mCamera;

    private Dialog mLoadingScreen;

    public TMXTiledMap mTMXTiledMap;

    public Sprite selectionCircle;
    public Line orderLine;

    public Battle battle;
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

        // me
        Player p = new Player();
        p.setArmy(ArmiesData.USA);
        ArrayList<Unit> lstUnits = new ArrayList<Unit>();
        Unit e = UnitsData.buildScout(ArmiesData.USA, Experience.elite).copy();
        lstUnits.add(e);
        Unit e2 = UnitsData.buildRifleMan(ArmiesData.USA, Experience.veteran).copy();
        lstUnits.add(e2);
        p.setUnits(lstUnits);
        p.setArmyIndex(0);
        battle.getPlayers().add(p);

        // enemy
        p = new Player();
        p.setArmy(ArmiesData.GERMANY);
        lstUnits = new ArrayList<Unit>();
        e = UnitsData.buildScout(ArmiesData.GERMANY, Experience.recruit).copy();
        lstUnits.add(e);
        e2 = UnitsData.buildRifleMan(ArmiesData.GERMANY, Experience.veteran).copy();
        lstUnits.add(e2);
        p.setUnits(lstUnits);
        p.setArmyIndex(1);
        battle.getPlayers().add(p);
    }

    private void placeElements() {
        for (Player player : battle.getPlayers()) {
            for (Unit unit : player.getUnits()) {
                TMXTile t = tmxLayer.getTMXTile((int) (Math.random() * 20), (int) (Math.random() * 20));
                unit.setTilePosition(battle.getMap().getTiles()[t.getTileRow()][t.getTileColumn()]);
            }
        }
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
                    crosshair.setVisible(false);
                    return;
                }
                Unit unit = (Unit) selectedElement.getGameElement();

                // hide enemies info
                updateUnitInfoVisibility(unit.getArmy() == battle.getPlayers().get(0).getArmy());

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

                Order o = unit.getOrder();
                if (unit.getRank() == Rank.ally && o != null) {
                    if (o instanceof FireOrder) {
                        FireOrder f = (FireOrder) o;
                        crosshair.setColor(Color.RED);
                        crosshair.setPosition(f.getTarget().getSprite().getX() - crosshair.getWidth() / 2, f
                                .getTarget().getSprite().getY()
                                - crosshair.getHeight() / 2);
                        crosshair.setVisible(true);
                    } else if (o instanceof MoveOrder) {
                        MoveOrder f = (MoveOrder) o;
                        crosshair.setColor(Color.GREEN);
                        crosshair.setPosition(f.getxDestination() - crosshair.getWidth() / 2, f.getyDestination()
                                - crosshair.getHeight() / 2);
                        crosshair.setVisible(true);
                    } else {
                        crosshair.setVisible(false);
                    }
                } else {
                    crosshair.setVisible(false);
                }

            }
        });
    }

    private void updateUnitInfoVisibility(boolean isAlly) {
        int visibility = isAlly ? View.VISIBLE : View.GONE;
        ((TextView) mSelectedUnitLayout.findViewById(R.id.unitExperience)).setVisibility(visibility);
        ((TextView) mSelectedUnitLayout.findViewById(R.id.unitMainWeaponAmmo)).setVisibility(visibility);
        ((TextView) mSelectedUnitLayout.findViewById(R.id.unitSecondaryWeaponAmmo)).setVisibility(visibility);
        ((TextView) mSelectedUnitLayout.findViewById(R.id.unitFrags)).setVisibility(visibility);
    }

    private Font mFont;
    private Text fpsText;
    private GraphicElementFactory gameElementFactory;
    private InputManager mInputManager;
    private Dialog mGameMenuDialog;
    public Crosshair crosshair, crossHairLine;
    public Protection protection;
    public TMXLayer tmxLayer;
    private PhysicsWorld mPhysicsWorld;

    public static int gameCounter = 0;

    private void updateGame() {
        gameCounter++;
        if (gameCounter > 999) {
            gameCounter = 0;
        }
        for (Player player : battle.getPlayers()) {
            for (Unit unit : player.getUnits()) {
                if (unit.getHealth() != InjuryState.dead) {
                    if (unit.getOrder() != null) {
                        // resolve unit action
                        unit.resolveOrder(battle, tmxLayer);
                    } else {
                        // no order : take initiative
                        unit.takeInitiative();
                    }
                }
            }
        }
        updateSelectedElementLayout(mInputManager.selectedElement);
    }

    private void updateMoves() {
        for (Player player : battle.getPlayers()) {
            for (Unit unit : player.getUnits()) {
                if (unit.getOrder() != null && unit.getOrder() instanceof MoveOrder) {
                    // move unit
                    unit.move();
                    TMXTile newTile = tmxLayer.getTMXTileAt(unit.getSprite().getX(), unit.getSprite().getY());
                    if (newTile.getTileX() != unit.getTilePosition().getTileX()
                            || newTile.getTileY() != unit.getTilePosition().getTileY()) {
                        unit.setTilePosition(battle.getMap().getTiles()[newTile.getTileRow()][newTile.getTileColumn()]);
                    }
                }
            }
        }
        updateSelectedElementLayout(mInputManager.selectedElement);
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

    private static final FixtureDef FIXTURE_DEF = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
        mScene = new Scene();

        this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, 0), false);

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

        tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
        mScene.attachChild(tmxLayer);

        // init map
        Map m = new Map();
        Tile[][] lstTiles = new Tile[tmxLayer.getTileRows()][tmxLayer.getTileColumns()];
        for (TMXTile[] tt : tmxLayer.getTMXTiles()) {
            for (TMXTile t : tt) {
                lstTiles[t.getTileRow()][t.getTileColumn()] = new Tile(t, mTMXTiledMap);
            }
        }
        m.setTiles(lstTiles);
        battle.setMap(m);

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

        placeElements();
        for (Player player : battle.getPlayers()) {
            for (Unit unit : player.getUnits()) {
                GameSprite g = gameElementFactory.addGameElement(mScene, unit, mInputManager,
                        (player.getArmyIndex() == 0));
                Body body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, g, BodyType.DynamicBody, FIXTURE_DEF);
                this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(g, body, true, true));
            }
        }

        selectionCircle = new SelectionCircle(GraphicElementFactory.mGfxMap.get("selection.png"),
                getVertexBufferObjectManager());

        crosshair = new Crosshair(GraphicElementFactory.mGfxMap.get("crosshair.png"), getVertexBufferObjectManager());
        crosshair.setVisible(false);
        mScene.attachChild(crosshair);
        crossHairLine = new Crosshair(GraphicElementFactory.mGfxMap.get("crosshair.png"),
                getVertexBufferObjectManager());
        crossHairLine.setVisible(false);
        mScene.attachChild(crossHairLine);
        protection = new Protection(GraphicElementFactory.mGfxMap.get("protection.png"), getVertexBufferObjectManager());
        protection.setVisible(false);
        mScene.attachChild(protection);

        orderLine = new Line(0, 0, 0, 0, getVertexBufferObjectManager());
        orderLine.setColor(0.5f, 1f, 0.3f);
        orderLine.setLineWidth(50.0f);
        mScene.attachChild(orderLine);

        // Render loop
        mScene.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void reset() {
            }

            @Override
            public void onUpdate(final float pSecondsElapsed) {
                updateMoves();
            }
        });

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
        Animation menuButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_in);
        mGameMenuDialog.findViewById(R.id.surrenderButton).setAnimation(menuButtonAnimation);
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
        mGameMenuDialog.findViewById(R.id.resumeGameButton).setAnimation(menuButtonAnimation);
        mGameMenuDialog.findViewById(R.id.resumeGameButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mGameMenuDialog.dismiss();
            }
        });
        // exit button
        mGameMenuDialog.findViewById(R.id.exitButton).setAnimation(menuButtonAnimation);
        mGameMenuDialog.findViewById(R.id.exitButton).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO save
                startActivity(new Intent(GameActivity.this, HomeActivity.class));
                finish();
            }
        });
        mGameMenuDialog.show();
        menuButtonAnimation.start();
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
