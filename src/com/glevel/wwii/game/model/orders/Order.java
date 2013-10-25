package com.glevel.wwii.game.model.orders;

import java.io.Serializable;

import com.glevel.wwii.game.model.units.Unit;

public abstract class Order implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8153599419219936688L;
    protected Unit unit;

}
