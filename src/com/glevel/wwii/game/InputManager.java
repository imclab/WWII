package com.glevel.wwii.game;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.util.color.Color;

import android.view.MotionEvent;

import com.glevel.wwii.activities.GameActivity;
import com.glevel.wwii.game.graphics.UnitSprite;
import com.glevel.wwii.game.logic.MapLogic;
import com.glevel.wwii.game.models.Battle.Phase;
import com.glevel.wwii.game.models.Player;
import com.glevel.wwii.game.models.map.Tile;
import com.glevel.wwii.game.models.orders.DefendOrder;
import com.glevel.wwii.game.models.orders.FireOrder;
import com.glevel.wwii.game.models.orders.HideOrder;
import com.glevel.wwii.game.models.orders.MoveOrder;
import com.glevel.wwii.game.models.units.categories.Unit;
import com.glevel.wwii.game.models.units.categories.Vehicle;
import com.glevel.wwii.game.models.weapons.categories.IndirectWeapon;

public class InputManager implements IOnSceneTouchListener, IScrollDetectorListener, IPinchZoomDetectorListener {

    private static final int ACTION_MOVE_THRESHOLD = 100;
    private static final int HOVER_UNIT_RADIUS_THRESHOLD = 60;

    private ZoomCamera mCamera;
    private float mPinchZoomStartedCameraZoomFactor;
    private SurfaceScrollDetector mScrollDetector;
    private PinchZoomDetector mPinchZoomDetector;

    public boolean isLongPressTriggered = false;
    private boolean isDragged = false;

    public UnitSprite selectedElement = null;
    private GameActivity mGameActivity;
    private float lastX;
    private float lastY;

    public InputManager(GameActivity gameActivity, ZoomCamera camera) {
        mCamera = camera;
        this.mScrollDetector = new SurfaceScrollDetector(this);
        this.mPinchZoomDetector = new PinchZoomDetector(this);
        this.mGameActivity = gameActivity;
    }

    /**
     * Map scrolling
     */
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

    /**
     * Map zooming
     */
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

    /**
     * Click on map
     */
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

            switch (pSceneTouchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDragged = false;
                lastX = pSceneTouchEvent.getX();
                lastY = pSceneTouchEvent.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(pSceneTouchEvent.getX() - lastX) + Math.abs(pSceneTouchEvent.getY() - lastY) > ACTION_MOVE_THRESHOLD) {
                    isDragged = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!isDragged) {
                    // simple tap
                    clickOnMap(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
                }
                isDragged = false;
                break;
            }
        }
        return true;
    }

    private void clickOnMap(float x, float y) {
        if (selectedElement != null) {
            selectedElement.detachChild(mGameActivity.selectionCircle);
            selectedElement = null;
        }
    }

    public void onSelectGameElement(UnitSprite unitSprite) {
        if (selectedElement != null) {
            selectedElement.detachChild(mGameActivity.selectionCircle);
        }
        if (unitSprite.isVisible()) {
            selectedElement = unitSprite;
            mGameActivity.selectionCircle.setColor(selectedElement.getGameElement().getSelectionColor());
            mGameActivity.selectionCircle.setPosition(
                    (selectedElement.getWidth() - mGameActivity.selectionCircle.getWidth()) / 2,
                    (selectedElement.getHeight() - mGameActivity.selectionCircle.getHeight()) / 2);
            if (selectedElement.getGameElement() instanceof Vehicle) {
                mGameActivity.selectionCircle.setScale(1.5f);
            } else {
                mGameActivity.selectionCircle.setScale(1.0f);
            }
            unitSprite.attachChild(mGameActivity.selectionCircle);
        }
    }

    public void giveHideOrder(UnitSprite gameSprite) {
        if (gameSprite == selectedElement) {
            Unit unit = (Unit) selectedElement.getGameElement();
            // switch between hide and defend orders
            if (unit instanceof Vehicle || unit.getOrder() instanceof HideOrder) {
                unit.setOrder(new DefendOrder());
            } else {
                unit.setOrder(new HideOrder());
            }
        }
    }

    public void giveOrderToUnit(float x, float y) {
        if (mGameActivity.battle.getPhase() == Phase.combat && selectedElement != null) {
            if (selectedElement.getGameElement() instanceof Unit) {
                Unit unit = (Unit) selectedElement.getGameElement();
                UnitSprite g = getElementAtCoordinates(x, y);
                if (!unit.isDead() && g != null && g.getGameElement() instanceof Unit && g != selectedElement
                        && !((Unit) g.getGameElement()).isDead()
                        && ((Unit) g.getGameElement()).getArmy() != ((Unit) selectedElement.getGameElement()).getArmy()) {
                    unit.setOrder(new FireOrder((Unit) g.getGameElement()));
                } else if (!unit.isDead() && unit.canMove()) {
                    unit.setOrder(new MoveOrder(x, y));
                }
            }
        }
    }

    public void updateOrderLine(UnitSprite gameSprite, float x, float y) {
        if (mGameActivity.battle.getPhase() == Phase.deployment) {
            // during deployment phase
            TMXTile tmxtile = mGameActivity.tmxLayer.getTMXTileAt(x, y);
            Tile tile = mGameActivity.battle.getMap().getTiles()[tmxtile.getTileRow()][tmxtile.getTileColumn()];
            int[] deploymentBoundaries = mGameActivity.battle.getDeploymentBoundaries(mGameActivity.battle.getMe());
            if (tmxtile != null && tmxtile.getTileColumn() >= deploymentBoundaries[0]
                    && tmxtile.getTileColumn() < deploymentBoundaries[1]
                    && ((Unit) gameSprite.getGameElement()).canMoveIn(tile)) {
                gameSprite.setPosition(x, y);
                gameSprite.getGameElement().setTilePosition(
                        mGameActivity.battle.getMap().getTiles()[tmxtile.getTileRow()][tmxtile.getTileColumn()]);
            }
        } else {
            // get distance
            int distance = (int) MapLogic.getDistanceBetween(gameSprite.getX(), gameSprite.getY(), x, y);

            // during combat phase
            mGameActivity.orderLine.setPosition(gameSprite.getX(), gameSprite.getY(), x, y);
            UnitSprite g = getElementAtCoordinates(x, y);
            if (g != null && g.isVisible() && g != gameSprite && g.getGameElement() instanceof Unit
                    && !((Unit) g.getGameElement()).isDead() && g.isVisible()
                    && ((Unit) g.getGameElement()).getArmy() != ((Unit) selectedElement.getGameElement()).getArmy()) {
                // attack
                if (((Unit) selectedElement.getGameElement()).getWeapons().get(0) instanceof IndirectWeapon
                        || MapLogic.canSee(mGameActivity.battle.getMap(), selectedElement.getGameElement(), x, y)) {
                    mGameActivity.orderLine.setColor(Color.RED);
                } else {
                    mGameActivity.orderLine.setColor(Color.BLACK);
                }
                mGameActivity.crossHairLine.setColor(Color.RED);
                mGameActivity.crossHairLine.setPosition(x, y);
                mGameActivity.crossHairLine.setVisible(true);
                mGameActivity.crossHairLine.updateDistanceLabel(distance, mGameActivity.battle,
                        (Unit) gameSprite.getGameElement(), (Unit) g.getGameElement());
                mGameActivity.protection.setVisible(false);
            } else if (!((Unit) gameSprite.getGameElement()).canMove()) {
                // immobile units
                if (((Unit) selectedElement.getGameElement()).getWeapons().get(0) instanceof IndirectWeapon
                        || MapLogic.canSee(mGameActivity.battle.getMap(), selectedElement.getGameElement(), x, y)) {
                    mGameActivity.orderLine.setColor(Color.RED);
                } else {
                    mGameActivity.orderLine.setColor(Color.BLACK);
                }
                mGameActivity.crossHairLine.setColor(Color.TRANSPARENT);
                mGameActivity.crossHairLine.setPosition(x, y);
                mGameActivity.crossHairLine.updateDistanceLabel(distance, mGameActivity.battle,
                        (Unit) gameSprite.getGameElement(), null);
                mGameActivity.crossHairLine.setVisible(true);
                mGameActivity.protection.setVisible(false);
            } else {
                // move
                if (mGameActivity.battle.getMap().getTiles()[mGameActivity.tmxLayer.getTMXTileAt(x, y).getTileRow()][mGameActivity.tmxLayer
                        .getTMXTileAt(x, y).getTileColumn()].getTerrain() != null) {
                    // grants protection
                    mGameActivity.protection.setPosition(x, y);
                    mGameActivity.protection.setVisible(true);
                    mGameActivity.crossHairLine.setColor(Color.TRANSPARENT);
                } else {
                    mGameActivity.protection.setVisible(false);
                    mGameActivity.crossHairLine.setColor(Color.GREEN);
                }

                mGameActivity.crossHairLine.setPosition(x, y);
                mGameActivity.crossHairLine.updateDistanceLabel(distance, mGameActivity.battle,
                        (Unit) gameSprite.getGameElement(), null);
                mGameActivity.crossHairLine.setVisible(true);
                mGameActivity.orderLine.setColor(Color.GREEN);
            }
            mGameActivity.orderLine.setVisible(true);
        }
    }

    public void hideOrderLine() {
        mGameActivity.orderLine.setVisible(false);
        mGameActivity.crossHairLine.setVisible(false);
        mGameActivity.protection.setVisible(false);
    }

    private UnitSprite getElementAtCoordinates(float x, float y) {
        for (Player p : mGameActivity.battle.getPlayers()) {
            for (Unit g : p.getUnits()) {
                if (Math.abs(g.getSprite().getX() - x) < HOVER_UNIT_RADIUS_THRESHOLD
                        && Math.abs(g.getSprite().getY() - y) < HOVER_UNIT_RADIUS_THRESHOLD) {
                    return g.getSprite();
                }
            }
        }
        return null;
    }

    public boolean isDeploymentPhase() {
        return mGameActivity.battle.getPhase() == Phase.deployment;
    }

}
