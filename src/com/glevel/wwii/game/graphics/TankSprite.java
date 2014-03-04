package com.glevel.wwii.game.graphics;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.glevel.wwii.game.GraphicsFactory;
import com.glevel.wwii.game.InputManager;
import com.glevel.wwii.game.models.GameElement;
import com.glevel.wwii.game.models.weapons.categories.DeflectionWeapon;
import com.glevel.wwii.game.models.weapons.categories.Weapon;

public class TankSprite extends UnitSprite {

    private transient CenteredSprite turretSprite;
    private transient Sprite mainWeaponMuzzle, secondaryWeaponMuzzle;
    private transient boolean isMainWeaponFiring, isSecondaryWeaponFiring;

    public TankSprite(GameElement gameElement, InputManager inputManager, float pX, float pY,
            ITextureRegion pTextureRegion, VertexBufferObjectManager mVertexBufferObjectManager) {
        super(gameElement, inputManager, pX, pY, pTextureRegion, mVertexBufferObjectManager);
        addTurretSprite();
        addMuzzleFlashSprites();
    }

    private void addTurretSprite() {
        turretSprite = new CenteredSprite(0, 0, GraphicsFactory.mGfxMap.get("panzeriv_turret.png"),
                getVertexBufferObjectManager());
        attachChild(turretSprite);
    }

    private void addMuzzleFlashSprites() {
        secondaryWeaponMuzzle = new Sprite(76, -5, GraphicsFactory.mGfxMap.get("muzzle_flash.png"),
                getVertexBufferObjectManager());
        secondaryWeaponMuzzle.setScale(0.8f);
        secondaryWeaponMuzzle.setVisible(false);
        attachChild(secondaryWeaponMuzzle);

        mainWeaponMuzzle = new Sprite(62, -61, GraphicsFactory.mGfxMap.get("muzzle_flash.png"),
                getVertexBufferObjectManager());
        mainWeaponMuzzle.setScale(1.5f);
        mainWeaponMuzzle.setVisible(false);
        turretSprite.attachChild(mainWeaponMuzzle);
    }

    @Override
    protected void onManagedUpdate(float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);
        if (isMainWeaponFiring) {
            mainWeaponMuzzle.setVisible(true);
            isMainWeaponFiring = false;
        } else if (mainWeaponMuzzle.isVisible()) {
            mainWeaponMuzzle.setVisible(false);
        }

        if (isSecondaryWeaponFiring) {
            secondaryWeaponMuzzle.setVisible(true);
            isSecondaryWeaponFiring = false;
        } else if (secondaryWeaponMuzzle.isVisible()) {
            secondaryWeaponMuzzle.setVisible(false);
        }
    }

    @Override
    public void startFireAnimation(Weapon weapon) {
        if (weapon instanceof DeflectionWeapon) {
            isMainWeaponFiring = true;
        } else {
            isSecondaryWeaponFiring = true;
        }
    }

    public float getTurretSpriteRotation() {
        return turretSprite.getRotation();
    }

    public void setTurretSpriteRotation(float angle) {
        turretSprite.setRotation(angle);
    }

    public Sprite getTurretSprite() {
        return turretSprite;
    }

}
