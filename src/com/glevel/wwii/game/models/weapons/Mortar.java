package com.glevel.wwii.game.models.weapons;

import com.glevel.wwii.game.models.weapons.categories.IndirectWeapon;

public class Mortar extends IndirectWeapon {

    /**
     * 
     */
    private static final long serialVersionUID = 8787512407777567492L;

    public Mortar(int name, int image, int apPower, int atPower, int range, int nbMagazines, int cadence,
            int magazineSize, int reloadSpeed, int shootSpeed, int explosionSize) {
        super(name, image, apPower, atPower, range, nbMagazines, cadence, magazineSize, reloadSpeed, shootSpeed,
                explosionSize);
    }

}
