package com.glevel.wwii.game.model.units.weapons;

import com.glevel.wwii.game.model.units.Weapon;

public class Mortar extends Weapon {

    public Mortar(int name, int image, int apPower, int atPower, int range, int nbMagazines, int cadence,
            int magazineSize, int reloadSpeed, int shootSpeed) {
        super(name, image, apPower, atPower, range, nbMagazines, cadence, magazineSize, reloadSpeed, shootSpeed);
    }

}
