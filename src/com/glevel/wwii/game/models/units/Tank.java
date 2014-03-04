package com.glevel.wwii.game.models.units;

import java.util.List;

import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.models.units.categories.Vehicle;
import com.glevel.wwii.game.models.weapons.categories.Weapon;

public class Tank extends Vehicle {

    /**
     * 
     */
    private static final long serialVersionUID = -4095890968516700014L;
    private static final int TANK_VIRTUAL_WIDTH = 2, TANK_VIRTUAL_HEIGHT = 4;

    public Tank(ArmiesData army, int name, int image, Experience experience, List<Weapon> weapons, int moveSpeed,
            int armor, String spriteName, float spriteScale) {
        super(army, name, image, experience, weapons, moveSpeed, VehicleType.TANK, armor, TANK_VIRTUAL_WIDTH,
                TANK_VIRTUAL_HEIGHT, spriteName, spriteScale);
    }

    @Override
    public float getUnitSpeed() {

        // depends on health
        float healthFactor = 1 - getHealth().ordinal() * 0.25f;

        // depends on terrain
        switch (getTilePosition().getGround()) {
        case concrete:
            return 1.0f * healthFactor;
        case grass:
            return 1.0f * healthFactor;
        case mud:
            return 0.7f * healthFactor;
        case water:
            return 0.1f * healthFactor;
        }

        return 0;
    }

}
