package com.glevel.wwii.game.model.units;

import java.util.List;

import android.graphics.Canvas;

public class Tank extends Vehicle {

	public Tank(int name, int image, Experience experience,
			List<Weapon> weapons, int moveSpeed) {
		super(name, image, experience, weapons, moveSpeed);
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub

	}

}
