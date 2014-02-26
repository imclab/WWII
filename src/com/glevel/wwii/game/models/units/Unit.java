package com.glevel.wwii.game.models.units;

import java.util.List;

import com.glevel.wwii.R;
import com.glevel.wwii.activities.GameActivity;
import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.models.Battle;
import com.glevel.wwii.game.models.GameElement;
import com.glevel.wwii.game.models.map.Map;
import com.glevel.wwii.game.models.orders.DefendOrder;
import com.glevel.wwii.game.models.orders.FireOrder;
import com.glevel.wwii.game.models.orders.MoveOrder;
import com.glevel.wwii.game.models.orders.Order;
import com.glevel.wwii.game.models.weapons.TurretWeapon;
import com.glevel.wwii.game.models.weapons.Weapon;

public abstract class Unit extends GameElement {

    /**
     * 
     */
    private static final long serialVersionUID = -1514358997270651189L;

    // private static final float DEFEND_ORDER_AMBUSH_DISTANCE = 300;

    protected final ArmiesData army;
    private final int image;
    private final int moveSpeed;
    private List<Weapon> weapons;
    protected Experience experience;
    private String spriteName;
    private float spriteScale;

    private InjuryState health;
    private int frags;
    private boolean isAvailable;
    private Order order;
    private Action currentAction;
    private int panic;
    private int aimCounter = 0;

    public Unit(ArmiesData army, int name, int image, Experience experience, List<Weapon> weapons, int moveSpeed,
            String spriteName, float spriteScale) {
        super(name, spriteName, spriteScale);
        this.army = army;
        this.image = image;
        this.experience = experience;
        this.weapons = weapons;
        this.moveSpeed = moveSpeed;
        this.health = InjuryState.none;
        this.currentAction = Action.waiting;
        this.setPanic(0);
        this.frags = 0;
        this.spriteName = spriteName;
        this.spriteScale = spriteScale;
    }

    protected abstract float getUnitSpeed();

    public abstract float getUnitTerrainProtection();

    protected abstract int getUnitPrice();

    public static enum InjuryState {
        none(R.color.green), injured(R.color.yellow), badlyInjured(R.color.orange), dead(R.color.red);

        private final int color;

        private InjuryState(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
        }

    }

    public static enum Experience {
        recruit(0), veteran(R.drawable.ic_veteran), elite(R.drawable.ic_elite);

        private final int image;

        private Experience(int image) {
            this.image = image;
        }

        public int getImage() {
            return image;
        }

    }

    public static enum Action {
        waiting, walking, running, firing, hiding, reloading, aiming
    }

    public InjuryState getHealth() {
        return health;
    }

    public void setHealth(InjuryState health) {
        this.health = health;
    }

    public int getMoveSpeed() {
        return moveSpeed;
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public void setWeapons(List<Weapon> weapons) {
        this.weapons = weapons;
    }

    public Experience getExperience() {
        return experience;
    }

    public void setExperience(Experience experience) {
        this.experience = experience;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Action getCurrentAction() {
        return currentAction;
    }

    public void setCurrentAction(Action currentAction) {
        this.currentAction = currentAction;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public int getFrags() {
        return frags;
    }

    public void setFrags(int frags) {
        this.frags = frags;
    }

    public int getImage() {
        return image;
    }

    public ArmiesData getArmy() {
        return army;
    }

    public int getRealSellPrice(boolean isSelling) {
        if (isSelling) {
            return (int) (getUnitPrice() * GameUtils.SELL_PRICE_FACTOR);
        } else {
            return getUnitPrice();
        }
    }

    /**
     * Create a new instance of Unit. Used when we buy a unit.
     * 
     * @param army
     * @return
     */
    public Unit copy() {
        Unit unit = null;
        if (this instanceof Soldier) {
            unit = new Soldier(army, name, image, experience, weapons, moveSpeed, spriteName, spriteScale);
        } else if (this instanceof Tank) {
            Vehicle vehicle = (Vehicle) this;
            unit = new Tank(army, name, image, experience, weapons, moveSpeed, vehicle.getArmor(), spriteName,
                    spriteScale);
        }
        return unit;
    }

    public void move() {
        this.currentAction = Action.walking;
        // update position
        MoveOrder moveOrder = (MoveOrder) order;
        updateUnitRotation(moveOrder.getXDestination(), moveOrder.getYDestination());
        float dx = moveOrder.getXDestination() - sprite.getX();
        float dy = moveOrder.getYDestination() - sprite.getY();
        double angle = Math.atan(dy / dx);
        float dd = moveSpeed * 10 * 0.04f * getUnitSpeed();
        boolean hasArrived = false;
        if (Math.sqrt(dx * dx + dy * dy) <= dd) {
            hasArrived = true;
            dd = (float) Math.sqrt(dx * dx + dy * dy);
        }

        float[] newPosition = GameUtils.getCoordinatesAfterTranslation(sprite.getX(), sprite.getY(), dd, angle, dx > 0);
        sprite.setPosition(newPosition[0], newPosition[1]);

        if (hasArrived) {
            setOrder(null);
        }
    }

    private void updateUnitRotation(float xDestination, float yDestination) {
        float dx = xDestination - sprite.getX();
        float dy = yDestination - sprite.getY();
        double angle = Math.atan(dy / dx);
        if (dx > 0) {
            sprite.setRotation((float) (angle * 180 / Math.PI + 90));
        } else {
            sprite.setRotation((float) (angle * 180 / Math.PI + 270));
        }
    }

    public void fire(Battle battle) {
        FireOrder f = (FireOrder) order;

        Unit target = f.getTarget();
        if (target.isDead() || !GameUtils.canSee(battle.getMap(), this, target)) {
            // if target is dead or is not visible anymore, stop to shoot
            order = new DefendOrder();
            return;
        }

        // get most suitable weapon
        Weapon weapon = getBestWeapon(battle, target);
        if (weapon != null) {
            if (weapon instanceof TurretWeapon) {
                // turrets take time to rotate
                TurretWeapon turret = (TurretWeapon) weapon;
                boolean isRotatingOver = turret.rotateTurret(sprite, target.getSprite().getX(), target.getSprite()
                        .getY());
                if (!isRotatingOver) {
                    return;
                }
            } else {
                updateUnitRotation(target.getSprite().getX(), target.getSprite().getY());
            }

            if (weapon.getReloadCounter() > 0) {
                if (aimCounter == 0) {
                    aimCounter = -10;
                    // aiming
                    this.currentAction = Action.aiming;
                } else if (aimCounter < 0) {
                    aimCounter++;
                    if (aimCounter == 0) {
                        aimCounter = weapon.getCadence();
                    }
                    // aiming
                    this.currentAction = Action.aiming;
                } else if (GameActivity.gameCounter % (11 - weapon.getShootSpeed()) == 0) {
                    // firing !!!

                    // add muzzle flash sprite
                    sprite.isFiring = true;

                    weapon.setAmmoAmount(weapon.getAmmoAmount() - 1);
                    weapon.setReloadCounter(weapon.getReloadCounter() - 1);
                    aimCounter--;
                    currentAction = Action.firing;

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
                this.currentAction = Action.reloading;
                weapon.setReloadCounter(-weapon.getReloadSpeed());
            } else {
                // reloading
                this.currentAction = Action.reloading;
                if (GameActivity.gameCounter % 12 == 0) {
                    weapon.setReloadCounter(weapon.getReloadCounter() + 1);
                    if (weapon.getReloadCounter() == 0) {
                        // reloading is over
                        weapon.setReloadCounter(weapon.getMagazineSize());
                    }
                }

            }
        } else {
            // no weapon available for this fire order
            this.order = new DefendOrder();
        }
    }

    public Weapon getBestWeapon(Battle battle, Unit target) {
        float distance = GameUtils.getDistanceBetween(this, target);
        boolean canSeeTarget = GameUtils.canSee(battle.getMap(), this, target);
        Weapon bestWeapon = null;
        for (Weapon weapon : weapons) {
            if (weapon.canUseWeapon(target, distance, canSeeTarget)) {
                if (bestWeapon == null || weapon.getEfficiencyAgainst(target) > bestWeapon.getEfficiencyAgainst(target)) {
                    bestWeapon = weapon;
                }
            }
        }

        return bestWeapon;
    }

    public void applyDamage(int damage) {
        health = InjuryState.values()[Math.min(InjuryState.dead.ordinal(), health.ordinal() + damage)];
    }

    public void hide() {
        this.currentAction = Action.hiding;

        if (GameActivity.gameCounter % 3 == 0) {
            if (this.panic > 0) {
                this.panic--;
            }
        }
    }

    public int getPanic() {
        return panic;
    }

    public void setPanic(int panic) {
        this.panic = panic;
    }

    public void resolveOrder(Battle battle) {
        if (this.panic > 0) {
            // test if the unit can react
            if (Math.random() * 10 + getExperience().ordinal() < this.panic) {
                // the unit is under fire
                hide();
                return;
            }
        }

        if (this.order instanceof MoveOrder) {
            // TODO update A*
        } else if (this.order instanceof DefendOrder) {
            // search for enemies
            for (Unit u : battle.getEnemies(this)) {
                if (!u.isDead() && GameUtils.canSee(battle.getMap(), this, u)) {
                    // fire on enemy
                    order = new FireOrder(u);
                    return;
                }
            }
            // stay ambush
            hide();
        } else if (this.order instanceof FireOrder) {
            fire(battle);
        }
    }

    public void takeInitiative() {
        order = new DefendOrder();
    }

    public int getAimCounter() {
        return aimCounter;
    }

    public void setAimCounter(int aimCounter) {
        this.aimCounter = aimCounter;
    }

    public boolean isDead() {
        return health == InjuryState.dead;
    }

    public void getShots(Unit shooter, Map map) {
        // increase panic
        if (panic < 10) {
            panic++;
        }

        // fight back
        if (order == null || order instanceof DefendOrder || order instanceof MoveOrder && Math.random() < 0.5) {
            if (GameUtils.canSee(map, this, shooter)) {
                order = new FireOrder(shooter);
            }
        }
    }

    public void killedSomeone() {
        // add frags
        frags++;
    }

    public void died(Battle battle) {
        sprite.setCanBeDragged(false);
        order = null;
        // draw sprite
        if (this instanceof Tank || weapons.size() > 0 && weapons.get(0) instanceof TurretWeapon) {
            // smoke
            battle.getOnNewSprite().drawAnimatedSprite(getSprite().getX(), getSprite().getY() - 70, "smoke.png", 120,
                    2.0f, 100, true);
        } else if (this instanceof Soldier) {
            // blood
            battle.getOnNewSprite().drawAnimatedSprite(getSprite().getX(), getSprite().getY(), "blood.png", 120, 0.6f,
                    0, false);
        }

    }

    public boolean canMove() {
        return moveSpeed > 0;
    }

}
