package com.glevel.wwii.game.model.armies;

public abstract class Vehicle extends Unit {

	private static enum VehicleType {
		light, tank
	}

	private VehicleType vehicleType;
	private int width;
	private int height;

}
