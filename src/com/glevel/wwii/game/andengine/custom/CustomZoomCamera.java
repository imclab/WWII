package com.glevel.wwii.game.andengine.custom;

import org.andengine.engine.camera.ZoomCamera;

public class CustomZoomCamera extends ZoomCamera {

    // add zoom limitations
    private float mZoomMin = 0.0f, mZoomMax = 100.0f;

    public CustomZoomCamera(final float pX, final float pY, final float pWidth, final float pHeight, float zoomMin,
            float zoomMax) {
        super(pX, pY, pWidth, pHeight);
        this.mZoomMin = zoomMin;
        this.mZoomMax = zoomMax;
    }

    @Override
    public void setZoomFactor(final float pZoomFactor) {
        if (pZoomFactor > mZoomMin && pZoomFactor < mZoomMax) {
            this.mZoomFactor = pZoomFactor;

            if (this.mBoundsEnabled) {
                this.ensureInBounds();
            }
        }
    }

}
