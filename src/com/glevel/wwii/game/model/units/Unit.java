package com.glevel.wwii.game.model.units;

import java.util.List;

import com.glevel.wwii.R;
import com.glevel.wwii.activities.GameActivity;
import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.logic.FightLogic;
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

    public static enum Rank {
        ally, enemy, neutral
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
        float dd = moveSpeed * 200 * 0.1f;
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
            updateUnitRotation(f.getTarget().getSprite().getX(), f.getTarget().getSprite().getY());

            if (weapon.getReloadCounter() > 0) {
                if (f.getAimCounter() == 0) {
                    f.setAimCounter(-10);
                    // aiming
                    this.currentAction = Action.aiming;
                } else if (f.getAimCounter() < 0) {
                    f.setAimCounter(f.getAimCounter() + 1);
                    if (f.getAimCounter() == 0) {
                        f.setAimCounter(weapon.getCadence());
                    }
                    // aiming
                    this.currentAction = Action.aiming;
                } else if (GameActivity.gameCounter % (11 - weapon.getShootSpeed()) == 0) {
                    // firing !!!

                    // add muzzle flash sprite
                    getSprite().isFiring = true;

                    weapon.setAmmoAmount(weapon.getAmmoAmount() - 1);
                    weapon.setReloadCounter(weapon.getReloadCounter() - 1);
                    f.setAimCounter(f.getAimCounter() - 1);
                    this.currentAction = Action.firing;

                    // if not a lot of ammo, more aiming !
                    if (weapon.getAmmoAmount() == weapon.getMagazineSize() * 2) {
                        weapon.setCadence(weapon.getCadence() / 2);
                    }

                    Unit target = f.getTarget();
                    // increase target panic // TODO depends on experience
                    if (target.getPanic() < 10) {
                        target.setPanic(target.getPanic() + 1);
                    }

                    // does it touch the target ?

                    float distance = GameUtils.getDistanceBetween(target, this);
                    // TODO add protection
                    int tohit = FightLogic.HIT_CHANCES[getExperience().ordinal()][FightLogic
                            .distanceToRangeCategory(distance)];
                    if (target.getCurrentAction() == Action.hiding || target.getCurrentAction() == Action.reloading) {
                        // target is hiding
                        tohit -= 10;
                    }

                    // TODO tohit depends on target's experience

                    int diceRoll = (int) (Math.random() * 100);
                    if (diceRoll < tohit) {
                        // hit !
                        if (diceRoll < tohit / 4) {
                            // critical !
                            target.setHealth(InjuryState.dead);
                        } else if (diceRoll < tohit / 2) {
                            // heavy !
                            target.setHealth(InjuryState.values()[Math.min(InjuryState.dead.ordinal(), target
                                    .getHealth().ordinal() + 2)]);
                        } else {
                            if (Math.random() < 0.5) {
                                // light injured
                                target.setHealth(InjuryState.values()[Math.min(InjuryState.dead.ordinal(), target
                                        .getHealth().ordinal() + 1)]);
                            } else {
                                // nothing
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

    public void hide() {
        this.currentAction = Action.hiding;

        if (this.panic > 0) {
            this.panic--;
        }
    }

    public int getPanic() {
        return panic;
    }

    public void setPanic(int panic) {
        this.panic = panic;
    }

    public void resolveOrder() {
        if (this.panic > 0) {
            // test if the unit can react
            if (this.experience.ordinal() < this.panic) {
                // the unit is hiding
                hide();
                return;
            }
        }

        if (this.order instanceof MoveOrder) {
            // unit is moving
            move();
        } else if (this.order instanceof DefendOrder) {
            // TODO check if there are enemies to be shot
            hide();
        } else if (this.order instanceof FireOrder) {
            // TODO check if unit can still shoot on enemy
            fire();
            // this.order = new DefendOrder(this);
        }
    }

    public void takeInitiative() {
        // TODO
        // unit.setOrder(new DefendOrder(unit));
        // resolveUnitOrder(unit);
    }

}
