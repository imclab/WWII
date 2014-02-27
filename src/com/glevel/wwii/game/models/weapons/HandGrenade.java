package com.glevel.wwii.game.models.weapons;

import com.glevel.wwii.game.models.weapons.categories.IndirectWeapon;

public class HandGrenade extends IndirectWeapon {

    /**
     * 
     */
    private static final long serialVersionUID = 2356944182225959652L;

    public HandGrenade(int name, int image, int apPower, int atPower, int range, int nbMagazines, int cadence,
            int magazineSize, int reloadSpeed, int shootSpeed, int explosionSize) {
        super(name, image, apPower, atPower, range, nbMagazines, cadence, magazineSize, reloadSpeed, shootSpeed,
                explosionSize);
        // TODO Auto-generated constructor stub
    }

}
