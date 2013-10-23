package com.glevel.wwii.game.model.weapons;

import com.glevel.wwii.game.GameUtils;
import com.glevel.wwii.game.model.Battle;
import com.glevel.wwii.game.model.Player;
import com.glevel.wwii.game.model.units.Unit;
import com.glevel.wwii.game.model.units.Unit.Action;

public class DeflectionWeapon extends Weapon {

    private static final float EXPLOSION_EPICENTER_FACTOR = 0.5f;
    private static final int CHANCE_TO_HIT_IN_EPICENTER = 100;
    private static final int CHANCE_TO_HIT_AROUND = 50;
    private static final int BASIC_MAXIMAL_DEFLECTION = 5;// in meters

    private final int explosionSize;

    public DeflectionWeapon(int name, int image, int apPower, int atPower, int range, int nbMagazines, int cadence,
            int magazineSize, int reloadSpeed, int shootSpeed, int explosionSize) {
        super(name, image, apPower, atPower, range, nbMagazines, cadence, magazineSize, reloadSpeed, shootSpeed);
        this.explosionSize = explosionSize;
    }

    @Override
    public void resolveFireShot(Battle battle, Unit shooter, Unit target) {
        float distance = GameUtils.getDistanceBetween(shooter, target);

        // deflection depends on distance and experience of the shooter
        float deflection = (int) (Math.random() * (BASIC_MAXIMAL_DEFLECTION + Weapon.distanceToRangeCategory(distance) - shooter
                .getExperience().ordinal())) * GameUtils.PIXEL_BY_METER;
        double angle = Math.random() * 360;

        // calculate impact position
        float[] impactPosition = GameUtils.getCoordinatesAfterTranslation(target.getSprite().getX(), target.getSprite()
                .getY(), deflection, angle, Math.random() < 0.5);

        // draw explosion sprite
        battle.getOnNewSprite().drawSprite(impactPosition[0], impactPosition[1], "explosion.png",
                3 * GameUtils.GAME_LOOP_FREQUENCY);

        // get all the units in the explosion
        float distanceToImpact;// in meters
        for (Player p : battle.getPlayers()) {
            for (Unit u : p.getUnits()) {
                distanceToImpact = GameUtils.getDistanceBetween(u, impactPosition) / GameUtils.PIXEL_BY_METER;
                if (distanceToImpact < explosionSize) {
                    if (distanceToImpact < explosionSize * EXPLOSION_EPICENTER_FACTOR) {
                        // great damage in the explosion's epicenter
                        resolveDamageDiceRoll(CHANCE_TO_HIT_IN_EPICENTER, target);
                    } else {
                        // minor damage further
                        int tohit = CHANCE_TO_HIT_AROUND;

                        // add terrain protection
                        tohit *= target.getUnitTerrainProtection();

                        if (target.getCurrentAction() == Action.hiding || target.getCurrentAction() == Action.reloading) {
                            // target is hiding : tohit depends on target's
                            // experience
                            tohit -= 5 * (target.getExperience().ordinal() + 1);
                        }

                        resolveDamageDiceRoll(tohit, target);
                    }
                }
            }
        }
    }

    public int getExplosionSize() {
        return explosionSize;
    }

}
