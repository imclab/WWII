package com.glevel.wwii.game.logic;

import com.glevel.wwii.game.model.armies.Soldier;
import com.glevel.wwii.game.model.armies.Unit;
import com.glevel.wwii.game.model.armies.Weapon;

public class FightLogic {

	public static final int RANGE_FACTOR = 2;
	public static final int RANGE_CATEGORY_NUMBER = 5;

	/**
	 * 
	 * @param shooter
	 * @param target
	 * @param range
	 * @return null if can't reach target or no ammo
	 * @return most efficient weapon
	 */
	public static Weapon chooseBestWeapon(Unit shooter, Unit target, int range) {
		Weapon bestWeapon = null;
		int bestPower = 0;
		// check each shooter's weapon
		for (Weapon weapon : shooter.getWeapons()) {
			int weaponPower = getWeaponPower(weapon, target, range);
			// if this weapon has ammo left and it is the most efficient against
			// target
			if (weapon.getAmmoAmount() > 0 && weaponPower > bestPower) {
				bestPower = weaponPower;
				bestWeapon = weapon;
			}
		}
		return bestWeapon;
	}

	/**
	 * Fire action
	 * 
	 * @param shooter
	 * @param target
	 * @param range
	 */
	public static void fire(Unit shooter, Unit target, int range) {
		// unit chooses its most efficient weapon
		Weapon weapon = chooseBestWeapon(shooter, target, range);
		if (weapon != null) {
			// check cadence delay
			if (weapon.getCurrentCadence() == 0) {
				// remove ammo
				weapon.setAmmoAmount(weapon.getAmmoAmount() - 1);

				// reset cadence delay
				weapon.setCurrentCadence(weapon.getCadence());
				
			} else {
				weapon.setCurrentCadence(weapon.getCurrentCadence() - 1);
			}
		}
	}

	public static final int getWeaponPower(Weapon weapon, Unit target, int range) {
		int index = Math.min(RANGE_CATEGORY_NUMBER, range / RANGE_FACTOR);
		if (target instanceof Soldier) {
			return weapon.getApPower()[index];
		} else {
			return weapon.getAtPower()[index];
		}
	}

}
