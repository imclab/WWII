package com.glevel.wwii.game.graphics;

import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.glevel.wwii.game.GraphicsFactory;
import com.glevel.wwii.game.InputManager;
import com.glevel.wwii.game.models.GameElement;
import com.glevel.wwii.game.models.orders.HideOrder;
import com.glevel.wwii.game.models.orders.Order;
import com.glevel.wwii.game.models.weapons.categories.Weapon;

public class CannonSprite extends UnitSprite {

	private transient Sprite mainWeaponMuzzle;
	private transient Sprite hideSprite;
	private transient boolean isFiring;

	public CannonSprite(GameElement gameElement, InputManager inputManager, float pX, float pY,
			TiledTextureRegion pTextureRegion, VertexBufferObjectManager mVertexBufferObjectManager) {
		super(gameElement, inputManager, pX, pY, pTextureRegion, mVertexBufferObjectManager);
		addMuzzleFlashSprite();
		addHideSprite();
		setZIndex(30);
	}

	private void addHideSprite() {
		hideSprite = new HideSprite(GraphicsFactory.mGfxMap.get("hide.png"), getVertexBufferObjectManager());
		hideSprite.setVisible(false);
		attachChild(hideSprite);
	}

	private void addMuzzleFlashSprite() {
		mainWeaponMuzzle = new Sprite(42, -52, GraphicsFactory.mGfxMap.get("muzzle_flash.png"),
				getVertexBufferObjectManager());
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

	@Override
	public void setRotation(float pRotation) {
		hideSprite.setRotation(-pRotation);
		super.setRotation(pRotation);
	}

	public void setOrder(Order order) {
		hideSprite.setVisible(order instanceof HideOrder);
	}

}
