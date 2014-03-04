package com.glevel.wwii.game.models.weapons;

import com.glevel.wwii.game.graphics.TankSprite;
import com.glevel.wwii.game.graphics.UnitSprite;
import com.glevel.wwii.game.models.weapons.categories.DeflectionWeapon;

public class Turret extends DeflectionWeapon {

    /**
     * 
     */
    private static final long serialVersionUID = -2063295813207549974L;
    private int turretRotationSpeed;

    public Turret(int name, int image, int apPower, int atPower, int range, int nbMagazines, int cadence,
            int magazineSize, int reloadSpeed, int shootSpeed, int explosionSize, int turretRotationSpeed) {
        super(name, image, apPower, atPower, range, nbMagazines, cadence, magazineSize, reloadSpeed, shootSpeed,
                explosionSize);
        this.setTurretRotationSpeed(turretRotationSpeed);
    }

    public int getTurretRotationSpeed() {
        return turretRotationSpeed;
    }

    public void setTurretRotationSpeed(int turretRotationSpeed) {
        this.turretRotationSpeed = turretRotationSpeed;
    }

    public boolean rotateTurret(UnitSprite sprite, float xDestination, float yDestination) {
        // calculate final rotation and rotation step direction
        float dx = xDestination - sprite.getX();
        float dy = yDestination - sprite.getY();
        double finalAngle = Math.atan(dy / dx) * 180 / Math.PI;
        if (dx > 0) {
            finalAngle += 90;
        } else {
            finalAngle -= 90;
        }
        double rotationStep = 0.0;

        // rotate turret
        if (sprite instanceof TankSprite) {
            TankSprite tankSprite = (TankSprite) sprite;
            double dTau = finalAngle - tankSprite.getTurretSpriteRotation() - tankSprite.getRotation();
            if (dTau > 0) {
                rotationStep = Math.min(dTau, turretRotationSpeed);
            } else if (dTau < 0) {
                rotationStep = Math.max(dTau, -turretRotationSpeed);
            }
            tankSprite.setTurretSpriteRotation((float) (tankSprite.getTurretSpriteRotation() + rotationStep));
        } else {
            double dTau = finalAngle - sprite.getRotation();
            if (dTau > 0) {
                rotationStep = Math.min(dTau, turretRotationSpeed);
            } else if (dTau < 0) {
                rotationStep = Math.max(dTau, -turretRotationSpeed);
            }
            sprite.setRotation((float) (sprite.getRotation() + rotationStep));
        }

        return Math.abs(rotationStep) < turretRotationSpeed;
    }

}
