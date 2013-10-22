package com.glevel.wwii.game.model.units;

import com.glevel.wwii.R;

public class Weapon {

    private final int name;
    private final int image;
    private final int apPower;
    private final int atPower;
    private final int range;
    private final int magazineSize;
    private final int reloadSpeed;
    private final int shootSpeed;

    private int cadence;
    private int ammoAmount;
    private int reloadCounter;// while > 0 there are ammo left, while < 0
                              // reloading

    // price
    private static final int WEAPON_BASE_PRICE = 2;
    private static final int LONG_RANGE_THRESHOLD = 1000;
    private static final float LONG_RANGE_PRICE_MODIFIER = 1.5f;

    public Weapon(int name, int image, int apPower, int atPower, int range, int nbMagazines, int cadence,
            int magazineSize, int reloadSpeed, int shootSpeed) {
        this.name = name;
        this.image = image;
        this.apPower = apPower;
        this.atPower = atPower;
        this.range = range;
        this.ammoAmount = nbMagazines * magazineSize;
        this.cadence = cadence;
        this.magazineSize = magazineSize;
        this.reloadCounter = magazineSize;
        this.reloadSpeed = reloadSpeed;
        this.shootSpeed = shootSpeed;
    }

    public int getPrice() {
        int price = WEAPON_BASE_PRICE;
        price += 0.1 * (apPower + atPower) * ammoAmount / magazineSize;
        // long range
        if (range > LONG_RANGE_THRESHOLD) {
            price *= LONG_RANGE_PRICE_MODIFIER;
        }
        return price;
    }

    public int getMagazineSize() {
        return magazineSize;
    }

    public int getReloadCounter() {
        return reloadCounter;
    }

    public void setReloadCounter(int reloadCounter) {
        this.reloadCounter = reloadCounter;
    }

    public int getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

    public int getAmmoAmount() {
        return ammoAmount;
    }

    public void setAmmoAmount(int ammoAmount) {
        this.ammoAmount = ammoAmount;
    }

    public int getCadence() {
        return cadence;
    }

    public void setCadence(int cadence) {
        this.cadence = cadence;
    }

    public int getApPower() {
        return apPower;
    }

    public int getAtPower() {
        return atPower;
    }

    public int getRange() {
        return range;
    }

    public int getReloadSpeed() {
        return reloadSpeed;
    }

    public int getAPColorEfficiency() {
        return efficiencyValueToColor(apPower);
    }

    public int getATColorEfficiency() {
        return efficiencyValueToColor(atPower);
    }

    public int getShootSpeed() {
        return shootSpeed;
    }

    private int efficiencyValueToColor(int efficiency) {
        switch (efficiency) {
        case 1:
            return R.drawable.bg_unit_efficiency_grey;
        case 2:
            return R.drawable.bg_unit_efficiency_red;
        case 3:
            return R.drawable.bg_unit_efficiency_orange;
        case 4:
            return R.drawable.bg_unit_efficiency_yellow;
        case 5:
            return R.drawable.bg_unit_efficiency_green;
        default:
            return R.drawable.bg_unit_efficiency_black;
        }
    }

}
