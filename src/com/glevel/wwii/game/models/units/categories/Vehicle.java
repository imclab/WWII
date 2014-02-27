package com.glevel.wwii.game.models.units.categories;

import java.util.List;

import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.models.weapons.categories.Weapon;

public abstract class Vehicle extends Unit {

    /**
     * 
     */
    private static final long serialVersionUID = -110116920924422447L;

    // price
    private static final int VEHICLE_BASE_PRICE = 30;
    private static final float RECRUIT_PRICE_MODIFIER = 0.7f;
    private static final float VETERAN_PRICE_MODIFIER = 1.0f;
    private static final float ELITE_PRICE_MODIFIER = 1.5f;

    protected static enum VehicleType {
        light, tank
    }

    public Vehicle(ArmiesData army, int name, int image, Experience experience, List<Weapon> weapons, int moveSpeed,
            VehicleType type, int armor, int width, int height, String spriteName, float spriteScale) {
        super(army, name, image, experience, weapons, moveSpeed, spriteName, spriteScale);
        this.vehicleType = type;
        this.armor = armor;
        this.width = width;
        this.height = height;
    }

    private final VehicleType vehicleType;
    private final int width;
    private final int height;
    protected int armor;

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public int getPrice() {
        int price = VEHICLE_BASE_PRICE;

        // add weapons price
        for (Weapon weapon : getWeapons()) {
            price += weapon.getPrice();
        }

        // experience modifier
        switch (getExperience()) {
        case recruit:
            price *= RECRUIT_PRICE_MODIFIER;
            break;
        case veteran:
            price *= VETERAN_PRICE_MODIFIER;
            break;
        case elite:
            price *= ELITE_PRICE_MODIFIER;
            break;
        }

        // armor + movement speed modifiers
        price += (armor + moveSpeed) * 5;

        return price;
    }

    @Override
    public float getUnitTerrainProtection() {
        return 1.0f;
    }

}
