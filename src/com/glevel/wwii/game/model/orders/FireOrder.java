package com.glevel.wwii.game.model.orders;

import com.glevel.wwii.game.model.Order;
import com.glevel.wwii.game.model.armies.Unit;

public class FireOrder extends Order {

	private int xDestination;
	private int yDestination;
	private Unit target;

	public FireOrder(Unit unit, Unit target) {
		this.unit = unit;
		this.target = target;
	}

	public FireOrder(Unit unit, int xDestination, int yDestination) {
		this.unit = unit;
		this.xDestination = xDestination;
		this.yDestination = yDestination;
	}

	public int getxDestination() {
		return xDestination;
	}

	public void setxDestination(int xDestination) {
		this.xDestination = xDestination;
	}

	public int getyDestination() {
		return yDestination;
	}

	public void setyDestination(int yDestination) {
		this.yDestination = yDestination;
	}

	public Unit getTarget() {
		return target;
	}

	public void setTarget(Unit target) {
		this.target = target;
	}

}
