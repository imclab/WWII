package com.glevel.wwii.game.model.units;

import java.util.List;

import org.andengine.extension.tmx.TMXLayer;

import com.glevel.wwii.R;
import com.glevel.wwii.activities.GameActivity;
import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.logic.FightLogic;
import com.glevel.wwii.game.model.Battle;
import com.glevel.wwii.game.model.GameElement;
import com.glevel.wwii.game.model.orders.DefendOrder;
import com.glevel.wwii.game.model.orders.FireOrder;
import com.glevel.wwii.game.model.orders.MoveOrder;
import com.glevel.wwii.game.model.orders.Order;

public abstract class Unit extends GameElement {

    protected final ArmiesData army;
    private final int image;
    private final int moveSpeed;
    private List<Weapon> weapons;
    protected Experience experience;

    private int requisitionPrice;
    private InjuryState health;
    private int frags;
    private boolean isAvailable;
    private Order order;
    private Action currentAction;
    private int panic;
    private int aimCounter = 0;

    public Unit(ArmiesData army, int name, int image, Experience experience, List<Weapon> weapons, int moveSpeed) {
        super(name, "soldier.png");
        this.army = army;
        this.image = image;
        this.experience = experience;
        this.weapons = weapons;
        this.moveSpeed = moveSpeed;
        this.health = InjuryState.none;
        this.currentAction = Action.waiting;
        this.setPanic(0);
        this.frags = 0;
        this.requisitionPrice = calculateUnitPrice();
    }

    protected abstract float getUnitSpeed();

    protected abstract float getUnitTerrainProtection();

    private int calculateUnitPrice() {
        int basePrice = 5;
        switch (experience) {
        case recruit:
            basePrice *= 0.5;
            break;
        case elite:
            basePrice *= 2.5;
            break;
        }
        // TODO
        return basePrice;
    }

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
        recruit(R.color.recruit), veteran(R.color.veteran), elite(R.color.elite);

        private final int color;

        private Experience(int color) {
            this.color = color;
        }

        public int getColor() {
            return color;
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

    public int getRequisitionPrice() {
        return requisitionPrice;
    }

    public void setRequisitionPrice(int requisitionPrice) {
        this.requisitionPrice = requisitionPrice;
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
            return (int) (requisitionPrice * GameUtils.SELL_PRICE_FACTOR);
        } else {
            return requisitionPrice;
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
            unit = new Soldier(army, name, image, experience, weapons, moveSpeed);
        } else if (this instanceof Tank) {
            Vehicle vehicle = (Vehicle) this;
            unit = new Tank(army, name, image, experience, weapons, moveSpeed, vehicle.getArmor());
        }
        return unit;
    }

    public void move() {
        this.currentAction = Action.walking;
        // TODO
        // update position
        MoveOrder moveOrder = (MoveOrder) order;
        updateUnitRotation(moveOrder.getxDestination(), moveOrder.getyDestination());
        float dx = moveOrder.getxDestination() - sprite.getX();
        float dy = moveOrder.getyDestination() - sprite.getY();
        double angle = Math.atan(dy / dx);
        float dd = moveSpeed * 10 * 0.1f * getUnitSpeed();
        boolean hasArrived = false;
        if (Math.sqrt(dx * dx + dy * dy) <= dd) {
            hasArrived = true;
            dd = (float) Math.sqrt(dx * dx + dy * dy);
        }
        if (dx > 0) {
            sprite.setPosition((float) (sprite.getX() + dd * Math.cos(angle)),
                    (float) (sprite.getY() + dd * Math.sin(angle)));
        } else {
            sprite.setPosition((float) (sprite.getX() - dd * Math.cos(angle)),
                    (float) (sprite.getY() + dd * Math.sin(angle + Math.PI)));
        }

        if (hasArrived) {
            setOrder(null);
        }
    }

    private void updateUnitRotation(float xDestination, float yDestintation) {
        float dx = xDestination - sprite.getX();
        float dy = yDestintation - sprite.getY();
        double angle = Math.atan(dy / dx);
        if (dx > 0) {
            sprite.setRotation((float) (angle * 180 / Math.PI + 90));
        } else {
            sprite.setRotation((float) (angle * 180 / Math.PI + 270));
        }
    }

    public void fire() {
        FireOrder f = (FireOrder) order;

        // get weapon // TODO
        Weapon weapon = getWeapons().get(0);

        if (weapon.getAmmoAmount() > 0) {
            Unit target = f.getTarget();
            if (target.getHealth() == InjuryState.dead) {
                // if target is dead, stop to shoot
                setOrder(new DefendOrder(this));
            }

            updateUnitRotation(target.getSprite().getX(), target.getSprite().getY());

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
                    getSprite().isFiring = true;

                    weapon.setAmmoAmount(weapon.getAmmoAmount() - 1);
                    weapon.setReloadCounter(weapon.getReloadCounter() - 1);
                    aimCounter--;
                    this.currentAction = Action.firing;

                    // if not a lot of ammo, more aiming !
                    if (weapon.getAmmoAmount() == weapon.getMagazineSize() * 2) {
                        weapon.setCadence(Math.max(1, weapon.getCadence() / 2));
                    }

                    // increase target panic // TODO depends on experience
                    if (target.getPanic() < 10) {
                        target.setPanic(target.getPanic() + 1);
                    }

                    // does it touch the target ?
                    if (target.getHealth() != InjuryState.dead) {

                        float distance = GameUtils.getDistanceBetween(target, this);
                        int tohit = FightLogic.HIT_CHANCES[getExperience().ordinal()][FightLogic
                                .distanceToRangeCategory(distance)];

                        // add terrain protection
                        tohit *= target.getUnitTerrainProtection();

                        if (target.getCurrentAction() == Action.hiding || target.getCurrentAction() == Action.reloading) {
                            // target is hiding : tohit depends on target's
                            // experience
                            tohit -= 5 * (target.getExperience().ordinal() + 1);
                        }

                        // tohit depends on weapon range
                        if (distance > weapon.getRange() / 2) {
                            tohit -= 10;
                        }

                        int diceRoll = (int) (Math.random() * 100);
                        if (diceRoll < tohit) {
                            // hit !
                            if (diceRoll < tohit / 4) {
                                // critical !
                                target.setHealth(InjuryState.dead);
                            } else if (diceRoll < tohit / 2) {
                                // heavy !
                                target.applyDamage(2);
                            } else {
                                if (Math.random() < 0.5) {
                                    // light injured
                                    target.applyDamage(1);
                                } else {
                                    // nothing
                                }
                            }

                            if (target.getHealth() == InjuryState.dead) {
                                // target is dead
                                target.getSprite().setCanBeDragged(false);
                                target.setOrder(null);

                                // add frag
                                setFrags(getFrags() + 1);
                            }
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
            // no ammo left
            setOrder(new DefendOrder(this));
        }
    }

    private void applyDamage(int damage) {
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

    public void resolveOrder(Battle battle, TMXLayer tmxLayer) {
        if (this.panic > 0) {
            // test if the unit can react
            if (Math.random() * 10 + getExperience().ordinal() < this.panic) {
                // the unit is hiding
                hide();
                return;
            }
        }

        if (this.order instanceof MoveOrder) {
            // TODO update A*
        } else if (this.order instanceof DefendOrder) {
            for (Unit u : battle.getEnemies(this)) {
                if (u.getHealth() != InjuryState.dead && GameUtils.canSee(battle.getMap(), this, u, tmxLayer)) {
                    setOrder(new FireOrder(this, u));
                    return;
                }
            }
            hide();
        } else if (this.order instanceof FireOrder) {
            fire();
        }
    }

    public void takeInitiative() {
        setOrder(new DefendOrder(this));
    }

    public int getAimCounter() {
        return aimCounter;
    }

    public void setAimCounter(int aimCounter) {
        this.aimCounter = aimCounter;
    }
}
