package com.glevel.wwii.game.graphics;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.glevel.wwii.game.GraphicsFactory;
import com.glevel.wwii.game.InputManager;
import com.glevel.wwii.game.models.GameElement;
import com.glevel.wwii.game.models.weapons.categories.Weapon;

public class SoldierSprite extends UnitSprite {

	private transient Sprite mainWeaponMuzzle;
	private transient Sprite hideSprite;
	private transient boolean isFiring;

	public SoldierSprite(GameElement gameElement, InputManager inputManager, float pX, float pY, ITextureRegion pTextureRegion,
			VertexBufferObjectManager mVertexBufferObjectManager) {
		super(gameElement, inputManager, pX, pY, pTextureRegion, mVertexBufferObjectManager);
		addMuzzleFlashSprite();
		addHideSprite();
		setZIndex(30);
	}

	private void addHideSprite() {
		hideSprite = new HideSprite(GraphicsFactory.mGfxMap.get("hide.png"), getVertexBufferObjectManager());
		hideSprite.setVisible(false);
		attachChild(getHideSprite());

	}

	private void addMuzzleFlashSprite() {
		mainWeaponMuzzle = new Sprite(53, -50, GraphicsFactory.mGfxMap.get("muzzle_flash.png"), getVertexBufferObjectManager());
		mainWeaponMuzzle.setVisible(false);
		attachChild(mainWeaponMuzzle);
	}

	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);
		if (isFiring) {
			mainWeaponMuzzle.setVisible(true);
			isFiring = false;
		} else if (mainWeaponMuzzle.isVisible()) {
			mainWeaponMuzzle.setVisible(false);
		}
	}

	@Override
	public void startFireAnimation(Weapon weapon) {
		isFiring = true;
	}

	public Sprite getHideSprite() {
		return hideSprite;
	}

	@Override
	public void setRotation(float pRotation) {
		hideSprite.setRotation(-pRotation);
		super.setRotation(pRotation);
	}
}
