package com.glevel.wwii.game.model.units;

import java.util.List;

import com.glevel.wwii.R;
import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.model.GameElement;
import com.glevel.wwii.game.model.orders.Order;

public abstract class Unit extends GameElement {

	protected final ArmiesData army;
	private final int image;
	private final int moveSpeed;
	private List<Weapon> weapons;
	protected Experience experience;

	private int requisitionPrice;
	private InjuryState health;
	private int frags;
	private boolean isAvailable;
	private Order order;
	private CurrentAction currentAction;

	public Unit(ArmiesData army, int name, int image, Experience experience,
			List<Weapon> weapons, int moveSpeed) {
		super(name);
		this.army = army;
		this.image = image;
		this.experience = experience;
		this.weapons = weapons;
		this.moveSpeed = moveSpeed;
		this.health = InjuryState.none;
		this.frags = 0;
		this.requisitionPrice = calculateUnitPrice();
	}

	private int calculateUnitPrice() {
		int basePrice = 5;
		switch (experience) {
		case recruit:
			basePrice *= 0.5;
			break;
		case elite:
			basePrice *= 2.5;
			break;
		}
		// TODO
		return basePrice;
	}

	public static enum InjuryState {
		none, injured, badlyInjured, dead
	}

	public static enum Experience {
		recruit(R.color.recruit), veteran(R.color.veteran), elite(R.color.elite);

		private final int color;

		private Experience(int color) {
			this.color = color;
		}

		public int getColor() {
			return color;
		}

	}

	public static enum CurrentAction {
		none, walk, run, shoot
	}

	public static enum Rank {
		ally, enemy, neutral
	}

	public InjuryState getHealth() {
		return health;
	}

	public void setHealth(InjuryState health) {
		this.health = health;
	}

	public int getMoveSpeed() {
		return moveSpeed;
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

	public int getImage() {
		return image;
	}

	public ArmiesData getArmy() {
		return army;
	}

	public int getRealSellPrice(boolean isSelling) {
		if (isSelling) {
			return (int) (requisitionPrice * GameUtils.SELL_PRICE_FACTOR);
		} else {
			return requisitionPrice;
		}
	}

	/**
	 * Create a new instance of Unit. Used when we buy a unit.
	 * 
	 * @param army
	 * @return
	 */
	public Unit copy() {
		Unit unit = null;
		if (this instanceof Soldier) {
			unit = new Soldier(army, name, image, experience, weapons,
					moveSpeed);
		} else if (this instanceof Tank) {
			Vehicle vehicle = (Vehicle) this;
			unit = new Tank(army, name, image, experience, weapons, moveSpeed,
					vehicle.getArmor());
		}
		return unit;
	}

}
