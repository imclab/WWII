package com.glevel.wwii.game.data;

import com.glevel.wwii.R;
import com.glevel.wwii.game.model.units.Weapon;

public class WeaponsData {

    public static Weapon buildGarandM1() {
        return new Weapon(R.string.garandM1, R.drawable.ic_garand, 2, 0, 400, 8, 1, 8, 2, 2);
    }

    public static Weapon buildMauserG98() {
        return new Weapon(R.string.mauser, R.drawable.ic_mauser, 2, 0, 400, 8, 1, 5, 3, 2);
    }

    public static Weapon buildThompson() {
        return new Weapon(R.string.thompson, R.drawable.ic_thompson, 3, 0, 50, 6, 5, 30, 2, 10);
    }

    public static Weapon buildMP40() {
        return new Weapon(R.string.mp40, R.drawable.ic_mp40, 4, 0, 100, 6, 4, 32, 2, 10);
    }

    public static Weapon buildBrowningM2() {
        return new Weapon(R.string.browningM2, R.drawable.ic_browning_m2, 4, 1, 1200, 5, 10, 100, 5, 10);
    }

    public static Weapon buildMG42() {
        return new Weapon(R.string.mg42, R.drawable.ic_mg42, 5, 1, 1200, 8, 8, 80, 5, 10);
    }

    public static Weapon buildMortar50() {
        return new Weapon(R.string.mortar50, R.drawable.ic_mortar, 3, 1, 1500, 24, 1, 1, 2, 1);
    }

    public static Weapon buildMortar81() {
        return new Weapon(R.string.mortar81, R.drawable.ic_mortar, 4, 2, 1500, 24, 1, 1, 2, 1);
    }

    public static Weapon buildBazooka() {
        return new Weapon(R.string.bazookaM1A, R.drawable.ic_bazooka, 1, 3, 140, 16, 1, 1, 4, 1);
    }

    public static Weapon buildPanzerschreck() {
        return new Weapon(R.string.panzerschreck, R.drawable.ic_panzerschreck, 1, 4, 220, 16, 1, 1, 4, 1);
    }

    public static Weapon buildPanzerfaust() {
        return new Weapon(R.string.panzerfaust, R.drawable.ic_panzerfaust, 1, 3, 60, 2, 1, 1, 4, 1);
    }

    public static Weapon buildCannon75() {
        return new Weapon(R.string.cannon75, R.drawable.ic_cannon, 3, 5, 800, 30, 1, 1, 3, 1);
    }

    public static Weapon buildPak43() {
        return new Weapon(R.string.pak43, R.drawable.ic_cannon, 3, 5, 800, 30, 1, 1, 3, 1);
    }

    public static Weapon buildHandGrenades(ArmiesData army) {
        int image;
        if (army == ArmiesData.GERMANY) {
            image = R.drawable.ic_grenade_ger;
        } else {
            image = R.drawable.ic_grenade_usa;
        }
        return new Weapon(R.string.grenade, image, 5, 1, 30, 2, 1, 1, 1, 1);
    }

}
