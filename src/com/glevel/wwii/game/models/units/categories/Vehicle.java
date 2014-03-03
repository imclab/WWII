package com.glevel.wwii.game.models.units.categories;

import java.util.List;

import org.andengine.extension.tmx.TMXTile;

import com.glevel.wwii.activities.GameActivity;
import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.logic.MapLogic;
import com.glevel.wwii.game.logic.pathfinding.AStar;
import com.glevel.wwii.game.logic.pathfinding.Node;
import com.glevel.wwii.game.models.Battle;
import com.glevel.wwii.game.models.map.Map;
import com.glevel.wwii.game.models.map.Tile;
import com.glevel.wwii.game.models.map.Tile.TerrainType;
import com.glevel.wwii.game.models.orders.DefendOrder;
import com.glevel.wwii.game.models.orders.MoveOrder;
import com.glevel.wwii.game.models.weapons.Turret;
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

    // movement
    private static final int REVERSE_THRESHOLD = 10 * GameUtils.PIXEL_BY_METER;// in
                                                                               // meters
    private static final float REVERSE_SPEED = 0.5f;
    private static final float ROTATION_SPEED = 0.6f;

    // fire
    private static final float MG_MAX_FIRE_ANGLE = 30.0f;

    private float nextX = -1.0f, nextY = -1.0f;

    protected static enum VehicleType {
        LIGHT, TANK
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
        case RECRUIT:
            price *= RECRUIT_PRICE_MODIFIER;
            break;
        case VETERAN:
            price *= VETERAN_PRICE_MODIFIER;
            break;
        case ELITE:
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
        if (tile.getTerrain() != null && tile.getTerrain() != TerrainType.field
                && tile.getTerrain() != TerrainType.bush) {
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

        if (path != null && path.size() > 1) {
            nextX = path.get(path.size() - 1).getX() * GameUtils.PIXEL_BY_TILE;
            nextY = path.get(path.size() - 1).getY() * GameUtils.PIXEL_BY_TILE;
        }
    }

    @Override
    public void move() {
        currentAction = Action.MOVING;
        MoveOrder moveOrder = (MoveOrder) order;

        if (nextX >= 0) {

            // cannot rotate and move at the same time
            RotationStatus rotationStatus = updateUnitRotation(moveOrder.getXDestination(), moveOrder.getYDestination());
            if (rotationStatus == RotationStatus.ROTATING) {
                return;
            }

            float dx = moveOrder.getXDestination() - sprite.getX();
            float dy = moveOrder.getYDestination() - sprite.getY();
            double angle = Math.atan(dy / dx);
            float dd = moveSpeed * 10 * 0.04f * getUnitSpeed()
                    * (rotationStatus == RotationStatus.REVERSE ? REVERSE_SPEED : 1.0f);

            boolean hasArrived = false;
            float distanceLeft = MapLogic.getDistanceBetween(moveOrder.getXDestination(), moveOrder.getYDestination(),
                    sprite.getX(), sprite.getY());
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

    @Override
    protected RotationStatus updateUnitRotation(float xDestination, float yDestination) {
        RotationStatus rotationStatus = RotationStatus.NONE;

        float dx = xDestination - sprite.getX();
        float dy = yDestination - sprite.getY();
        double finalAngle = Math.atan(dy / dx) * 180 / Math.PI;
        if (dx > 0) {
            finalAngle += 90;
        } else {
            finalAngle -= 90;
        }
        double dTau = finalAngle - sprite.getRotation();

        // reverse if not far !
        if (Math.abs(dTau) > 135.0f
                && MapLogic.getDistanceBetween(xDestination, yDestination, sprite.getX(), sprite.getY()) < REVERSE_THRESHOLD) {
            if (dx > 0) {
                finalAngle -= 180;
            } else {
                finalAngle += 180;
            }
            dTau = finalAngle - sprite.getRotation();
            rotationStatus = RotationStatus.REVERSE;
        }

        double rotationStep = 0;
        if (dTau > 0) {
            rotationStep = Math.min(dTau, ROTATION_SPEED);
        } else if (dTau < 0) {
            rotationStep = Math.max(dTau, -ROTATION_SPEED);
        }
        sprite.setRotation((float) (sprite.getRotation() + rotationStep));

        return Math.abs(finalAngle - sprite.getRotation()) < ROTATION_SPEED ? rotationStatus : RotationStatus.ROTATING;
    }

    @Override
    public void fire(Battle battle, Unit target) {

        if (target.isDead() || !MapLogic.canSee(battle.getMap(), this, target)) {
            // if target is dead or is not visible anymore, stop to shoot
            order = new DefendOrder();
            return;
        }

        // get most suitable weapon
        boolean canShoot = false;
        for (Weapon weapon : getWeapons()) {
            if (weapon.canUseWeapon(this, target, MapLogic.canSee(battle.getMap(), this, target))) {
                fireWithWeapon(battle, weapon, target);
                canShoot = true;
            }
        }

        if (!canShoot) {
            // no weapon available for this fire order
            this.order = new DefendOrder();
        }
    }

    public boolean fireWhileMoving(Battle battle, Unit target) {

        if (target.isDead() || !MapLogic.canSee(battle.getMap(), this, target)) {
            // if target is dead or is not visible anymore, stop to shoot
            return false;
        }

        // get most suitable weapon
        Weapon weapon = getBestWeapon(battle, target);
        if (weapon != null) {
            if (weapon instanceof Turret && target instanceof Vehicle) {
                // canons cannot shoot while moving but can rotate
                Turret turret = (Turret) weapon;
                turret.rotateTurret(sprite, target.getSprite().getX(), target.getSprite().getY());
                return true;
            } else if (Math.abs(MapLogic.getAngle(sprite, target.getSprite().getX(), target.getSprite().getY())) >= MG_MAX_FIRE_ANGLE) {
                return false;
            }

            if (weapon.getReloadCounter() > 0) {
                if (aimCounter == 0) {
                    aimCounter = -10;
                    // aiming
                    this.currentAction = Action.AIMING;
                } else if (aimCounter < 0) {
                    aimCounter++;
                    if (aimCounter == 0) {
                        aimCounter = weapon.getCadence();
                    }
                    // aiming
                    this.currentAction = Action.AIMING;
                } else if (GameActivity.gameCounter % (11 - weapon.getShootSpeed()) == 0) {
                    // firing !!!

                    // add muzzle flash sprite
                    sprite.startFireAnimation(weapon);

                    weapon.setAmmoAmount(weapon.getAmmoAmount() - 1);
                    weapon.setReloadCounter(weapon.getReloadCounter() - 1);
                    aimCounter--;
                    currentAction = Action.FIRING;

                    // if not a lot of ammo, more aiming !
                    if (weapon.getAmmoAmount() == weapon.getMagazineSize() * 2) {
                        weapon.setCadence(Math.max(1, weapon.getCadence() / 2));
                    }

                    // increase target panic
                    target.getShots(this, battle.getMap());

                    if (!target.isDead()) {// prevent the multiple frags bug

                        weapon.resolveFireShot(battle, this, target);

                        if (target.isDead()) {
                            target.died(battle);
                            killedSomeone();
                        }
                    }
                }
            } else if (weapon.getReloadCounter() == 0) {
                // need to reload
                weapon.setReloadCounter(-weapon.getReloadSpeed());
            } else {
                // reloading
                if (GameActivity.gameCounter % 12 == 0) {
                    weapon.setReloadCounter(weapon.getReloadCounter() + 1);
                    if (weapon.getReloadCounter() == 0) {
                        // reloading is over
                        weapon.setReloadCounter(weapon.getMagazineSize());
                    }
                }
            }
            return true;
        }
        return false;
    }

}
