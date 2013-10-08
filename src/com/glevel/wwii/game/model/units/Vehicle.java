package com.glevel.wwii.game.model.units;

import java.util.List;

public abstract class Vehicle extends Unit {

	public Vehicle(int name, int image, Experience experience,
			List<Weapon> weapons, int moveSpeed) {
		super(name, image, experience, weapons, moveSpeed);
	}

	private static enum VehicleType {
		light, tank
	}

	private VehicleType vehicleType;
	private int width;
	private int height;

}
