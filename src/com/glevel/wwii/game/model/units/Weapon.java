package com.glevel.wwii.game.model.units;

public class Weapon {

	private String name;
	private int ammoAmount;
	private int[] apPower;
	private int[] atPower;
	private int cadence;
	private int currentCadence;

	public int getCurrentCadence() {
		return currentCadence;
	}

	public void setCurrentCadence(int currentCadence) {
		this.currentCadence = currentCadence;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAmmoAmount() {
		return ammoAmount;
	}

	public void setAmmoAmount(int ammoAmount) {
		this.ammoAmount = ammoAmount;
	}

	public int[] getApPower() {
		return apPower;
	}

	public void setApPower(int[] apPower) {
		this.apPower = apPower;
	}

	public int[] getAtPower() {
		return atPower;
	}

	public void setAtPower(int[] atPower) {
		this.atPower = atPower;
	}

	public int getCadence() {
		return cadence;
	}

	public void setCadence(int cadence) {
		this.cadence = cadence;
	}
}
