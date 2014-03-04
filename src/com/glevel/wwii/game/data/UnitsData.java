package com.glevel.wwii.game.data;

import java.util.ArrayList;
import java.util.List;

import com.glevel.wwii.R;
import com.glevel.wwii.game.models.units.Soldier;
import com.glevel.wwii.game.models.units.Tank;
import com.glevel.wwii.game.models.units.categories.Unit;
import com.glevel.wwii.game.models.units.categories.Unit.Experience;
import com.glevel.wwii.game.models.weapons.categories.Weapon;

public class UnitsData {

    private static final float SOLDIER_SCALE = 0.5f, TANK_SCALE = 1.0f;

    public static List<Unit> getAllUnits(ArmiesData army) {
        List<Unit> lstUnits = new ArrayList<Unit>();
        switch (army) {
        case GERMANY:
            lstUnits.add(buildRifleMan(army, Experience.RECRUIT));
            lstUnits.add(buildRifleMan(army, Experience.VETERAN));
            lstUnits.add(buildScout(army, Experience.VETERAN));
            lstUnits.add(buildScout(army, Experience.ELITE));
            lstUnits.add(buildHMG(army, Experience.VETERAN));
            lstUnits.add(buildHMG(army, Experience.ELITE));
            lstUnits.add(buildBazooka(army, Experience.RECRUIT));
            lstUnits.add(buildBazooka(army, Experience.VETERAN));
            lstUnits.add(buildBazooka(army, Experience.ELITE));
            lstUnits.add(buildMortar(army, Experience.VETERAN));
            lstUnits.add(buildATCannon(army, Experience.VETERAN));
            lstUnits.add(buildATCannon(army, Experience.ELITE));
            lstUnits.add(buildPantherG(army, Experience.VETERAN));
            lstUnits.add(buildPantherG(army, Experience.ELITE));
            break;
        case USA:
            lstUnits.add(buildRifleMan(army, Experience.RECRUIT));
            lstUnits.add(buildRifleMan(army, Experience.VETERAN));
            lstUnits.add(buildScout(army, Experience.VETERAN));
            lstUnits.add(buildScout(army, Experience.ELITE));
            lstUnits.add(buildHMG(army, Experience.VETERAN));
            lstUnits.add(buildHMG(army, Experience.ELITE));
            lstUnits.add(buildBazooka(army, Experience.RECRUIT));
            lstUnits.add(buildBazooka(army, Experience.VETERAN));
            lstUnits.add(buildMortar(army, Experience.VETERAN));
            lstUnits.add(buildATCannon(army, Experience.VETERAN));
            lstUnits.add(buildATCannon(army, Experience.ELITE));
            lstUnits.add(buildShermanM4A1(army, Experience.VETERAN));
            break;
        }
        return lstUnits;
    }

    public static Unit buildRifleMan(ArmiesData army, Experience experience) {
        List<Weapon> weapons = new ArrayList<Weapon>();
        switch (army) {
        case GERMANY:
            weapons.add(WeaponsData.buildMauserG98());
            weapons.add(WeaponsData.buildHandGrenades(army));
            return new Soldier(army, R.string.rifleman, R.drawable.ic_launcher, experience, weapons, 3, "soldier.png",
                    SOLDIER_SCALE);
        case USA:
            weapons.add(WeaponsData.buildGarandM1());
            weapons.add(WeaponsData.buildHandGrenades(army));
            return new Soldier(army, R.string.rifleman, R.drawable.ic_launcher, experience, weapons, 3, "soldier.png",
                    SOLDIER_SCALE);
        }
        return null;
    }

    public static Unit buildScout(ArmiesData army, Experience experience) {
        List<Weapon> weapons = new ArrayList<Weapon>();
        switch (army) {
        case GERMANY:
            weapons.add(WeaponsData.buildMP40());
            weapons.add(WeaponsData.buildPanzerfaust());
            return new Soldier(army, R.string.scout, R.drawable.ic_launcher, experience, weapons, 3, "soldier.png",
                    SOLDIER_SCALE);
        case USA:
            weapons.add(WeaponsData.buildThompson());
            weapons.add(WeaponsData.buildHandGrenades(army));
            return new Soldier(army, R.string.scout, R.drawable.ic_launcher, experience, weapons, 3, "soldier.png",
                    SOLDIER_SCALE);
        }
        return null;
    }

    public static Unit buildHMG(ArmiesData army, Experience experience) {
        List<Weapon> weapons = new ArrayList<Weapon>();
        switch (army) {
        case GERMANY:
            weapons.add(WeaponsData.buildMG42());
            return new Soldier(army, R.string.hmg, R.drawable.ic_launcher, experience, weapons, 1, "soldier.png",
                    SOLDIER_SCALE);
        case USA:
            weapons.add(WeaponsData.buildBrowningM2());
            return new Soldier(army, R.string.hmg, R.drawable.ic_launcher, experience, weapons, 1, "soldier.png",
                    SOLDIER_SCALE);
        }
        return null;
    }

    public static Unit buildBazooka(ArmiesData army, Experience experience) {
        List<Weapon> weapons = new ArrayList<Weapon>();
        switch (army) {
        case GERMANY:
            weapons.add(WeaponsData.buildPanzerschreck());
            return new Soldier(army, R.string.panzerschreck, R.drawable.ic_launcher, experience, weapons, 2,
                    "soldier.png", SOLDIER_SCALE);
        case USA:
            weapons.add(WeaponsData.buildBazooka());
            return new Soldier(army, R.string.bazooka, R.drawable.ic_launcher, experience, weapons, 2, "soldier.png",
                    SOLDIER_SCALE);
        }
        return null;
    }

    public static Unit buildMortar(ArmiesData army, Experience experience) {
        List<Weapon> weapons = new ArrayList<Weapon>();
        switch (army) {
        case GERMANY:
            weapons.add(WeaponsData.buildMortar81());
            return new Soldier(army, R.string.mortar, R.drawable.ic_launcher, experience, weapons, 1, "soldier.png",
                    SOLDIER_SCALE);
        case USA:
            weapons.add(WeaponsData.buildMortar50());
            return new Soldier(army, R.string.mortar, R.drawable.ic_launcher, experience, weapons, 1, "soldier.png",
                    SOLDIER_SCALE);
        }
        return null;
    }

    public static Unit buildATCannon(ArmiesData army, Experience experience) {
        List<Weapon> weapons = new ArrayList<Weapon>();
        switch (army) {
        case GERMANY:
            weapons.add(WeaponsData.buildPak43());
            return new Soldier(army, R.string.at_cannon, R.drawable.ic_launcher, experience, weapons, 0,
                    "german_at_50.png", 1.0f);
        case USA:
            weapons.add(WeaponsData.buildCannon75(10));
            return new Soldier(army, R.string.at_cannon, R.drawable.ic_launcher, experience, weapons, 0,
                    "german_at_50.png", 0.8f);
        }
        return null;
    }

    public static Unit buildShermanM4A1(ArmiesData army, Experience experience) {
        List<Weapon> weapons = new ArrayList<Weapon>();
        weapons.add(WeaponsData.buildCannon75(5));
        weapons.add(WeaponsData.buildBrowningM2());
        return new Tank(army, R.string.shermanM4A1, R.drawable.ic_launcher, experience, weapons, 3, 3, "panzeriv.png",
                TANK_SCALE);
    }

    public static Unit buildPantherG(ArmiesData army, Experience experience) {
        List<Weapon> weapons = new ArrayList<Weapon>();
        weapons.add(WeaponsData.buildCannon75(8));
        weapons.add(WeaponsData.buildMG42());
        return new Tank(army, R.string.pantherG, R.drawable.ic_launcher, experience, weapons, 3, 3, "panzeriv.png",
                TANK_SCALE);
    }

}
