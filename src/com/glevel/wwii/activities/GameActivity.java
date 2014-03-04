package com.glevel.wwii.activities;

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
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.AnimatedSprite.IAnimationListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import com.glevel.wwii.R;
import com.glevel.wwii.analytics.GoogleAnalyticsHelper;
import com.glevel.wwii.analytics.GoogleAnalyticsHelper.EventAction;
import com.glevel.wwii.analytics.GoogleAnalyticsHelper.EventCategory;
import com.glevel.wwii.database.DatabaseHelper;
import com.glevel.wwii.game.AI;
import com.glevel.wwii.game.GameConverterHelper;
import com.glevel.wwii.game.GameGUI;
import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.GraphicsFactory;
import com.glevel.wwii.game.InputManager;
import com.glevel.wwii.game.andengine.custom.CustomLayoutGameActivity;
import com.glevel.wwii.game.andengine.custom.CustomZoomCamera;
import com.glevel.wwii.game.graphics.CenteredSprite;
import com.glevel.wwii.game.graphics.Crosshair;
import com.glevel.wwii.game.graphics.DeploymentZone;
import com.glevel.wwii.game.graphics.Protection;
import com.glevel.wwii.game.graphics.SelectionCircle;
import com.glevel.wwii.game.graphics.SoldierSprite;
import com.glevel.wwii.game.graphics.TankSprite;
import com.glevel.wwii.game.graphics.UnitSprite;
import com.glevel.wwii.game.interfaces.OnNewSpriteToDraw;
import com.glevel.wwii.game.logic.MapLogic;
import com.glevel.wwii.game.models.Battle;
import com.glevel.wwii.game.models.Battle.Phase;
import com.glevel.wwii.game.models.GameElement;
import com.glevel.wwii.game.models.GameElement.Rank;
import com.glevel.wwii.game.models.Player;
import com.glevel.wwii.game.models.map.Tile;
import com.glevel.wwii.game.models.orders.FireOrder;
import com.glevel.wwii.game.models.orders.MoveOrder;
import com.glevel.wwii.game.models.orders.Order;
import com.glevel.wwii.game.models.units.Soldier;
import com.glevel.wwii.game.models.units.Tank;
import com.glevel.wwii.game.models.units.categories.Unit;

public class GameActivity extends CustomLayoutGameActivity implements OnNewSpriteToDraw {

    private DatabaseHelper mDbHelper;
    private GraphicsFactory mGameElementFactory;
    private InputManager mInputManager;
    private GameGUI mGameGUI;

    public Battle battle = null;
    protected boolean mMustSaveGame = true;

    public Scene mScene;
    private ZoomCamera mCamera;
    public TMXLayer tmxLayer;
    public TMXTiledMap mTMXTiledMap;

    private Font mFont;
    public Sprite selectionCircle;
    public Line orderLine;
    public Crosshair crosshair, crossHairLine;
    public Protection protection;
    private DeploymentZone deploymentZone;

    public static int gameCounter = 0;

    @Override
    public EngineOptions onCreateEngineOptions() {
        this.mCamera = new CustomZoomCamera(0, 0, GameUtils.CAMERA_WIDTH, GameUtils.CAMERA_HEIGHT);
        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
                new FillResolutionPolicy(), mCamera);
        return engineOptions;
    }

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);

        initGameActivity();
    }

    protected void initGameActivity() {
        mDbHelper = new DatabaseHelper(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            // load game or new game
            long gameId = extras.getLong("game_id", 0);
            battle = mDbHelper.getBattleDao().getById(gameId);
        }

        if (battle == null) {
            // load last saved battle
            battle = GameConverterHelper.getUnfinishedBattles(mDbHelper).get(0);
        }

        battle.setOnNewSprite(this);

        // init GUI
        mGameGUI = new GameGUI(this);
        mGameGUI.showLoadingScreen();
        mGameGUI.initGUI();

        // used to keep only one saved battle for each game mode / campaign
        battle.setId(0L);
        GameConverterHelper.deleteSavedBattles(mDbHelper, battle.getCampaignId());
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
        mGameElementFactory = new GraphicsFactory(this, getVertexBufferObjectManager(), getTextureManager());
        mGameElementFactory.initGraphics(battle);

        // load font
        mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256,
                Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32, Color.WHITE.hashCode());
        mFont.load();

        pOnCreateResourcesCallback.onCreateResourcesFinished();
    }

    @Override
    protected synchronized void onResume() {
        if (GraphicsFactory.mTiledGfxMap.size() == 0 && mGameElementFactory != null) {
            mEngine.stop();
            mGameElementFactory.initGraphics(battle);
            mEngine.start();
        }

        super.onResume();
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
        // prepare scene
        mScene = new Scene();
        mScene.setOnAreaTouchTraversalFrontToBack();
        mScene.setBackground(new Background(0, 0, 0));
        mInputManager = new InputManager(this, mCamera);
        this.mScene.setOnSceneTouchListener(mInputManager);
        this.mScene.setTouchAreaBindingOnActionDownEnabled(true);

        // prepare tile map
        try {
            final TMXLoader tmxLoader = new TMXLoader(this.getAssets(), this.mEngine.getTextureManager(),
                    TextureOptions.BILINEAR_PREMULTIPLYALPHA, this.getVertexBufferObjectManager(), null);
            this.mTMXTiledMap = tmxLoader.loadFromAsset("tmx/" + battle.getTileMapName());
        } catch (final TMXLoadException e) {
            Debug.e(e);
        }
        tmxLayer = this.mTMXTiledMap.getTMXLayers().get(0);
        mScene.attachChild(tmxLayer);

        // make the camera not exceed the bounds of the TMXEntity
        this.mCamera.setBounds(0, 0, tmxLayer.getHeight(), tmxLayer.getWidth());
        this.mCamera.setBoundsEnabled(true);

        pOnCreateSceneCallback.onCreateSceneFinished(mScene);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
        // add selection circle
        selectionCircle = new SelectionCircle(GraphicsFactory.mGfxMap.get("selection.png"),
                getVertexBufferObjectManager());

        // add crosshairs, icons and order line
        crosshair = new Crosshair(GraphicsFactory.mGfxMap.get("crosshair.png"), getVertexBufferObjectManager());
        crosshair.setVisible(false);
        mScene.attachChild(crosshair);

        Text distanceText = new Text(0, 0, mFont, "", 5, getVertexBufferObjectManager());
        mScene.attachChild(distanceText);

        crossHairLine = new Crosshair(GraphicsFactory.mGfxMap.get("crosshair.png"), getVertexBufferObjectManager(),
                distanceText);
        crossHairLine.setVisible(false);
        mScene.attachChild(crossHairLine);

        protection = new Protection(GraphicsFactory.mGfxMap.get("protection.png"), getVertexBufferObjectManager());
        protection.setVisible(false);
        mScene.attachChild(protection);

        orderLine = new Line(0, 0, 0, 0, getVertexBufferObjectManager());
        orderLine.setColor(0.5f, 1f, 0.3f);
        orderLine.setLineWidth(50.0f);
        mScene.attachChild(orderLine);

        // init battle's map tiles
        Tile[][] lstTiles = new Tile[tmxLayer.getTileRows()][tmxLayer.getTileColumns()];
        for (TMXTile[] tt : tmxLayer.getTMXTiles()) {
            for (TMXTile t : tt) {
                lstTiles[t.getTileRow()][t.getTileColumn()] = new Tile(t, mTMXTiledMap);
            }
        }
        battle.getMap().setTiles(lstTiles);
        battle.getMap().setTmxLayer(tmxLayer);

        // add armies to scene
        for (Player player : battle.getPlayers()) {

            int[] deploymentBoundaries = battle.getDeploymentBoundaries(player);

            if (!battle.isStarted()) {
                // deploy troops for the first time
                AI.deployTroops(battle, player);
            }

            for (Unit unit : player.getUnits()) {
                // add element to scene / create sprite
                addElementToScene(unit, player.getArmyIndex() == 0);
                // init units rotation
                unit.getSprite().setRotation(deploymentBoundaries[0] == 0 ? 90 : -90);

                if (battle.isStarted()) {
                    // load position and rotation
                    float currentX = unit.getCurrentX();
                    float currentY = unit.getCurrentY();
                    float currentRotation = unit.getCurrentRotation();
                    TMXTile t = tmxLayer.getTMXTile((int) (currentX / GameUtils.PIXEL_BY_TILE),
                            (int) (currentY / GameUtils.PIXEL_BY_TILE));
                    unit.setTilePosition(battle.getMap().getTiles()[t.getTileRow()][t.getTileColumn()]);
                    unit.getSprite().setX(currentX);
                    unit.getSprite().setY(currentX);
                    unit.getSprite().setRotation(currentRotation);
                }
            }

        }

        pOnPopulateSceneCallback.onPopulateSceneFinished();

        // init camera position
        this.mCamera.setCenter(battle.getDeploymentBoundaries(battle.getMe())[0] * GameUtils.PIXEL_BY_TILE,
                tmxLayer.getHeight() / 2);

        // hide loading screen
        mGameGUI.hideLoadingScreen();

        if (battle.getPhase() == Phase.deployment) {
            startDeploymentPhase();
        } else {
            mGameGUI.hideDeploymentButton();
            startGame();
            startRenderLoop();
        }

        battle.setHasStarted(true);
    }

    private void startDeploymentPhase() {
        // add deployment fogs of war
        int[] deploymentBoundaries = battle.getDeploymentBoundaries(battle.getMe());
        if (deploymentBoundaries[0] == 0) {
            deploymentZone = new DeploymentZone(deploymentBoundaries[1] * GameUtils.PIXEL_BY_TILE, 0.0f, battle
                    .getMap().getWidth() * GameUtils.PIXEL_BY_TILE, battle.getMap().getHeight()
                    * GameUtils.PIXEL_BY_TILE, getVertexBufferObjectManager());
        } else {
            deploymentZone = new DeploymentZone(0.0f, 0.0f, deploymentBoundaries[0] * GameUtils.PIXEL_BY_TILE, battle
                    .getMap().getHeight() * GameUtils.PIXEL_BY_TILE, getVertexBufferObjectManager());
        }
        mScene.attachChild(deploymentZone);

        mGameGUI.displayBigLabel(getString(R.string.deployment_phase), R.color.white);

        startRenderLoop();
    }

    public void startGame() {
        // register game logic loop
        TimerHandler spriteTimerHandler = new TimerHandler(1.0f / GameUtils.GAME_LOOP_FREQUENCY, true,
                new ITimerCallback() {
                    @Override
                    public void onTimePassed(TimerHandler pTimerHandler) {
                        updateGame();
                    }
                });
        mEngine.registerUpdateHandler(spriteTimerHandler);

        // show go label
        mGameGUI.displayBigLabel(getString(R.string.go), R.color.white);

        // hide deployment
        if (deploymentZone != null) {
            deploymentZone.setVisible(false);
        }
        battle.setPhase(Phase.combat);
    }

    private void startRenderLoop() {
        // register render loop
        mScene.registerUpdateHandler(new IUpdateHandler() {
            @Override
            public void reset() {
            }

            @Override
            public void onUpdate(final float pSecondsElapsed) {
                updateMoves();

                // update selected element info
                mGameGUI.updateSelectedElementLayout(mInputManager.selectedElement);
            }
        });
    }

    @Override
    public void onBackPressed() {
        pauseGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGameGUI != null) {
            mGameGUI.onPause();
        }
        if (mGameElementFactory != null) {
            mGameElementFactory.onPause();
        }
        if (mMustSaveGame) {
            GameConverterHelper.saveGame(mDbHelper, battle);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }

    private void pauseGame() {
        mGameGUI.openGameMenu();
        mEngine.stop();
    }

    public void resumeGame() {
        mEngine.start();
    }

    private void updateGame() {
        gameCounter++;
        if (gameCounter > 999) {
            gameCounter = 0;
        }
        if (gameCounter % GameUtils.UPDATE_VISION_FREQUENCY == 0) {
            updateVisibility();
        }
        for (Player player : battle.getPlayers()) {
            for (Unit unit : player.getUnits()) {

                if (!unit.isDead()) {
                    if (player.isAI() && gameCounter % GameUtils.AI_FREQUENCY == 0) {
                        // update AI orders
                        AI.updateUnitOrder(battle, unit);
                    }
                    if (unit.getOrder() != null) {
                        // resolve unit action
                        unit.resolveOrder(battle);
                    } else {
                        // no order : take initiative
                        unit.takeInitiative();
                    }
                }
            }
            // check victory conditions
            if (gameCounter % GameUtils.CHECK_VICTORY_FREQUENCY == 0 && player.checkIfPlayerWon(battle)) {
                endGame(player, false);
            }
        }

        // update selected element order's crosshair
        if (mInputManager.selectedElement == null) {
            crosshair.setVisible(false);
        } else if (mInputManager.selectedElement.getGameElement() instanceof Unit) {
            Unit unit = (Unit) mInputManager.selectedElement.getGameElement();
            Order o = unit.getOrder();
            if (unit.getRank() == Rank.ally && o != null) {
                if (o instanceof FireOrder) {
                    FireOrder f = (FireOrder) o;
                    crosshair.setColor(Color.RED);
                    crosshair.setPosition(f.getTarget().getSprite().getX(), f.getTarget().getSprite().getY());
                    crosshair.setVisible(true);
                } else if (o instanceof MoveOrder) {
                    MoveOrder f = (MoveOrder) o;
                    crosshair.setColor(Color.GREEN);
                    crosshair.setPosition(f.getXDestination(), f.getYDestination());
                    crosshair.setVisible(true);
                } else {
                    crosshair.setVisible(false);
                }
            } else {
                crosshair.setVisible(false);
            }
        }
    }

    /**
     * Updates player vision
     */
    private void updateVisibility() {
        for (Unit unit : battle.getPlayers().get(1).getUnits()) {
            if (unit.getRank() == Rank.enemy && !unit.isDead()) {
                unit.setVisible(false);
            }
        }
        for (Unit u : battle.getMe().getUnits()) {
            if (!u.isDead()) {
                for (Unit e : battle.getEnemies(u)) {
                    if (MapLogic.canSee(battle.getMap(), u, e)) {
                        e.setVisible(true);
                    }
                }
            }
        }
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
    }

    public void addElementToScene(GameElement gameElement, boolean isMySquad) {
        // create sprite
        UnitSprite sprite = null;
        if (gameElement instanceof Soldier) {
            sprite = new SoldierSprite(gameElement, mInputManager, 0, 0, GraphicsFactory.mGfxMap.get(gameElement
                    .getSpriteName()), getVertexBufferObjectManager());
        } else if (gameElement instanceof Tank) {
            sprite = new TankSprite(gameElement, mInputManager, 0, 0, GraphicsFactory.mGfxMap.get(gameElement
                    .getSpriteName()), getVertexBufferObjectManager());
        }

        if (gameElement.getTilePosition() != null) {
            sprite.setPosition(gameElement.getTilePosition().getTileX(), gameElement.getTilePosition().getTileY());
        }
        gameElement.setSprite(sprite);
        gameElement.setRank(isMySquad ? Rank.ally : Rank.enemy);
        mScene.registerTouchArea(sprite);
        mScene.attachChild(sprite);
    }

    public void endGame(final Player winningPlayer, boolean instantly) {
        // stop engine
        mEngine.stop();

        if (winningPlayer != null) {
            GoogleAnalyticsHelper.sendEvent(getApplicationContext(), EventCategory.in_game, EventAction.end_game,
                    winningPlayer == battle.getMe() ? "victory" : "defeat");
        }

        if (instantly) {
            // show battle report without big label animation
            goToReport(winningPlayer == battle.getMe());
        } else {
            mGameGUI.displayVictoryLabel(winningPlayer == battle.getMe());
        }
    }

    public void goToReport(boolean victory) {
        long battleId = GameConverterHelper.saveGame(mDbHelper, battle);
        Intent i = new Intent(GameActivity.this, BattleReportActivity.class);
        Bundle extras = new Bundle();
        extras.putLong("game_id", battleId);
        extras.putBoolean("victory", victory);
        i.putExtras(extras);
        startActivity(i);
        finish();
    }

    @Override
    public void drawSprite(float x, float y, String spriteName, final int duration, final int size) {
        final Sprite sprite = new CenteredSprite(x, y, GraphicsFactory.mGfxMap.get(spriteName),
                getVertexBufferObjectManager());
        sprite.setScale(size);
        mScene.attachChild(sprite);
        if (duration > 0) {
            sprite.registerUpdateHandler(new IUpdateHandler() {

                private int timeLeft = duration;

                public void onUpdate(float pSecondsElapsed) {

                    if (--timeLeft <= 0) {
                        // remove sprite
                        runOnUpdateThread(new Runnable() {
                            @Override
                            public void run() {
                                mScene.detachChild(sprite);
                            }
                        });
                    }
                }

                @Override
                public void reset() {
                }

            });
        }
    }

    @Override
    public void drawAnimatedSprite(float x, float y, String spriteName, int frameDuration, float scale, int loopCount,
            final boolean removeAfter) {
        if (GraphicsFactory.mTiledGfxMap.get(spriteName) == null) {
            return;
        }

        final AnimatedSprite sprite = new AnimatedSprite(0, 0, GraphicsFactory.mTiledGfxMap.get(spriteName),
                getVertexBufferObjectManager());
        sprite.setPosition(x - sprite.getWidth() / 2.0f, y - sprite.getWidth() / 2.0f);
        sprite.setScale(scale);
        sprite.animate(frameDuration, loopCount, new IAnimationListener() {
            @Override
            public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
            }

            @Override
            public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount,
                    int pInitialLoopCount) {
            }

            @Override
            public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
            }

            @Override
            public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
                if (removeAfter) {
                    // remove sprite
                    runOnUpdateThread(new Runnable() {
                        @Override
                        public void run() {
                            mScene.detachChild(sprite);
                        }
                    });
                } else {
                    pAnimatedSprite.setCurrentTileIndex(1);
                }
            }
        });
        mScene.attachChild(sprite);
    }

    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignInSucceeded() {
    }

}
