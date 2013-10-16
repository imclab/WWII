package com.glevel.wwii.game.model.units;

import java.util.List;

import com.glevel.wwii.R;
import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.data.ArmiesData;
import com.glevel.wwii.game.model.GameElement;
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
        waiting, walking, running, firing, hiding, reloading
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

    public void move(boolean isRunning) {
        this.currentAction = (isRunning ? Action.running : Action.walking);
        // TODO
        // update position
        MoveOrder moveOrder = (MoveOrder) order;
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
            sprite.setRotation((float) (angle * 180 / Math.PI + 90));
        } else {
            sprite.setPosition((float) (sprite.getX() - dd * Math.cos(angle)),
                    (float) (sprite.getY() + dd * Math.sin(angle + Math.PI)));
            sprite.setRotation((float) (angle * 180 / Math.PI + 270));
        }

        if (hasArrived) {
            setOrder(null);
        }
    }

    public void fire() {
        this.currentAction = Action.firing;
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

}
