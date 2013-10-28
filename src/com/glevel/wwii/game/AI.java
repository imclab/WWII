package com.glevel.wwii.game;

import com.glevel.wwii.game.model.Battle;
import com.glevel.wwii.game.model.orders.FireOrder;
import com.glevel.wwii.game.model.orders.MoveOrder;
import com.glevel.wwii.game.model.units.Unit;

public class AI {

    public static void updateUnitOrder(Battle battle, Unit unit) {

        for (Unit u : battle.getEnemies(unit)) {
            if (!u.isDead() && GameUtils.getDistanceBetween(unit, u) < 30 * GameUtils.PIXEL_BY_METER
                    && GameUtils.canSee(battle.getMap(), unit, u)) {
                unit.setOrder(new FireOrder(u));
                return;
            }
        }

        unit.setOrder(new MoveOrder((float) Math.random() * 1000, (float) Math.random() * 1000));

    }

}
