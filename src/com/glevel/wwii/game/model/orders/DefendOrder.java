package com.glevel.wwii.game.model.orders;

import com.glevel.wwii.game.model.units.Unit;

public class DefendOrder extends Order {

    /**
     * 
     */
    private static final long serialVersionUID = 4397286734981789298L;

    public DefendOrder(Unit unit) {
		this.unit = unit;
	}

}
