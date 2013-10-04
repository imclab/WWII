package com.glevel.wwii.game.model.armies;

import java.util.ArrayList;
import java.util.List;

import com.glevel.wwii.game.model.GameElement;
import com.glevel.wwii.game.model.Order;

public abstract class Unit extends GameElement {

	public static enum InjuryState {
		none, injured, badlyInjured, dead
	}

	public static enum Experience {
		adhoc, normal, experimented, elite
	}

	public static enum CurrentAction {
		none, walk, run, shoot
	}

	public static enum Rank {
		ally, enemy, neutral
	}

	private InjuryState health = InjuryState.none;
	private int moveSpeed;
	private List<Weapon> weapons = new ArrayList<Weapon>();
	private Experience experience;

	private Order order;
	private CurrentAction currentAction;

	private int requisitionPrice;
	private boolean isAvailable;

	public InjuryState getHealth() {
		return health;
	}

	public void setHealth(InjuryState health) {
		this.health = health;
	}

	public int getMoveSpeed() {
		return moveSpeed;
	}

	public void setMoveSpeed(int moveSpeed) {
		this.moveSpeed = moveSpeed;
	}

	public List<Weapon> getWeapons() {
		return weapons;
	}

	public void setWeapons(List<Weapon> weapons) {
		this.weapons = weapons;
	}

	public Experience getExperience() {
		return experience;
	}

	public void setExperience(Experience experience) {
		this.experience = experience;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

	public CurrentAction getCurrentAction() {
		return currentAction;
	}

	public void setCurrentAction(CurrentAction currentAction) {
		this.currentAction = currentAction;
	}

	public int getRequisitionPrice() {
		return requisitionPrice;
	}

	public void setRequisitionPrice(int requisitionPrice) {
		this.requisitionPrice = requisitionPrice;
	}

	public boolean isAvailable() {
		return isAvailable;
	}

	public void setAvailable(boolean isAvailable) {
		this.isAvailable = isAvailable;
	}

	public int getFrags() {
		return frags;
	}

	public void setFrags(int frags) {
		this.frags = frags;
	}

	private int frags;
}
