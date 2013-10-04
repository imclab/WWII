package com.glevel.wwii.game.model.orders;

import com.glevel.wwii.game.model.Order;
import com.glevel.wwii.game.model.armies.Unit;

public class DefendOrder extends Order {

	public DefendOrder(Unit unit) {
		this.unit = unit;
	}

}
