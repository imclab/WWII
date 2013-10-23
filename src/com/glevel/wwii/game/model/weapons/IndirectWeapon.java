package com.glevel.wwii.game.model.weapons;

public class IndirectWeapon extends DeflectionWeapon {

    public IndirectWeapon(int name, int image, int apPower, int atPower, int range, int nbMagazines, int cadence,
            int magazineSize, int reloadSpeed, int shootSpeed, int explosionSize) {
        super(name, image, apPower, atPower, range, nbMagazines, cadence, magazineSize, reloadSpeed, shootSpeed,
                explosionSize);
    }

}
