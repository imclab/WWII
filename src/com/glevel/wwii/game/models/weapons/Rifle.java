package com.glevel.wwii.game.models.weapons;

import com.glevel.wwii.game.models.weapons.categories.Weapon;


public class Rifle extends Weapon {

    /**
     * 
     */
    private static final long serialVersionUID = 8078864652145859378L;

    public Rifle(int name, int image, int apPower, int atPower, int range, int nbMagazines, int cadence,
            int magazineSize, int reloadSpeed, int shootSpeed) {
        super(name, image, apPower, atPower, range, nbMagazines, cadence, magazineSize, reloadSpeed, shootSpeed);
    }

}
