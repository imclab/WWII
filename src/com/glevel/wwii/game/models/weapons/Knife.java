package com.glevel.wwii.game.models.weapons;

import com.glevel.wwii.game.models.weapons.categories.Weapon;

public class Knife extends Weapon {

    /**
     * 
     */
    private static final long serialVersionUID = 3632389181937576479L;

    public Knife() {
        super(0, 0, 0, 0, 3, 1, 10000, 8, 2, 3, new int[] { 30, 0, 0, 0 });
    }

}
