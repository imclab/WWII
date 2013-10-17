package com.glevel.wwii.game.model.orders;

import com.glevel.wwii.game.model.units.Unit;

public class FireOrder extends Order {

    private int xDestination;
    private int yDestination;
    private Unit target;
    private int aimCounter;

    public FireOrder(Unit unit, Unit target) {
        this.unit = unit;
        this.target = target;
        this.setAimCounter(0);
    }

    public FireOrder(Unit unit, int xDestination, int yDestination) {
        this.unit = unit;
        this.xDestination = xDestination;
        this.yDestination = yDestination;
        this.setAimCounter(0);
    }

    public int getxDestination() {
        return xDestination;
    }

    public void setxDestination(int xDestination) {
        this.xDestination = xDestination;
    }

    public int getyDestination() {
        return yDestination;
    }

    public void setyDestination(int yDestination) {
        this.yDestination = yDestination;
    }

    public Unit getTarget() {
        return target;
    }

    public void setTarget(Unit target) {
        this.target = target;
    }

    public int getAimCounter() {
        return aimCounter;
    }

    public void setAimCounter(int aimCounter) {
        this.aimCounter = aimCounter;
    }

}
