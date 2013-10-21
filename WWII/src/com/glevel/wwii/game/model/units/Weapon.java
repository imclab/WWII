package com.glevel.wwii.game.model.units;

import com.glevel.wwii.R;

public class Weapon {

    private int name;
    private int image;
    private int apPower;
    private int atPower;
    private int range;
    private int ammoAmount;
    private int cadence;
    private int magazineSize;
    private int reloadSpeed;
    private int reloadCounter;// while > 0 there are ammo left, while < 0
                              // reloading
    private int shootSpeed;

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

    public int getMagazineSize() {
        return magazineSize;
    }

    public void setMagazineSize(int magazineSize) {
        this.magazineSize = magazineSize;
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

    public void setName(int name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
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

    public void setApPower(int apPower) {
        this.apPower = apPower;
    }

    public int getAtPower() {
        return atPower;
    }

    public void setAtPower(int atPower) {
        this.atPower = atPower;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getReloadSpeed() {
        return reloadSpeed;
    }

    public void setReloadSpeed(int reloadSpeed) {
        this.reloadSpeed = reloadSpeed;
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

    public void setShootSpeed(int shootSpeed) {
        this.shootSpeed = shootSpeed;
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
