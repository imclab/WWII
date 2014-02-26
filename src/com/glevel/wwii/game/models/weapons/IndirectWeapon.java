package com.glevel.wwii.game.models.weapons;

import com.glevel.wwii.game.models.map.Tile.TerrainType;
import com.glevel.wwii.game.models.units.Unit;

public class IndirectWeapon extends DeflectionWeapon {

    /**
     * 
     */
    private static final long serialVersionUID = 2274584769324690466L;

    public IndirectWeapon(int name, int image, int apPower, int atPower, int range, int nbMagazines, int cadence,
            int magazineSize, int reloadSpeed, int shootSpeed, int explosionSize) {
        super(name, image, apPower, atPower, range, nbMagazines, cadence, magazineSize, reloadSpeed, shootSpeed,
                explosionSize);
    }

    @Override
    protected void resolveDamageDiceRoll(int tohit, Unit target) {
        // mortars are useless on targets in houses
        if (range > 200 && target.getTilePosition().getTerrain() == TerrainType.house) {
            return;
        }
        
        super.resolveDamageDiceRoll(tohit, target);
    }

}
