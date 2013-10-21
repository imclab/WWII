package com.glevel.wwii.game;

import org.andengine.engine.camera.ZoomCamera;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.util.color.Color;

import android.view.MotionEvent;

import com.glevel.wwii.activities.GameActivity;
import com.glevel.wwii.game.model.GameSprite;
import com.glevel.wwii.game.model.orders.DefendOrder;
import com.glevel.wwii.game.model.orders.FireOrder;
import com.glevel.wwii.game.model.orders.MoveOrder;
import com.glevel.wwii.game.model.units.Unit;
import com.glevel.wwii.game.model.units.Unit.InjuryState;

public class InputManager implements IOnSceneTouchListener, IScrollDetectorListener, IPinchZoomDetectorListener {

    private ZoomCamera mCamera;
    private float mPinchZoomStartedCameraZoomFactor;
    private SurfaceScrollDetector mScrollDetector;
    private PinchZoomDetector mPinchZoomDetector;
    private static final int ACTION_MOVE_THRESHOLD = 100;

    public boolean isLongPressTriggered = false;
    private boolean isDragged = false;

    public GameSprite selectedElement = null;
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

    public void onSelectGameElement(GameSprite gameSprite) {
        if (selectedElement != null) {
            selectedElement.detachChild(mGameActivity.selectionCircle);
        }
        selectedElement = gameSprite;
        mGameActivity.selectionCircle.setColor(selectedElement.getGameElement().getSelectionColor());
        mGameActivity.selectionCircle.setZIndex(-10);
        gameSprite.attachChild(mGameActivity.selectionCircle);
    }

    public void giveHideOrder(GameSprite gameSprite) {
        if (gameSprite == selectedElement) {
            // give defend order
            Unit unit = (Unit) selectedElement.getGameElement();
            unit.setOrder(new DefendOrder(unit));
        }
    }

    public void giveOrderToUnit(float x, float y) {
        if (selectedElement != null) {
            if (selectedElement.getGameElement() instanceof Unit) {
                Unit unit = (Unit) selectedElement.getGameElement();
                GameSprite g = getElementAtCoordinates(x, y);
                if (g != null && g.getGameElement() instanceof Unit && g != selectedElement
                        && ((Unit) g.getGameElement()).getHealth() != InjuryState.dead
                        && ((Unit) g.getGameElement()).getArmy() != ((Unit) selectedElement.getGameElement()).getArmy()) {
                    unit.setOrder(new FireOrder(unit, (Unit) g.getGameElement()));
                } else {
                    unit.setOrder(new MoveOrder(unit, x, y));
                }
            }
        }
    }

    public void updateOrderLine(GameSprite gameSprite, float x, float y) {
        mGameActivity.orderLine.setPosition(gameSprite.getX(), gameSprite.getY(), x, y);
        // TODO set color
        // add anims
        GameSprite g = getElementAtCoordinates(x, y);
        if (g != null && g != gameSprite && g.getGameElement() instanceof Unit
                && ((Unit) g.getGameElement()).getHealth() != InjuryState.dead
                && ((Unit) g.getGameElement()).getArmy() != ((Unit) selectedElement.getGameElement()).getArmy()) {
            mGameActivity.orderLine.setColor(Color.RED);
            mGameActivity.crossHairLine.setColor(Color.RED);
            mGameActivity.crossHairLine.setPosition(x - mGameActivity.crossHairLine.getWidth() / 2, y
                    - mGameActivity.crossHairLine.getHeight() / 2);
            mGameActivity.crossHairLine.setVisible(true);
        } else {
            mGameActivity.orderLine.setColor(Color.GREEN);
            mGameActivity.crossHairLine.setColor(Color.GREEN);
            mGameActivity.crossHairLine.setPosition(x - mGameActivity.crossHairLine.getWidth() / 2, y
                    - mGameActivity.crossHairLine.getHeight() / 2);
            mGameActivity.crossHairLine.setVisible(true);
        }
        mGameActivity.orderLine.setVisible(true);
    }

    public void hideOrderLine() {
        mGameActivity.orderLine.setVisible(false);
        mGameActivity.crossHairLine.setVisible(false);
    }

    private static final int HOVER_UNIT_RADIUS_THRESHOLD = 60;

    private GameSprite getElementAtCoordinates(float x, float y) {
        for (GameSprite g : mGameActivity.lstUnits) {
            if (Math.abs(g.getX() - x) < HOVER_UNIT_RADIUS_THRESHOLD
                    && Math.abs(g.getY() - y) < HOVER_UNIT_RADIUS_THRESHOLD) {
                return g;
            }
        }
        return null;
    }
}
