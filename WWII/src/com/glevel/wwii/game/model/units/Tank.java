package com.glevel.wwii.game.model.units;

import java.util.List;

import com.glevel.wwii.game.data.ArmiesData;

public class Tank extends Vehicle {

    private static final int TANK_VIRTUAL_WIDTH = 2, TANK_VIRTUAL_HEIGHT = 4;

    public Tank(ArmiesData army, int name, int image, Experience experience, List<Weapon> weapons, int moveSpeed,
            int armor) {
        super(army, name, image, experience, weapons, moveSpeed, VehicleType.tank, armor, TANK_VIRTUAL_WIDTH,
                TANK_VIRTUAL_HEIGHT);
    }

}
