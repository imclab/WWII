package com.glevel.wwii.game.models.units.categories;

import java.util.List;

import org.andengine.extension.tmx.TMXTile;

import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.logic.MapLogic;
import com.glevel.wwii.game.logic.pathfinding.AStar;
import com.glevel.wwii.game.logic.pathfinding.Node;
import com.glevel.wwii.game.models.map.Map;
import com.glevel.wwii.game.models.map.Tile;
import com.glevel.wwii.game.models.map.Tile.TerrainType;
import com.glevel.wwii.game.models.orders.MoveOrder;
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

    private float nextX = -1.0f, nextY = -1.0f;

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

    @Override
    public float getUnitSpeed() {

        // depends on health
        float healthFactor = 1 - getHealth().ordinal() * 0.25f;

        // depends on terrain
        switch (getTilePosition().getGround()) {
        case concrete:
            return 1.0f * healthFactor;
        case grass:
            return 0.7f * healthFactor;
        case mud:
            return 0.4f * healthFactor;
        case water:
            return 0.1f * healthFactor;
        }

        return 0;
    }

    @Override
    public boolean canMoveIn(Node node) {
        Tile tile = (Tile) node;
        if (tile.getTerrain() != null && tile.getTerrain() != TerrainType.bush && tile.getTerrain() != TerrainType.bush) {
            return false;
        }

        return super.canMoveIn(tile);
    }

    @Override
    public void updateMovementPath(Map map) {
        MoveOrder moveOrder = (MoveOrder) order;

        TMXTile tmxTile = map.getTmxLayer().getTMXTileAt(moveOrder.getXDestination(), moveOrder.getYDestination());
        Tile destinationTile = map.getTiles()[tmxTile.getTileRow()][tmxTile.getTileColumn()];
        tmxTile = map.getTmxLayer().getTMXTileAt(sprite.getX(), sprite.getY());
        Tile originTile = map.getTiles()[tmxTile.getTileRow()][tmxTile.getTileColumn()];
        List<Tile> path = new AStar<Tile>().search(map.getTiles(), originTile, destinationTile, true, this, 5);

        nextX = path.get(path.size() - 1).getX() * GameUtils.PIXEL_BY_TILE;
        nextY = path.get(path.size() - 1).getY() * GameUtils.PIXEL_BY_TILE;
    }

    @Override
    public void move() {
        currentAction = Action.moving;
        MoveOrder moveOrder = (MoveOrder) order;

        if (nextX >= 0) {

            // cannot rotate and move at the same time
            boolean rotateOK = rotate(moveOrder.getXDestination(), moveOrder.getYDestination());
            if (!rotateOK) {
                return;
            }

            float dx = moveOrder.getXDestination() - sprite.getX();
            float dy = moveOrder.getYDestination() - sprite.getY();
            double angle = Math.atan(dy / dx);
            float dd = moveSpeed * 10 * 0.04f * getUnitSpeed();

            boolean hasArrived = false;
            float distanceLeft = (float) Math.sqrt(Math.pow(moveOrder.getXDestination() - sprite.getX(), 2)
                    + Math.pow(moveOrder.getYDestination() - sprite.getY(), 2));
            if (distanceLeft < dd) {
                hasArrived = true;
                dd = distanceLeft;
            }

            float[] newPosition = MapLogic.getCoordinatesAfterTranslation(sprite.getX(), sprite.getY(), dd, angle,
                    dx > 0);
            sprite.setPosition(newPosition[0], newPosition[1]);

            if (hasArrived) {
                order = null;
            }
        }
    }

    private boolean rotate(float xDestination, float yDestination) {
        float dx = xDestination - sprite.getX();
        float dy = yDestination - sprite.getY();
        double finalAngle = Math.atan(dy / dx) * 180 / Math.PI;
        if (dx > 0) {
            finalAngle += 90;
        } else {
            finalAngle -= 90;
        }
        double dTau = finalAngle - sprite.getRotation();
        double rotationStep = 0;
        if (dTau > 0) {
            rotationStep = Math.min(dTau, 0.6);
        } else if (dTau < 0) {
            rotationStep = Math.max(dTau, -0.6);
        }
        sprite.setRotation((float) (sprite.getRotation() + rotationStep));

        return Math.abs(finalAngle - sprite.getRotation()) < 0.6f;
    }

}
