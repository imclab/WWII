package com.glevel.wwii.game.logic;

import com.glevel.wwii.game.model.units.Soldier;
import com.glevel.wwii.game.model.units.Unit;
import com.glevel.wwii.game.model.units.Weapon;

public class FightLogic {

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
        // TODO
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
        // TODO

        Weapon weapon = chooseBestWeapon(shooter, target, range);
        if (weapon != null) {

        }
    }

    public static final int getWeaponPower(Weapon weapon, Unit target, int range) {
        // TODO
        if (target instanceof Soldier) {
            return weapon.getApPower();
        } else {
            return weapon.getAtPower();
        }
    }

    public static final int[][] HIT_CHANCES = { { 70, 40, 20, 5 }, { 80, 55, 35, 10 }, { 90, 70, 50, 20 } };

    public static int distanceToRangeCategory(float distance) {
        if (distance < 50) {
            return 0;
        } else if (distance < 100) {
            return 1;
        } else if (distance < 200) {
            return 2;
        } else {
            return 3;
        }
    }

}
