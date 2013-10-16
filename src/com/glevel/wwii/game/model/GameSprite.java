package com.glevel.wwii.game.model;

import org.andengine.entity.sprite.Sprite;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.glevel.wwii.game.InputManager;

public class GameSprite extends Sprite {

    private final GameElement mGameElement;
    private static final int ACTION_MOVE_THRESHOLD = 150;
    private static final int VALID_ORDER_THRESHOLD = 100;
    private InputManager mInputManager;
    private boolean isGrabbed = false;

    public GameSprite(GameElement gameElement, InputManager inputManager, float pX, float pY,
            ITextureRegion pTextureRegion, VertexBufferObjectManager mVertexBufferObjectManager) {
        super(pX, pY, pTextureRegion, mVertexBufferObjectManager);
        mGameElement = gameElement;
        mInputManager = inputManager;
    }

    @Override
    public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX,
            final float pTouchAreaLocalY) {
        switch (pSceneTouchEvent.getAction()) {
        case TouchEvent.ACTION_DOWN:
            // element is selected
            mInputManager.onSelectGameElement(this);
            this.setAlpha(0.8f);
            break;
        case TouchEvent.ACTION_MOVE:
            if (!isGrabbed && Math.abs(pTouchAreaLocalX) + Math.abs(pTouchAreaLocalY) > ACTION_MOVE_THRESHOLD) {
                // element is dragged
                isGrabbed = true;
            }
            mInputManager.updateOrderLine(this, pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
            break;
        case TouchEvent.ACTION_UP:
            if (isGrabbed) {
                // cancel if small distance
                if (Math.abs(pSceneTouchEvent.getX() - getX()) + Math.abs(pSceneTouchEvent.getY() - getY()) > VALID_ORDER_THRESHOLD) {
                    // give order to unit
                    mInputManager.giveOrderToUnit(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
                }
            } else {
                // mInputManager.giveHideOrder(this);
            }
            mInputManager.hideOrderLine();
            isGrabbed = false;
            this.setAlpha(1.0f);
            break;
        }
        return true;
    }

    public GameElement getGameElement() {
        return mGameElement;
    }

}
